import { spawn } from 'child_process';
import { promisify } from 'util';
import { exec } from 'child_process';
import { dirname, join } from 'path';
import { fileURLToPath } from 'url';
import { access, constants } from 'fs/promises';
import { Buffer } from 'buffer';
import { AGENT_LOG_SEQUENCES, AGENT_TEST_CONFIGS } from './test-fixtures.js';

const execAsync = promisify(exec);
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

/**
 * FridaTestHelper - Comprehensive test utilities for Frida-based testing
 * Provides static methods for process management, script injection, and test validation
 */
export class FridaTestHelper {
    // Track all spawned processes for cleanup
    static #activeProcesses = new Set();

    // Static constants for timeouts and configurations
    static TIMEOUTS = {
        PROCESS_WAIT: 5000,
        INJECTION: 10000,
        INJECTION_MIN: 12000, // Increased further to ensure agent has time to start
        CLEANUP_DELAY: 300,
        STUB_INITIALIZATION: 3000,
        OUTPUT_CHECK_INTERVAL: 50,
        AUTO_STOP_DELAY: 5000,
        KILL_DELAY: 300,
    };

    static MAPPINGS = {
        JAR_FILE: {
            launcher: 'LauncherStub.jar',
            bluetoothphone: 'BluetoothPhoneStub.jar',
            systemservice: 'SystemServiceStub.jar',
            qgime: 'QgimeStub.jar',
            vehiclesetting: 'VehicleSettingStub.jar',
        },
        JAVA_CLASS: {
            launcher: 'com.qinggan.app.launcher.LauncherStub',
            bluetoothphone: 'com.qinggan.bluetoothphone.BluetoothPhoneStub',
            systemservice: 'com.qinggan.systemservice.SystemServiceStub',
            qgime: 'com.qinggan.app.qgime.QgimeStub',
            vehiclesetting: 'com.qinggan.app.vehiclesetting.VehicleSettingStub',
        },
        PROCESS_NAME: {
            launcher: 'com.qinggan.app.launcher',
            bluetoothphone: 'com.qinggan.bluetoothphone',
            systemservice: 'com.qinggan.systemservice',
            qgime: 'com.qinggan.app.qgime',
            vehiclesetting: 'com.qinggan.app.vehiclesetting',
        },
        AGENT_SCRIPT: {
            'weather-widget-mod': 'weather-widget-mod.js',
            'app-launcher-mod': 'app-launcher-mod.js',
            'navbar-launcher-mod': 'navbar-launcher-mod.js',
            'app-viewport-mod': 'app-viewport-mod.js',
            'phone-num-mod': 'phone-num-mod.js',
            'app-multi-display': 'app-multi-display-mod.js',
            'voboost-to-menu-mod': 'voboost-to-menu-mod.js',
            'forced-ev-mod': 'forced-ev-mod.js',
            'keyboard-ru-mod': 'keyboard-ru-mod.js',
            'keyboard-lock-en-mod': 'keyboard-lock-en-mod.js',
            'ADAS-activation-mod': 'ADAS-activation-mod.js',
            'low-speed-sound-mod': 'low-speed-sound-mod.js',
            'media-source-mod': 'media-source-mod.js',
            'media-window-mod': 'media-window-mod.js',
        },
    };

    /**
     * Validates input parameters and throws TypeError with descriptive messages
     * @param {string} paramName - Name of the parameter being validated
     * @param {any} value - Value to validate
     * @param {string} expectedType - Expected type (e.g., 'string', 'number', 'object')
     * @param {boolean} [allowNull=false] - Whether null values are allowed
     * @throws {TypeError} When validation fails
     */
    static _validateParam(paramName, value, expectedType, allowNull = false) {
        if (value === null || value === undefined) {
            if (!allowNull) {
                throw new TypeError(
                    `Parameter '${paramName}' cannot be null or undefined. Expected ${expectedType}.`
                );
            }
            return;
        }

        const actualType = Array.isArray(value) ? 'array' : typeof value;
        if (actualType !== expectedType) {
            throw new TypeError(
                `Parameter '${paramName}' must be of type ${expectedType}, but received ${actualType}.`
            );
        }
    }

    /**
     * Creates an enhanced error message with troubleshooting guidance
     * @param {string} message - Base error message
     * @param {string} [troubleshooting] - Troubleshooting guidance
     * @param {Error} [originalError] - Original error that caused this issue
     * @returns {Error} Enhanced error with troubleshooting info
     */
    static _createEnhancedError(message, troubleshooting = '', originalError = null) {
        const fullMessage = troubleshooting
            ? `${message}\n\nTroubleshooting: ${troubleshooting}`
            : message;
        const error = new Error(fullMessage);
        if (originalError) {
            error.cause = originalError;
        }
        return error;
    }

    /**
     * Validates that a file exists at the specified path
     * @param {string} filePath - Path to the file to validate
     * @throws {Error} When file doesn't exist
     */
    static async _validateFileExists(filePath) {
        try {
            await access(filePath, constants.F_OK);
        } catch (error) {
            throw this._createEnhancedError(
                `Agent script file not found: ${filePath}`,
                'Ensure the build process has completed and the script exists in the build directory. Try running `npm run build` first.',
                error
            );
        }
    }

    /**
     * Manages output buffering with memory leak prevention
     */
    static _createOutputBuffer(maxSize = 1024 * 1024) {
        // 1MB default limit
        const buffer = [];
        let totalSize = 0;

        return {
            add: (data) => {
                const dataSize = Buffer.byteLength(data, 'utf8');
                if (totalSize + dataSize > maxSize) {
                    // Remove oldest chunks to prevent memory leaks
                    while (buffer.length > 0 && totalSize + dataSize > maxSize) {
                        const removed = buffer.shift();
                        totalSize -= Buffer.byteLength(removed, 'utf8');
                    }
                }
                buffer.push(data);
                totalSize += dataSize;
            },
            get: () => buffer.join(''),
            clear: () => {
                buffer.length = 0;
                totalSize = 0;
            },
            size: () => totalSize,
        };
    }

    /**
     * Waits for a process to become available
     * @param {string} processName - Name of the process to wait for
     * @param {number} [timeout=TIMEOUTS.PROCESS_WAIT] - Maximum time to wait in milliseconds
     * @returns {Promise<boolean>} True if process is found, throws error otherwise
     * @throws {TypeError} When parameters are invalid
     * @throws {Error} When process is not found within timeout
     */
    static async waitForProcess(processName, timeout = this.TIMEOUTS.PROCESS_WAIT) {
        this._validateParam('processName', processName, 'string');
        this._validateParam('timeout', timeout, 'number');

        const start = Date.now();
        while (Date.now() - start < timeout) {
            try {
                // For Java processes, look for java processes and check by class name
                const { stdout } = await execAsync(`ps aux | grep java | grep -v grep`);
                if (stdout.includes(processName)) {
                    return true;
                }

                // Also check frida-ps for java processes
                const { stdout: fridaOutput } = await execAsync(`frida-ps | grep java`);
                if (fridaOutput.includes('java')) {
                    return true;
                }
            } catch {
                // Process not found yet, continue waiting
            }
            await new Promise((resolve) =>
                setTimeout(resolve, this.TIMEOUTS.OUTPUT_CHECK_INTERVAL)
            );
        }

        throw this._createEnhancedError(
            `Process ${processName} not found after ${timeout}ms`,
            '1. Ensure the target application is running\n2. Check if the process name is correct\n3. Verify Frida has proper permissions\n4. Try running `frida-ps` to see available processes'
        );
    }

    /**
     * Injects a Frida script into a target process BY PID
     * @param {Object} options - Injection options
     * @param {number} options.pid - Target process PID (preferred)
     * @param {string} [options.process] - Target process name (fallback)
     * @param {string} options.script - Path to the Frida script
     * @param {Object|null} [options.params=null] - Parameters to pass to the script
     * @param {number} [options.timeout=TIMEOUTS.INJECTION] - Injection timeout in milliseconds
     * @returns {Promise<Object>} Injection result with success status, output, and control methods
     * @throws {TypeError} When parameters are invalid
     * @throws {Error} When injection fails
     */
    static async inject({
        pid,
        process: processName,
        script,
        params = null,
        timeout = this.TIMEOUTS.INJECTION,
    }) {
        // Validate we have either PID or process name
        if (!pid && !processName) {
            throw new TypeError('Either pid or process must be provided');
        }

        this._validateParam('script', script, 'string');
        this._validateParam('params', params, 'object', true);
        this._validateParam('timeout', timeout, 'number');

        if (pid !== undefined) {
            this._validateParam('pid', pid, 'number');
        }
        if (processName !== undefined) {
            this._validateParam('process', processName, 'string');
        }

        // Validate script file exists
        await this._validateFileExists(script);

        // Wait a moment for process to be available
        await new Promise((resolve) => setTimeout(resolve, 1000));

        // Build frida-inject command args (matching voboost Android implementation)
        // Uses Script.parameters - the ONLY way to pass parameters
        const args = [];
        if (pid) {
            args.push('-p', String(pid));
        } else {
            args.push('-n', processName);
        }
        args.push('-s', script); // frida-inject uses -s for script (not -l)

        // Pass parameters via --parameters flag -> sets Script.parameters
        if (params && Object.keys(params).length > 0) {
            args.push('--parameters', JSON.stringify(params));
        }

        if (process.env.DEBUG) {
            console.log('[DEBUG] Frida-inject command:', 'frida-inject', args.join(' '));
        }

        const frida = spawn('frida-inject', args, {
            stdio: ['inherit', 'pipe', 'pipe'], // inherit stdin for agent console.log to work
        });
        const outputBuffer = this._createOutputBuffer();
        let resolved = false;
        let autoStopTimeout = null;

        // Enhanced process cleanup with proper signal handling
        const stop = async () => {
            try {
                if (autoStopTimeout) {
                    clearTimeout(autoStopTimeout);
                    autoStopTimeout = null;
                }

                if (frida && !frida.killed) {
                    // Try graceful termination first with SIGTERM
                    frida.kill('SIGTERM');

                    // Wait for graceful shutdown
                    await new Promise((resolve) => setTimeout(resolve, this.TIMEOUTS.KILL_DELAY));

                    // If still alive, force kill with SIGKILL
                    if (!frida.killed) {
                        frida.kill('SIGKILL');
                        await new Promise((resolve) =>
                            setTimeout(resolve, this.TIMEOUTS.KILL_DELAY)
                        );
                    }
                }
            } catch (cleanupError) {
                // Log error but don't throw - cleanup should be best-effort
                if (process.env.DEBUG) {
                    console.warn('Process cleanup warning:', cleanupError.message);
                }
            } finally {
                outputBuffer.clear();
            }
        };

        frida.stdout.on('data', (data) => {
            const str = data.toString();
            outputBuffer.add(str);
            if (process.env.DEBUG) {
                console.log('[FRIDA STDOUT]', str.trim());
                console.log('[FRIDA BUFFER SIZE]', outputBuffer.size());
            }
        });

        frida.stderr.on('data', (data) => {
            const str = data.toString();
            outputBuffer.add(str);
            if (process.env.DEBUG) {
                console.log('[FRIDA STDERR]', str.trim());
                console.log('[FRIDA BUFFER SIZE]', outputBuffer.size());
            }
        });

        frida.on('exit', () => {
            if (!resolved) {
                resolved = true;
                // Clear auto-stop timeout when process exits naturally
                if (autoStopTimeout) {
                    clearTimeout(autoStopTimeout);
                    autoStopTimeout = null;
                }
            }
        });

        frida.on('error', () => {
            if (!resolved) {
                resolved = true;
                // Clear auto-stop timeout on error
                if (autoStopTimeout) {
                    clearTimeout(autoStopTimeout);
                    autoStopTimeout = null;
                }
            }
        });

        // Wait for agent to output expected messages or timeout
        const waitForAgentMessages = async () => {
            const startTime = Date.now();
            const maxWait = Math.min(timeout, this.TIMEOUTS.INJECTION_MIN);

            // Get the agent name from the script path
            const agentName = Object.keys(this.MAPPINGS.AGENT_SCRIPT).find(
                (key) => this.MAPPINGS.AGENT_SCRIPT[key] === script.split('/').pop()
            );

            if (!agentName) {
                // Fallback to hardcoded check if agent name not found
                while (Date.now() - startTime < maxWait) {
                    const currentOutput = outputBuffer.get();
                    if (
                        currentOutput.includes('Agent starting') &&
                        currentOutput.includes('Agent started')
                    ) {
                        return true;
                    }
                    if (frida.exitCode !== null) break;
                    await new Promise((resolve) => setTimeout(resolve, 100));
                }
                return false;
            }

            // Get the expected messages from AGENT_LOG_SEQUENCES
            const sequence = AGENT_LOG_SEQUENCES[agentName];
            if (!sequence || !sequence.required || sequence.required.length < 2) {
                // Fallback to hardcoded check if sequence not found
                while (Date.now() - startTime < maxWait) {
                    const currentOutput = outputBuffer.get();
                    if (
                        currentOutput.includes('Agent starting') &&
                        currentOutput.includes('Agent started')
                    ) {
                        return true;
                    }
                    if (frida.exitCode !== null) break;
                    await new Promise((resolve) => setTimeout(resolve, 100));
                }
                return false;
            }

            const [requiredMessage1, requiredMessage2] = sequence.required;

            while (Date.now() - startTime < maxWait) {
                const currentOutput = outputBuffer.get();

                // Check if we have the expected agent messages (with logger prefixes)
                if (
                    currentOutput.includes(requiredMessage1) &&
                    currentOutput.includes(requiredMessage2)
                ) {
                    return true;
                }

                // Check if process has exited
                if (frida.exitCode !== null) {
                    break;
                }

                await new Promise((resolve) => setTimeout(resolve, 100));
            }
            return false;
        };

        await waitForAgentMessages();

        // Auto-stop after timeout to prevent hanging - track the timeout for cleanup
        if (!resolved) {
            autoStopTimeout = setTimeout(async () => {
                await stop();
            }, this.TIMEOUTS.AUTO_STOP_DELAY);
        }

        return {
            success: frida.exitCode === null || frida.exitCode === 0,
            output: outputBuffer.get(),
            process: frida,
            stop: stop,
            exitCode: frida.exitCode,
        };
    }

    /**
     * Starts a Java stub process for testing
     * Returns stub object with PID for injection
     * @param {string} appName - Application name (must be in JAR_FILE mapping)
     * @param {string} [cwd=process.cwd()] - Working directory for the stub process
     * @returns {Promise<Object>} Stub process control object with PID
     * @throws {TypeError} When parameters are invalid
     * @throws {Error} When app name is unknown or stub fails to start
     */
    static async startStub(appName, cwd = process.cwd()) {
        this._validateParam('appName', appName, 'string');
        this._validateParam('cwd', cwd, 'string');

        const jarFile = this.MAPPINGS.JAR_FILE[appName];
        if (!jarFile) {
            const availableApps = Object.keys(this.MAPPINGS.JAR_FILE).join(', ');
            throw this._createEnhancedError(
                `Unknown app name: ${appName}`,
                `Available app names: ${availableApps}\nEnsure the app name matches one of the supported applications.`
            );
        }

        const buildDir = join(__dirname, '../build');
        const jarPath = join(buildDir, jarFile);

        // Verify JAR exists
        await this._validateFileExists(jarPath);

        // Start with JAR file
        const stub = spawn('java', ['-jar', jarFile], {
            cwd: buildDir,
            stdio: ['pipe', 'pipe', 'pipe'],
        });

        const outputBuffer = this._createOutputBuffer();

        stub.stdout.on('data', (data) => {
            const str = data.toString();
            outputBuffer.add(str);
            if (process.env.DEBUG) {
                console.log(`[STUB ${appName}]`, str.trim());
            }
        });

        stub.stderr.on('data', (data) => {
            const str = data.toString();
            outputBuffer.add(str);
            if (process.env.DEBUG) {
                console.log(`[STUB ${appName} ERROR]`, str.trim());
            }
        });

        // Wait for ready message instead of fixed delay
        const readyPromise = new Promise((resolve, reject) => {
            const timeout = setTimeout(() => {
                reject(new Error('Stub did not become ready within timeout'));
            }, this.TIMEOUTS.STUB_INITIALIZATION);

            const checkReady = (data) => {
                if (data.toString().includes('Ready for Frida injection')) {
                    clearTimeout(timeout);
                    stub.stdout.off('data', checkReady);
                    resolve();
                }
            };

            stub.stdout.on('data', checkReady);
        });

        try {
            await readyPromise;
        } catch {
            // If ready message not found, continue anyway (for backward compatibility)
            // but log the issue
            if (process.env.DEBUG) {
                console.warn('[DEBUG] Ready message not found, continuing anyway');
            }
        }

        return {
            process: stub,
            pid: stub.pid, // <-- KEY: Return PID for injection
            appName,
            get output() {
                return outputBuffer.get();
            },
            stop: async () => {
                stub.kill();
                await new Promise((resolve) => setTimeout(resolve, this.TIMEOUTS.KILL_DELAY));
                outputBuffer.clear();
            },
        };
    }

    /**
     * Gets the process name for a given app name
     * @param {string} appName - Application name
     * @returns {string} Process name for the app
     * @throws {TypeError} When appName is not a string
     */
    static getProcessName(appName) {
        this._validateParam('appName', appName, 'string');
        return this.MAPPINGS.PROCESS_NAME[appName] || appName;
    }

    /**
     * Gets the agent script file path for a given agent name
     * @param {string} agentName - Agent name (must be in AGENT_SCRIPT mapping)
     * @param {string} [buildPath='../voboost-script/build'] - Build directory path
     * @returns {string} Full path to the agent script
     * @throws {TypeError} When parameters are invalid
     * @throws {Error} When agent name is unknown
     */
    static getAgentScript(agentName, buildPath = '../voboost-script/build') {
        this._validateParam('agentName', agentName, 'string');
        this._validateParam('buildPath', buildPath, 'string');

        const filename = this.MAPPINGS.AGENT_SCRIPT[agentName];
        if (!filename) {
            const availableAgents = Object.keys(this.MAPPINGS.AGENT_SCRIPT).join(', ');
            throw this._createEnhancedError(
                `Unknown agent: ${agentName}`,
                `Available agents: ${availableAgents}\nEnsure the agent name matches one of the supported Frida scripts.`
            );
        }

        return join(buildPath, filename);
    }

    /**
     * Validates injection output against expected patterns for an agent
     * @param {string} output - Output from the injection process
     * @param {string} agentName - Name of the agent to validate against
     * @returns {Object} Validation result with validity status and pattern matches
     * @throws {TypeError} When parameters are invalid
     */
    static validateInjection(output, agentName) {
        this._validateParam('output', output, 'string');
        this._validateParam('agentName', agentName, 'string');

        const sequence = AGENT_LOG_SEQUENCES[agentName];

        if (!sequence) {
            return {
                valid: false,
                agentName,
                reason: `Unknown agent: ${agentName}`,
                details: {},
            };
        }

        const result = {
            valid: true,
            agentName,
            requiredMatched: [],
            requiredMissing: [],
            operationalMatched: [],
            forbiddenFound: [],
            reason: '',
        };

        // Helper to escape regex special characters
        const escapeRegex = (string) => {
            return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        };

        // 1. Check required messages appear IN ORDER
        let lastIndex = -1;
        for (const pattern of sequence.required) {
            const regex = new RegExp(escapeRegex(pattern), 'i');
            const match = output.match(regex);

            if (!match) {
                result.requiredMissing.push(pattern);
                result.valid = false;
            } else {
                const index = output.indexOf(match[0]);
                if (index <= lastIndex) {
                    result.valid = false;
                    result.reason = `Message "${pattern}" appeared out of order`;
                }
                lastIndex = index;
                result.requiredMatched.push(pattern);
            }
        }

        // 2. Check operational messages (any order, optional)
        for (const pattern of sequence.operational) {
            const regex = new RegExp(escapeRegex(pattern), 'i');
            if (regex.test(output)) {
                result.operationalMatched.push(pattern);
            }
        }

        // 3. Check NO forbidden messages appear
        for (const pattern of sequence.forbidden) {
            const regex = new RegExp(escapeRegex(pattern), 'i');
            if (regex.test(output)) {
                result.forbiddenFound.push(pattern);
                result.valid = false;
            }
        }

        // Build reason string
        if (!result.valid) {
            if (result.requiredMissing.length > 0) {
                result.reason = `Missing required: ${result.requiredMissing.join(', ')}`;
            }
            if (result.forbiddenFound.length > 0) {
                result.reason +=
                    (result.reason ? '; ' : '') +
                    `Found errors: ${result.forbiddenFound.join(', ')}`;
            }
        } else {
            result.reason = 'All required messages found in order, no errors';
        }

        return result;
    }

    /**
     * Cleans up resources and processes
     *
     * IMPORTANT: This method now only cleans up the specific resources passed to it.
     * It no longer uses aggressive pkill commands that could interfere with other tests.
     * With PID-based injection, each test manages its own process lifecycle.
     *
     * @param {...Object} resources - Resources to clean up (processes with stop/kill methods)
     * @returns {Promise<void>}
     */
    static async cleanup(...resources) {
        for (const resource of resources) {
            if (resource && typeof resource.stop === 'function') {
                try {
                    await resource.stop();
                } catch {
                    // Log but don't throw - cleanup should be best-effort
                    if (process.env.DEBUG) {
                        console.warn('Cleanup warning:', 'Cleanup failed');
                    }
                }
            } else if (resource && resource.kill) {
                try {
                    resource.kill();
                    await new Promise((resolve) =>
                        setTimeout(resolve, this.TIMEOUTS.CLEANUP_DELAY)
                    );
                } catch {
                    // Log but don't throw
                    if (process.env.DEBUG) {
                        console.warn('Cleanup warning:', 'Cleanup failed');
                    }
                }
            }
        }

        // Small delay to ensure cleanup completes
        await new Promise((resolve) => setTimeout(resolve, this.TIMEOUTS.CLEANUP_DELAY));
    }

    /**
     * Unified cleanup method for all processes and resources
     * This is the single source of truth for all cleanup operations
     * @returns {Promise<void>}
     */
    static async cleanupAllProcesses() {
        if (process.env.DEBUG) {
            console.log('[DEBUG] FridaTestHelper.cleanupAllProcesses() called');
        }

        const cleanupPromises = [];

        // Clean up tracked active processes
        for (const proc of this.#activeProcesses) {
            cleanupPromises.push(
                new Promise((resolve) => {
                    try {
                        if (!proc.killed) {
                            proc.kill('SIGKILL');
                            setTimeout(resolve, 500);
                        } else {
                            resolve();
                        }
                    } catch {
                        resolve();
                    }
                })
            );
        }

        await Promise.all(cleanupPromises);
        this.#activeProcesses.clear();

        // Aggressive cleanup of remaining processes with timeout
        try {
            const { exec } = await import('child_process');
            const { promisify } = await import('util');
            const execAsync = promisify(exec);

            // Kill any remaining stub processes (more specific than generic Java kill)
            // Add timeout to prevent hanging
            await Promise.race([
                execAsync(
                    'pkill -f "java.*LauncherStub\\|java.*BluetoothPhoneStub\\|java.*SystemServiceStub\\|java.*QgimeStub\\|java.*VehicleSettingStub\\|frida" || true'
                ),
                new Promise((_, reject) =>
                    setTimeout(() => reject(new Error('Cleanup pkill timeout')), 3000)
                ),
            ]);

            // Wait for processes to die
            await new Promise((resolve) => setTimeout(resolve, 1000));
        } catch {
            // Ignore cleanup errors
        }

        if (process.env.DEBUG) {
            console.log('[DEBUG] FridaTestHelper.cleanupAllProcesses() completed');
        }
    }

    /**
     * Waits for a specific pattern to appear in injection output
     * @param {Object} injection - Injection object from inject() method
     * @param {RegExp} pattern - Pattern to wait for
     * @param {number} [timeout=TIMEOUTS.PROCESS_WAIT] - Maximum time to wait
     * @returns {Promise<boolean>} True if pattern is found
     * @throws {TypeError} When parameters are invalid
     * @throws {Error} When pattern is not found within timeout
     */
    static async waitForOutput(injection, pattern, timeout = this.TIMEOUTS.PROCESS_WAIT) {
        this._validateParam('injection', injection, 'object');
        if (!(pattern instanceof RegExp)) {
            throw new TypeError("Parameter 'pattern' must be a RegExp object.");
        }
        this._validateParam('timeout', timeout, 'number');

        const start = Date.now();

        return new Promise((resolve, reject) => {
            const checkInterval = setInterval(() => {
                const currentOutput = injection.output || '';
                if (pattern.test(currentOutput)) {
                    clearInterval(checkInterval);
                    resolve(true);
                } else if (Date.now() - start > timeout) {
                    clearInterval(checkInterval);
                    reject(
                        this._createEnhancedError(
                            `Pattern not found in output within ${timeout}ms`,
                            '1. Check if the agent script is properly injected\n2. Verify the pattern matches expected output\n3. Increase timeout if the process takes longer to initialize\n4. Check for errors in the injection output'
                        )
                    );
                }
            }, this.TIMEOUTS.OUTPUT_CHECK_INTERVAL);
        });
    }

    /**
     * Creates a test configuration for a specific agent
     * @param {string} agentName - Name of the agent
     * @param {Object} [customParams={}] - Custom parameters to merge with defaults
     * @returns {Object} Test configuration object
     * @throws {TypeError} When parameters are invalid
     */
    static createTestConfig(agentName, customParams = {}) {
        this._validateParam('agentName', agentName, 'string');
        this._validateParam('customParams', customParams, 'object');

        const config = AGENT_TEST_CONFIGS[agentName] || { params: {} };
        return {
            ...config.params,
            ...customParams,
        };
    }

    /**
     * Validates that frida-inject is available in PATH
     * @returns {Promise<boolean>} True if frida-inject is available
     * @throws {Error} When frida-inject is not found
     */
    static async validateFridaInject() {
        try {
            await execAsync('frida-inject --version');
            return true;
        } catch (error) {
            throw this._createEnhancedError(
                'frida-inject not found in PATH',
                'frida-inject must be downloaded separately from Frida releases:\n' +
                    '1. Go to https://github.com/frida/frida/releases\n' +
                    '2. Download frida-inject-VERSION-macos-arm64.xz (or x86_64 for Intel)\n' +
                    '3. Extract: xz -d frida-inject.xz\n' +
                    '4. Make executable: chmod +x frida-inject\n' +
                    '5. Move to PATH: sudo mv frida-inject /usr/local/bin/',
                error
            );
        }
    }
}
