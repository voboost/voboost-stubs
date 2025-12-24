import { FridaTestHelper } from './test-frida-helper.js';

/**
 * ProcessManager - Manages process lifecycle and isolation for tests
 *
 * This class solves the critical test isolation problem by:
 * 1. Preventing race conditions during process cleanup
 * 2. Enabling process reuse across tests in the same file
 * 3. Providing proper locking mechanism to prevent concurrent access
 * 4. Centralizing cleanup logic to avoid aggressive pkill commands
 *
 * @example
 * // In test file
 * test.serial('my test', async t => {
 *     const stub = await ProcessManager.acquireProcess('launcher');
 *     try {
 *         // Run test with stub
 *     } finally {
 *         ProcessManager.releaseProcess('launcher');
 *     }
 * });
 *
 * // In test.after.always hook
 * test.after.always(async () => {
 *     await ProcessManager.cleanupAll();
 * });
 */
export class ProcessManager {
    // Private static fields for managing process instances and locks
    static #instances = new Map();
    static #locks = new Map();
    static #lockWaiters = new Map();

    // Configuration
    static LOCK_TIMEOUT = 30000; // 30 seconds max wait for lock
    static LOCK_CHECK_INTERVAL = 100; // Check every 100ms

    /**
     * Acquires a process for testing, with locking to prevent concurrent access
     * @param {string} processName - Name of the process (e.g., 'launcher', 'bluetoothphone')
     * @param {Object} [options={}] - Acquisition options
     * @param {boolean} [options.reuse=true] - Whether to reuse existing process
     * @param {number} [options.timeout=LOCK_TIMEOUT] - Maximum time to wait for lock
     * @returns {Promise<Object>} Stub process object
     * @throws {Error} When lock timeout is exceeded or process fails to start
     */
    static async acquireProcess(processName, options = {}) {
        const { reuse = true, timeout = this.LOCK_TIMEOUT } = options;

        // Wait for lock with timeout
        const lockAcquired = await this.#waitForLock(processName, timeout);
        if (!lockAcquired) {
            throw new Error(
                `Failed to acquire lock for process ${processName} within ${timeout}ms`
            );
        }

        try {
            // Reuse existing process if available and reuse is enabled
            if (reuse && this.#instances.has(processName)) {
                const stub = this.#instances.get(processName);
                // Verify process is still alive
                if (stub.process && !stub.process.killed) {
                    return stub;
                } else {
                    // Process died, remove it and start new one
                    this.#instances.delete(processName);
                }
            }

            // Start new process
            const stub = await FridaTestHelper.startStub(processName);
            this.#instances.set(processName, stub);

            return stub;
        } catch (error) {
            // Release lock on error
            this.#releaseLock(processName);
            throw error;
        }
    }

    /**
     * Releases the lock on a process, allowing other tests to use it
     * @param {string} processName - Name of the process to release
     */
    static releaseProcess(processName) {
        this.#releaseLock(processName);
    }

    /**
     * Checks if a process is currently locked
     * @param {string} processName - Name of the process to check
     * @returns {boolean} True if process is locked
     */
    static isLocked(processName) {
        return this.#locks.get(processName) === true;
    }

    /**
     * Gets the current stub instance for a process (if any)
     * @param {string} processName - Name of the process
     * @returns {Object|null} Stub instance or null if not running
     */
    static getInstance(processName) {
        return this.#instances.get(processName) || null;
    }

    /**
     * Stops a specific process and removes it from the pool
     * @param {string} processName - Name of the process to stop
     * @returns {Promise<void>}
     */
    static async stopProcess(processName) {
        const stub = this.#instances.get(processName);
        if (stub) {
            try {
                // Add timeout to prevent hanging
                await Promise.race([
                    stub.stop(),
                    new Promise((_, reject) =>
                        setTimeout(() => reject(new Error('Process stop timeout')), 5000)
                    ),
                ]);
            } catch {
                // Force kill if stop times out
                if (stub.process && !stub.process.killed) {
                    stub.process.kill('SIGKILL');
                    await new Promise((resolve) => setTimeout(resolve, 1000));
                }
            }
            this.#instances.delete(processName);
        }
        this.#releaseLock(processName);
    }

    /**
     * Cleans up all managed processes
     * Delegates to FridaTestHelper for unified cleanup logic
     * This should be called in test.after.always hooks
     * @returns {Promise<void>}
     */
    static async cleanupAll() {
        if (process.env.DEBUG) {
            console.log('[DEBUG] ProcessManager.cleanupAll() called');

            console.log('[DEBUG] Active processes:', Array.from(this.#instances.keys()));
        }

        // Force kill any remaining managed processes
        const cleanupPromises = [];
        for (const [processName, stub] of this.#instances.entries()) {
            if (process.env.DEBUG) {
                console.log(`[DEBUG] Force killing managed process: ${processName}`);
            }

            cleanupPromises.push(
                new Promise((resolve) => {
                    try {
                        if (stub.process && !stub.process.killed) {
                            stub.process.kill('SIGKILL');
                        }
                    } catch {
                        // Ignore
                    }
                    setTimeout(resolve, 500);
                })
            );
        }

        await Promise.all(cleanupPromises);

        // Delegate to FridaTestHelper for unified cleanup
        await FridaTestHelper.cleanupAllProcesses();

        // Clear ProcessManager state
        this.#instances.clear();
        this.#locks.clear();
        this.#lockWaiters.clear();

        if (process.env.DEBUG) {
            console.log('[DEBUG] ProcessManager.cleanupAll() completed');
        }
    }

    /**
     * Resets all state (useful for testing the ProcessManager itself)
     * @returns {Promise<void>}
     */
    static async reset() {
        await this.cleanupAll();
    }

    /**
     * Gets statistics about managed processes
     * @returns {Object} Statistics object
     */
    static getStats() {
        return {
            activeProcesses: this.#instances.size,
            lockedProcesses: Array.from(this.#locks.entries())
                .filter(([,]) => true)
                .map(([name]) => name),
            processes: Array.from(this.#instances.keys()),
        };
    }

    /**
     * Waits for a lock to become available
     * @param {string} processName - Name of the process
     * @param {number} timeout - Maximum time to wait
     * @returns {Promise<boolean>} True if lock was acquired
     * @private
     */
    static async #waitForLock(processName, timeout) {
        const startTime = Date.now();

        while (this.#locks.get(processName) === true) {
            if (Date.now() - startTime > timeout) {
                return false;
            }
            await new Promise((resolve) => setTimeout(resolve, this.LOCK_CHECK_INTERVAL));
        }

        // Acquire lock
        this.#locks.set(processName, true);
        return true;
    }

    /**
     * Releases a lock
     * @param {string} processName - Name of the process
     * @private
     */
    static #releaseLock(processName) {
        this.#locks.set(processName, false);
    }
}

export default ProcessManager;
