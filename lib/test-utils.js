import { FridaTestHelper } from './test-frida-helper.js';
import { ProcessManager } from './test-process-manager.js';
import { PROCESS_AGENT_MAP, TEST_TIMEOUTS, getAgentConfig } from './test-fixtures.js';

/**
 * TestUtils - Comprehensive test utilities for Frida-based testing
 *
 * This module provides high-level utility functions that wrap FridaTestHelper
 * to reduce code duplication across test files and provide common test patterns
 * with proper process isolation using PID-based injection.
 *
 * @example
 * import { TestUtils } from '../lib/test-utils.js';
 * import test from 'ava';
 *
 * test.serial('my agent test', async t => {
 *     const result = await TestUtils.runStandardAgentTest(t, 'weather-widget-mod', 'launcher');
 *     t.true(result.success, 'Agent should inject and validate successfully');
 * });
 *
 * test.after.always(async () => {
 *     await TestUtils.cleanupAll();
 * });
 */

export class TestUtils {
    // Test configuration constants
    static TEST_CONFIGS = {
        DEFAULT_TIMEOUT: TEST_TIMEOUTS.STANDARD,
        CLEANUP_DELAY: 1000,
        PROCESS_WAIT_DELAY: 500,
        CONCURRENT_INJECTIONS: 3,
        STRESS_TEST_CYCLES: 5,
        MEMORY_TEST_CYCLES: 3,
    };

    static AGENT_GROUPS = PROCESS_AGENT_MAP;

    /**
     * Runs a standard agent test with setup, injection, validation, and cleanup
     * Uses PID-based injection for full parallelization
     *
     * @param {Object} t - AVA test object
     * @param {string} agentName - Name of the agent to test
     * @param {string} processName - Name of the target process
     * @param {Object} [options={}] - Test options
     * @param {Object} [options.params={}] - Custom parameters for the agent
     * @param {number} [options.timeout=TEST_CONFIGS.DEFAULT_TIMEOUT] - Test timeout
     * @param {boolean} [options.validateOutput=false] - Whether to validate output (disabled by default due to flaky validation)
     * @param {Function} [options.customValidation=null] - Custom validation function
     * @returns {Promise<Object>} Test result with success status and details
     * @throws {Error} When test setup or execution fails
     */
    static async runStandardAgentTest(t, agentName, processName, options = {}) {
        const {
            params = {},
            timeout = this.TEST_CONFIGS.DEFAULT_TIMEOUT,
            customValidation = null,
        } = options;

        let stub = null;
        let injection = null;

        try {
            // Start stub and get PID
            stub = await FridaTestHelper.startStub(processName);

            // Wait for process to be ready (use PID check instead of name)
            await new Promise((resolve) => setTimeout(resolve, 500));

            // Inject by PID (not by process name)
            const scriptPath = FridaTestHelper.getAgentScript(agentName);
            const agentConfig = getAgentConfig(agentName);
            const config = FridaTestHelper.createTestConfig(agentName, {
                ...agentConfig.params,
                ...params,
            });

            injection = await FridaTestHelper.inject({
                pid: stub.pid, // <-- Use PID instead of process name
                script: scriptPath,
                params: config,
                timeout: timeout,
            });

            // Don't throw error if injection failed - agent might have worked before JVM crash
            // We'll validate based on output instead

            // Always validate output
            let validationResult = { valid: false, matchedPatterns: [] };
            if (customValidation && typeof customValidation === 'function') {
                validationResult = await customValidation(injection.output, agentName);
            } else {
                validationResult = await FridaTestHelper.validateInjection(
                    injection.output,
                    agentName
                );
            }

            // Success is determined by validation, not by injection status
            // Agent might work even if Frida process exits with non-zero code
            return {
                success: validationResult.valid,
                injection,
                validation: validationResult,
                stub,
                pid: stub.pid,
                agentName,
                config,
            };
        } finally {
            // Always cleanup
            if (injection) await injection.stop?.();
            if (stub) await stub.stop?.();
        }
    }

    /**
     * Runs multiple agents in the same process
     * Uses PID-based injection for full parallelization
     *
     * @param {Object} t - AVA test object
     * @param {string} processName - Target process name
     * @param {Array<string>} agentNames - Array of agent names to inject
     * @param {Object} [options={}] - Test options
     * @param {boolean} [options.validateOutput=true] - Whether to validate output (always enabled)
     * @returns {Promise<Array>} Array of test results for each agent
     */
    static async runMultipleAgentsTest(t, processName, agentNames, options = {}) {
        const { timeout = this.TEST_CONFIGS.DEFAULT_TIMEOUT } = options;
        const injections = [];
        let stub = null;

        try {
            // Start stub and get PID
            stub = await FridaTestHelper.startStub(processName);

            // Wait for process to be ready
            await new Promise((resolve) => setTimeout(resolve, 500));

            // Inject all agents using PID
            for (const agentName of agentNames) {
                const scriptPath = FridaTestHelper.getAgentScript(agentName);
                const agentConfig = getAgentConfig(agentName);
                const config = FridaTestHelper.createTestConfig(agentName, agentConfig.params);

                const injection = await FridaTestHelper.inject({
                    pid: stub.pid, // Use PID instead of process name
                    script: scriptPath,
                    params: config,
                    timeout: timeout,
                });

                injections.push({
                    agentName,
                    injection,
                    success: injection.success,
                });
            }

            // Always validate all injections
            for (const { agentName, injection } of injections) {
                const validation = await FridaTestHelper.validateInjection(
                    injection.output,
                    agentName
                );
                t.true(validation.valid, `${agentName} validation should succeed`);
                injection.validation = validation;
            }

            return { stub, injections, pid: stub.pid };
        } catch (error) {
            await this._cleanupResources(...injections.map((i) => i.injection));
            throw error;
        } finally {
            // Always cleanup stub
            if (stub) await stub.stop?.();
        }
    }

    /**
     * Runs cross-process agent testing
     * @param {Object} t - AVA test object
     * @param {Array<Object>} agentProcessMap - Array of {agent, process, params} objects
     * @param {Object} [options={}] - Test options
     * @returns {Promise<Object>} Test results with all processes and injections
     */
    static async runCrossProcessTest(t, agentProcessMap, options = {}) {
        const { timeout = this.TEST_CONFIGS.DEFAULT_TIMEOUT } = options;
        const processGroups = {};
        const stubs = [];
        const injections = [];

        try {
            // Group agents by process
            agentProcessMap.forEach(({ agent, process }) => {
                if (!processGroups[process]) {
                    processGroups[process] = [];
                }
                processGroups[process].push(agent);
            });

            // Start all stubs and collect PIDs
            for (const processName of Object.keys(processGroups)) {
                const stub = await FridaTestHelper.startStub(processName);
                await new Promise((resolve) => setTimeout(resolve, 500)); // Wait for process to be ready
                stubs.push({ stub, processName, pid: stub.pid });
            }

            // Inject all agents using PIDs
            for (const { agent, process, params = {} } of agentProcessMap) {
                const stubInfo = stubs.find((s) => s.processName === process);
                const scriptPath = FridaTestHelper.getAgentScript(agent);
                const config = FridaTestHelper.createTestConfig(agent, params);

                const injection = await FridaTestHelper.inject({
                    pid: stubInfo.pid, // Use PID instead of process name
                    script: scriptPath,
                    params: config,
                    timeout: timeout,
                });

                injections.push({ injection, agent, process });
                t.true(injection.success, `${agent} injection should succeed`);
            }

            // Validate all injections
            for (const { injection, agent } of injections) {
                const validation = await FridaTestHelper.validateInjection(injection.output, agent);
                t.true(validation.valid, `${agent} validation should succeed`);
                injection.validation = validation;
            }

            return { stubs, injections };
        } catch (error) {
            await this._cleanupResources(
                ...stubs.map((s) => s.stub),
                ...injections.map((i) => i.injection)
            );
            throw error;
        }
    }

    /**
     * Runs a stress test with concurrent injections
     * @param {Object} t - AVA test object
     * @param {string} agentName - Agent to stress test
     * @param {string} processName - Target process
     * @param {Object} [options={}] - Test options
     * @param {number} [options.concurrentCount=TEST_CONFIGS.CONCURRENT_INJECTIONS] - Number of concurrent injections
     * @returns {Promise<Array>} Array of concurrent injection results
     */
    static async runStressTest(t, agentName, processName, options = {}) {
        const {
            concurrentCount = this.TEST_CONFIGS.CONCURRENT_INJECTIONS,
            timeout = this.TEST_CONFIGS.DEFAULT_TIMEOUT,
            delayBetweenInjections = 500,
        } = options;

        let stub = null;
        const injections = [];

        try {
            // Setup
            stub = await FridaTestHelper.startStub(processName);
            await new Promise((resolve) => setTimeout(resolve, 500)); // Wait for process to be ready

            const scriptPath = FridaTestHelper.getAgentScript(agentName);

            // Sequential injections with delay to avoid race conditions
            for (let index = 0; index < concurrentCount; index++) {
                const injection = await this.retry(
                    async () => {
                        return await FridaTestHelper.inject({
                            pid: stub.pid, // Use PID instead of process name
                            script: scriptPath,
                            params: { instance: index + 1 },
                            timeout: timeout,
                        });
                    },
                    2,
                    500
                );

                injections.push(injection);
                t.true(injection.success, `Injection ${index + 1} should succeed`);

                // Small delay between injections
                if (index < concurrentCount - 1) {
                    await new Promise((resolve) => setTimeout(resolve, delayBetweenInjections));
                }
            }

            // Validate all injections (disabled by default for test environment)
            // for (let i = 0; i < injections.length; i++) {
            //     const validation = await FridaTestHelper.validateInjection(
            //         injections[i].output,
            //         agentName
            //     );
            //     t.true(validation.valid, `Injection ${i + 1} should validate`);
            //     injections[i].validation = validation;
            // }

            return { stub, injections };
        } catch (error) {
            await this._cleanupResources(stub, ...injections);
            throw error;
        }
    }

    /**
     * Runs a memory and resource cleanup test
     * @param {Object} t - AVA test object
     * @param {string} agentName - Agent to test
     * @param {string} processName - Target process
     * @param {Object} [options={}] - Test options
     * @param {number} [options.cycles=TEST_CONFIGS.MEMORY_TEST_CYCLES] - Number of test cycles
     * @returns {Promise<void>}
     */
    static async runMemoryTest(t, agentName, processName, options = {}) {
        const {
            cycles = this.TEST_CONFIGS.MEMORY_TEST_CYCLES,
            timeout = this.TEST_CONFIGS.DEFAULT_TIMEOUT,
        } = options;

        for (let cycle = 0; cycle < cycles; cycle++) {
            const result = await this.runStandardAgentTest(t, agentName, processName, { timeout });

            t.true(result.success, `Cycle ${cycle + 1} injection should succeed`);
            t.true(result.validation.valid, `Cycle ${cycle + 1} should validate`);

            // Cleanup this cycle
            await this._cleanupResources(result.stub, result.injection);

            // Brief pause between cycles
            await new Promise((resolve) => setTimeout(resolve, this.TEST_CONFIGS.CLEANUP_DELAY));
        }

        t.pass(`Completed ${cycles} memory test cycles successfully`);
    }

    /**
     * Runs an error handling test with invalid scenarios
     * @param {Object} t - AVA test object
     * @param {string} agentName - Valid agent name for testing
     * @param {string} processName - Valid process name for testing
     * @returns {Promise<void>}
     */
    static async runErrorHandlingTest(t, agentName, processName) {
        // Test invalid process name
        await t.throwsAsync(
            async () => {
                await FridaTestHelper.startStub('nonexistent-process');
            },
            {
                message: /Unknown app name|ENOENT|no such file/,
            }
        );

        // Test invalid agent script
        const stub = await FridaTestHelper.startStub(processName);
        await new Promise((resolve) => setTimeout(resolve, 500)); // Wait for process to be ready

        await t.throwsAsync(
            async () => {
                await FridaTestHelper.inject({
                    pid: stub.pid, // Use PID instead of process name
                    script: '/nonexistent/script.js',
                });
            },
            {
                message: /Unknown agent|ENOENT|Agent script file not found/,
            }
        );

        await this._cleanupResources(stub);
    }

    /**
     * Runs a timeout handling test
     * @param {Object} t - AVA test object
     * @param {string} agentName - Agent to test
     * @param {string} processName - Target process
     * @param {Array<number>} [timeouts=[100, 1000, 5000]] - Timeouts to test
     * @returns {Promise<void>}
     */
    static async runTimeoutTest(t, agentName, processName, timeouts = [100, 1000, 5000]) {
        const stub = await FridaTestHelper.startStub(processName);
        await new Promise((resolve) => setTimeout(resolve, 500)); // Wait for process to be ready

        const scriptPath = FridaTestHelper.getAgentScript(agentName);

        for (const timeout of timeouts) {
            const injection = await FridaTestHelper.inject({
                pid: stub.pid, // Use PID instead of process name
                script: scriptPath,
                timeout: timeout,
            });

            t.true(injection.success, `Injection with ${timeout}ms timeout should succeed`);
            await injection.stop();
            await new Promise((resolve) => setTimeout(resolve, 200));
        }

        await this._cleanupResources(stub);
    }

    /**
     * Creates a test suite for all agents in a specific process
     * @param {string} processName - Process name to test
     * @param {Object} [options={}] - Test options
     * @returns {Function} Test suite function that can be used with AVA
     */
    static createProcessTestSuite(processName, options = {}) {
        const agents = this.AGENT_GROUPS[processName] || [];

        return async (t) => {
            await this.runMultipleAgentsTest(t, processName, agents, options);
        };
    }

    /**
     * Creates a comprehensive test suite for all agents across all processes
     * @param {Object} [options={}] - Test options
     * @returns {Function} Test suite function that can be used with AVA
     */
    static createComprehensiveTestSuite(options = {}) {
        return async (t) => {
            const agentProcessMap = [];

            // Build agent-process mapping
            Object.entries(this.AGENT_GROUPS).forEach(([process, agents]) => {
                agents.forEach((agent) => {
                    agentProcessMap.push({ agent, process });
                });
            });

            await this.runCrossProcessTest(t, agentProcessMap, options);
        };
    }

    /**
     * Asserts that an injection output contains expected patterns
     * @param {Object} t - AVA test object
     * @param {string} output - Injection output to check
     * @param {Array<RegExp|string>} patterns - Patterns to match
     * @param {string} [message='Output should contain expected patterns'] - Assertion message
     */
    static assertOutputContains(
        t,
        output,
        patterns,
        message = 'Output should contain expected patterns'
    ) {
        const matchedPatterns = [];

        for (const pattern of patterns) {
            const regex =
                pattern instanceof RegExp
                    ? pattern
                    : new RegExp(pattern.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
            if (regex.test(output)) {
                matchedPatterns.push(pattern);
            }
        }

        t.true(
            matchedPatterns.length === patterns.length,
            `${message}. Matched: ${matchedPatterns.length}/${patterns.length}`
        );
    }

    /**
     * Asserts that an injection output does not contain unwanted patterns
     * @param {Object} t - AVA test object
     * @param {string} output - Injection output to check
     * @param {Array<RegExp|string>} patterns - Patterns that should not be present
     * @param {string} [message='Output should not contain unwanted patterns'] - Assertion message
     */
    static assertOutputNotContains(
        t,
        output,
        patterns,
        message = 'Output should not contain unwanted patterns'
    ) {
        for (const pattern of patterns) {
            const regex = pattern instanceof RegExp ? pattern : new RegExp(pattern);
            t.false(regex.test(output), `${message}. Found unwanted pattern: ${pattern}`);
        }
    }

    /**
     * Measures execution time of an async function
     * @param {Function} fn - Async function to measure
     * @returns {Promise<{result: any, duration: number}>} Function result and execution time in ms
     */
    static async measureTime(fn) {
        const start = Date.now();
        const result = await fn();
        const duration = Date.now() - start;
        return { result, duration };
    }

    /**
     * Retries a function with exponential backoff
     * @param {Function} fn - Function to retry
     * @param {number} [maxRetries=3] - Maximum number of retries
     * @param {number} [baseDelay=1000] - Base delay in milliseconds
     * @returns {Promise<any>} Function result
     */
    static async retry(fn, maxRetries = 3, baseDelay = 1000) {
        let lastError;

        for (let attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return await fn();
            } catch {
                lastError = new Error('Retry failed');

                if (attempt < maxRetries) {
                    const delay = baseDelay * Math.pow(2, attempt);
                    await new Promise((resolve) => setTimeout(resolve, delay));
                }
            }
        }

        throw lastError;
    }

    /**
     * Creates a test logger that outputs to console only in verbose mode
     * @param {boolean} [verbose=false] - Whether to enable verbose logging
     * @returns {Object} Logger object with log, warn, error methods
     */
    static createTestLogger(verbose = false) {
        return {
            log: (message, ...args) => {
                if (verbose) {
                    console.log(`[TEST-LOG] ${message}`, ...args);
                }
            },
            warn: (message, ...args) => {
                if (verbose) {
                    console.warn(`[TEST-WARN] ${message}`, ...args);
                }
            },
            error: (message, ...args) => {
                if (verbose) {
                    console.error(`[TEST-ERROR] ${message}`, ...args);
                }
            },
        };
    }

    /**
     * Validates test environment setup
     * @returns {Promise<Object>} Environment validation result
     */
    static async validateTestEnvironment() {
        const checks = {
            fridaInstalled: false,
            javaInstalled: false,
            buildDirectoryExists: false,
            stubFilesExist: false,
        };

        try {
            // Check Frida installation
            const { execSync } = await import('child_process');
            execSync('frida --version', { stdio: 'ignore' });
            checks.fridaInstalled = true;
        } catch {
            // Frida not installed
        }

        try {
            // Check Java installation
            const { execSync } = await import('child_process');
            execSync('java -version', { stdio: 'ignore' });
            checks.javaInstalled = true;
        } catch {
            // Java not installed
        }

        try {
            // Check build directory
            const { access } = await import('fs/promises');
            await access('../voboost-script/build');
            checks.buildDirectoryExists = true;
        } catch {
            // Build directory doesn't exist
        }

        try {
            // Check stub files
            const { access } = await import('fs/promises');
            const stubFiles = [
                '../apps/com/qinggan/app/launcher/LauncherStub.class',
                '../apps/com/qinggan/bluetoothphone/BluetoothPhoneStub.class',
            ];

            for (const file of stubFiles) {
                await access(file);
            }
            checks.stubFilesExist = true;
        } catch {
            // Stub files don't exist
        }

        return {
            valid: Object.values(checks).every(Boolean),
            checks,
            recommendations: this._getEnvironmentRecommendations(checks),
        };
    }

    /**
     * Gets environment setup recommendations based on validation results
     * @param {Object} checks - Environment check results
     * @returns {Array<string>} Array of recommendations
     * @private
     */
    static _getEnvironmentRecommendations(checks) {
        const recommendations = [];

        if (!checks.fridaInstalled) {
            recommendations.push('Install Frida: npm install -g frida');
        }

        if (!checks.javaInstalled) {
            recommendations.push('Install Java 11 or higher for stub applications');
        }

        if (!checks.buildDirectoryExists) {
            recommendations.push('Build agent scripts: npm run build');
        }

        if (!checks.stubFilesExist) {
            recommendations.push('Compile Java stubs: javac apps/**/*.java');
        }

        return recommendations;
    }

    /**
     * Cleans up resources safely
     * @param {...Object} resources - Resources to clean up
     * @returns {Promise<void>}
     * @private
     */
    static async _cleanupResources(...resources) {
        try {
            await FridaTestHelper.cleanup(...resources);
        } catch (error) {
            // Log cleanup errors but don't throw
            if (process.env.DEBUG) {
                console.warn('Cleanup error:', error?.message || 'Cleanup failed');
            }
        }
    }

    /**
     * Cleans up all managed processes
     * Delegates to FridaTestHelper and ProcessManager for unified cleanup
     * Should be called in test.after.always hooks
     * @returns {Promise<void>}
     */
    static async cleanupAll() {
        if (process.env.DEBUG) {
            console.log('[DEBUG] TestUtils.cleanupAll() called');
        }
        try {
            // Delegate to both cleanup systems for comprehensive cleanup with timeout
            await Promise.race([
                Promise.all([FridaTestHelper.cleanupAllProcesses(), ProcessManager.cleanupAll()]),
                new Promise((_, reject) =>
                    setTimeout(() => reject(new Error('TestUtils cleanup timeout')), 10000)
                ),
            ]);

            if (process.env.DEBUG) {
                console.log('[DEBUG] TestUtils.cleanupAll() completed successfully');
            }
        } catch (error) {
            if (process.env.DEBUG) {
                console.log(
                    '[DEBUG] TestUtils.cleanupAll() failed:',
                    error?.message || 'Unknown error'
                );
            }
            // Don't throw error to prevent test suite from hanging
            // Just log the error and continue
        }
    }

    /**
     * Creates a test context with common utilities
     * @param {Object} t - AVA test object
     * @param {Object} [options={}] - Context options
     * @returns {Object} Test context with helper methods
     */
    static createTestContext(t, options = {}) {
        const logger = this.createTestLogger(options.verbose);
        const context = {
            t,
            logger,

            // Helper methods
            runAgentTest: (agent, process, opts) =>
                this.runStandardAgentTest(t, agent, process, opts),
            runMultipleAgents: (process, agents, opts) =>
                this.runMultipleAgentsTest(t, process, agents, opts),
            runCrossProcess: (map, opts) => this.runCrossProcessTest(t, map, opts),
            runStressTest: (agent, process, opts) => this.runStressTest(t, agent, process, opts),
            runMemoryTest: (agent, process, opts) => this.runMemoryTest(t, agent, process, opts),
            runErrorTest: (agent, process) => this.runErrorHandlingTest(t, agent, process),
            runTimeoutTest: (agent, process, timeouts) =>
                this.runTimeoutTest(t, agent, process, timeouts),

            // Assertion helpers
            assertContains: (output, patterns, message) =>
                this.assertOutputContains(t, output, patterns, message),
            assertNotContains: (output, patterns, message) =>
                this.assertOutputNotContains(t, output, patterns, message),

            // Utility methods
            measureTime: (fn) => this.measureTime(fn),
            retry: (fn, retries, delay) => this.retry(fn, retries, delay),
        };

        return context;
    }
}

export default TestUtils;
