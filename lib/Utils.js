import { Frida } from './Frida.js';
import { TEST_TIMEOUTS, getAgentConfig } from './Fixtures.js';
import { MultiAgent } from './MultiAgent.js';
import { Retry } from './Retry.js';
import { ErrorHandler } from './ErrorHandler.js';
import { Debug } from './Debug.js';

/**
 * @typedef {Object} TestConfigOptions
 * @property {Object} [params={}] - Custom parameters for the agent
 * @property {number} [timeout=TEST_CONFIGS.DEFAULT_TIMEOUT] - Test timeout
 * @property {Function} [customValidation=null] - Custom validation function
 * @property {number} [retryAttempts] - Custom retry count for injection
 * @property {number} [retryDelay] - Custom base delay for retries in milliseconds
 */

/**
 * @typedef {Object} StandardAgentTestResult
 * @property {boolean} success - Whether the test was successful
 * @property {import('./Frida.js').InjectionResult} injection - Injection result
 * @property {import('./Frida.js').ValidationResult} validation - Validation result
 * @property {import('./Frida.js').StubProcess} stub - Stub process
 * @property {number} pid - Process ID
 * @property {string} agentName - Name of the agent
 * @property {Object} config - Configuration used for the test
 */

/**
 * @typedef {Object} MultipleAgentsTestOptions
 * @property {number} [timeout=TEST_CONFIGS.DEFAULT_TIMEOUT] - Test timeout
 * @property {boolean} [assertSuccess=true] - Whether to assert that all injections succeed
 * @property {boolean} [assertValidation=true] - Whether to assert that all validations pass
 * @property {string} [successMessage] - Custom success message for assertions
 */

/**
 * @typedef {Object} MultipleAgentsTestResult
 * @property {import('./Frida.js').StubProcess} stub - Stub process
 * @property {Array<{agentName: string, injection: import('./Frida.js').InjectionResult, success: boolean}>} injections - Array of injection results
 * @property {number} pid - Process ID
 */

/**
 * @typedef {Object} AgentTestConfig
 * @property {string} agentName - Name of the agent
 * @property {string} processName - Name of the target process
 * @property {number} timeout - Test timeout in milliseconds
 * @property {Object} params - Merged agent parameters
 * @property {Function|null} customValidation - Custom validation function
 */

/**
 * TestUtils - Comprehensive test utilities for Frida-based testing
 *
 * This module provides high-level utility functions that wrap FridaHelper
 * to reduce code duplication across test files and provide common test patterns
 * with proper process isolation using PID-based injection.
 *
 * @example
 * import { TestUtils } from '../lib/Utils.js';
 * import test from 'ava';
 *
 * test('my agent test', async t => {
 *     const result = await Utils.runStandardAgentTest(t, 'weather-widget-mod', 'launcher');
 *     t.true(result.success, 'Agent should inject and validate successfully');
 * });
 */

export class Utils {
    /**
     * Unified test configuration constants
     * @type {Object}
     * @readonly
     */
    static TEST_CONFIGS = {
        // Multi-agent settings
        MULTI_AGENT_HEALTH_CHECK: true,
        MULTI_AGENT_RESTART_ON_FAILURE: true,

        // All timeouts in one place
        TIMEOUTS: {
            DEFAULT: TEST_TIMEOUTS.STANDARD, // 8000
            INJECTION_MULTI_AGENT: 20000,
            PROCESS_WAIT: 5000,
            PROCESS_STARTUP: 5000,
            PROCESS_SHUTDOWN: 3000,
            HEALTH_CHECK: 1000,
            STABILIZATION: 5000,
            CLEANUP_DELAY: 1000,
            CLEANUP_KILL_DELAY: 300,
            MULTI_AGENT_DELAY: 2000,
            OUTPUT_CHECK_INTERVAL: 50,
            AUTO_STOP_DELAY: 5000,
            PRE_INJECTION_WAIT: 1000,
            MESSAGE_CHECK_INTERVAL: 100,
            HEALTH_CHECK_INTERVAL: 500,
            MEMORY_CHECK_INTERVAL: 2000,
            RETRY_BASE_DELAY: 1000,
            RETRY_MAX_DELAY: 10000,
        },

        // All retries in one place
        RETRIES: {
            DEFAULT: 3,
            INJECTION_RETRY_ATTEMPTS: 3,
            MULTI_AGENT_MAX_RESTARTS: 2,
            ARCHITECTURE_ERROR: 1,
            BACKOFF: 2,
        },

        // All resources in one place
        RESOURCES: {
            MAX_MEMORY_MB: 4096, // 4GB
            MAX_CONCURRENT_PROCESSES: 10,
            OUTPUT_BUFFER_SIZE: 10 * 1024 * 1024, // 10MB
        },
    };

    /**
     * Gets timeout value by type
     * @param {string} type - Timeout type
     * @returns {number} Timeout value in milliseconds
     */
    static getTimeout(type) {
        return this.TEST_CONFIGS.TIMEOUTS[type] ?? this.TEST_CONFIGS.TIMEOUTS.DEFAULT;
    }

    /**
     * Gets retry configuration for error type
     * @param {string} errorType - Error type
     * @returns {Object} Retry configuration
     */
    static getRetryConfig(errorType) {
        switch (errorType) {
            case Frida.ERROR_TYPES.JAVA_BRIDGE:
            case Frida.ERROR_TYPES.PROCESS_TERMINATED:
            case Frida.ERROR_TYPES.MEMORY:
                return { maxRetries: this.TEST_CONFIGS.RETRIES.DEFAULT, recoverable: true };
            case Frida.ERROR_TYPES.ARCHITECTURE:
                return {
                    maxRetries: this.TEST_CONFIGS.RETRIES.ARCHITECTURE_ERROR,
                    recoverable: false,
                };
            default:
                return { maxRetries: this.TEST_CONFIGS.RETRIES.DEFAULT, recoverable: true };
        }
    }

    /**
     * Injects a single agent into a process
     * @private
     * @param {number} pid - Process ID to inject into
     * @param {string} agentName - Name of the agent
     * @param {number} timeout - Injection timeout
     * @param {Object} [retryOptions={}] - Retry options
     * @param {number} [retryOptions.maxRetries] - Maximum number of retry attempts
     * @returns {Promise<import('./Frida.js').InjectionResult>}
     */
    static async _injectAgent(pid, agentName, timeout, retryOptions = {}) {
        const scriptPath = Frida.getAgentScript(agentName);
        const agentConfig = getAgentConfig(agentName);
        const config = Frida.createTestConfig(agentName, agentConfig.params);

        // Create retry strategy
        const retryStrategy = new Retry({
            maxRetries: retryOptions.maxRetries || this.TEST_CONFIGS.RETRIES.DEFAULT,
            baseDelay: this.getTimeout('RETRY_BASE_DELAY'),
            backoff: this.TEST_CONFIGS.RETRIES.BACKOFF,
        });

        let attempt = 0;
        let lastError = null;

        while (attempt <= retryStrategy.maxRetries) {
            attempt++;

            try {
                const result = await Frida.inject({
                    pid,
                    script: scriptPath,
                    params: config,
                    timeout,
                    maxRetries: 1, // We handle retries at this level
                });

                // If injection succeeded, return the result
                if (result.success || result.hasValidOutput) {
                    if (process.env.DEBUG) {
                        console.log(
                            `[DEBUG] Injection succeeded for ${agentName} on attempt ${attempt}`
                        );
                    }
                    return result;
                }

                // If injection failed but has valid output, still consider it a failure
                // but don't retry for certain error types
                const errorType = result.errorType || Frida.ERROR_TYPES.UNKNOWN;
                if (!retryStrategy.shouldRetry(attempt, errorType)) {
                    const errorContext = ErrorHandler.createErrorContext(
                        new Error(result.error || 'Injection failed'),
                        { pid, agentName, attempt }
                    );
                    throw new Error(ErrorHandler.formatErrorMessage(errorContext));
                }

                lastError = new Error(result.error || 'Injection failed');
            } catch (error) {
                lastError = error;

                // Create error context and handle it
                const errorContext = ErrorHandler.createErrorContext(error, {
                    pid,
                    agentName,
                    attempt,
                });
                const retryConfig = this.getRetryConfig(errorContext.type);

                if (process.env.DEBUG) {
                    console.log(
                        `[DEBUG] Injection attempt ${attempt} failed for ${agentName}:`,
                        error.message
                    );
                }

                // Check if we should retry
                if (attempt >= retryStrategy.maxRetries || !retryConfig.recoverable) {
                    throw new Error(ErrorHandler.formatErrorMessage(errorContext));
                }

                // Wait before retry
                const delay = retryStrategy.getDelay(attempt);
                if (process.env.DEBUG) {
                    console.log(`[DEBUG] Waiting ${delay}ms before retry for ${agentName}...`);
                }
                await new Promise((resolve) => setTimeout(resolve, delay));
            }
        }

        // All attempts failed
        const errorContext = ErrorHandler.createErrorContext(
            lastError || new Error('Injection failed after all retries'),
            { pid, agentName, attempt }
        );
        throw new Error(ErrorHandler.formatErrorMessage(errorContext));
    }

    /**
     * Runs a standard agent test with setup, injection, validation, and cleanup
     * Uses PID-based injection for full parallelization
     *
     * @param {Object} t - AVA test object
     * @param {string} agentName - Name of the agent to test
     * @param {string} processName - Name of the target process
     * @param {TestConfigOptions} [options={}] - Test options
     * @returns {Promise<StandardAgentTestResult>} Test result with success status and details
     * @throws {Error} When test setup or execution fails
     */
    static async runStandardAgentTest(t, agentName, processName, options = {}) {
        const {
            params = {},
            timeout = this.getTimeout('DEFAULT'),
            customValidation = null,
            retryAttempts,
        } = options;

        let stub = null;
        let injection = null;

        try {
            // Start stub process and get its PID for injection
            stub = await Frida.startStub(processName);

            // Wait for process to be ready (using PID check instead of process name)
            await new Promise((resolve) => setTimeout(resolve, this.getTimeout('PROCESS_WAIT')));

            // Inject Frida agent by PID (not by process name)
            const agentConfig = getAgentConfig(agentName);
            const config = Frida.createTestConfig(agentName, {
                ...agentConfig.params,
                ...params,
            });

            injection = await this._injectAgent(stub.pid, agentName, timeout, {
                maxRetries: retryAttempts,
            });

            // Don't throw error if injection failed - agent might have worked before JVM crash
            // We'll validate based on output instead of injection status

            // Always validate output to determine actual success
            let validationResult = { valid: false, matchedPatterns: [] };
            if (customValidation && typeof customValidation === 'function') {
                validationResult = await customValidation(injection.output, agentName);
            } else {
                validationResult = await Frida.validateInjection(injection.output, agentName);
            }

            // Success is determined by BOTH injection status AND validation results
            // If injection failed (e.g., process terminated), the test should fail regardless of validation
            return {
                success: injection.success && validationResult.valid,
                injection,
                validation: validationResult,
                stub,
                pid: stub.pid,
                agentName,
                config,
            };
        } finally {
            // Always cleanup resources to prevent resource leaks
            if (injection) await injection.stop?.();
            if (stub) await stub.stop?.();
        }
    }

    /**
     * Runs a basic injection test with minimal boilerplate.
     * This is a simplified version of runStandardAgentTest for common use cases.
     * Handles all setup, injection, validation, and cleanup automatically.
     * Includes primary assertion for validation success.
     * Logs detailed information only on test failure.
     *
     * @param {Object} t - AVA test object with assertion methods
     * @param {string} agentName - Name of the agent to test (must exist in AGENT_SCRIPT mapping)
     * @param {string} processName - Name of the target process (must exist in JAR_FILE mapping)
     * @returns {Promise<StandardAgentTestResult>} Test result with validation details
     * @throws {Error} When agent or process name is invalid
     * @example
     * test('my-agent: default injection', async (t) => {
     *     await Utils.runBasicInjectionTest(t, 'my-agent', 'launcher');
     * });
     */
    static async runBasicInjectionTest(t, agentName, processName) {
        const config = this.createAgentTestConfig(agentName, processName);
        const result = await this.runStandardAgentTest(t, config.agentName, config.processName);

        // Primary assertion
        t.true(result.validation.valid, result.validation.reason);

        // Log details only on failure
        if (!result.validation.valid) {
            t.log('Required matched:', result.validation.requiredMatched);
            t.log('Required missing:', result.validation.requiredMissing);
            t.log('Errors found:', result.validation.forbiddenFound);
            t.log('Output preview:', result.injection.output.substring(0, 500));
        }

        return result;
    }

    /**
     * Runs multiple agents in the same process
     * Uses PID-based injection for full parallelization
     *
     * @param {Object} t - AVA test object
     * @param {string} processName - Target process name
     * @param {Array<string>} agentNames - Array of agent names to inject
     * @param {MultipleAgentsTestOptions} [options={}] - Test options
     * @returns {Promise<MultipleAgentsTestResult>} Test result with stub, injections, and validation details
     * @example
     * // Basic usage with default options
     * test('multiple agents in launcher', async (t) => {
     *     const result = await Utils.runMultipleAgentsTest(
     *         t,
     *         'launcher',
     *         ['weather-widget-mod', 'media-card-mod']
     *     );
     *
     *     // All agents should be injected and validated
     *     t.is(result.injections.length, 2);
     *     t.true(result.injections.every(i => i.success));
     * });
     *
     * @example
     * // Custom timeout and success message
     * test('multiple agents with custom options', async (t) => {
     *     const result = await Utils.runMultipleAgentsTest(
     *         t,
     *         'launcher',
     *         ['weather-widget-mod', 'media-card-mod', 'bluetooth-phone-mod'],
     *         {
     *             timeout: 15000,
     *             successMessage: 'All launcher agents should inject successfully',
     *             assertSuccess: true,
     *             assertValidation: true
     *         }
     *     );
     *
     *     // Check specific agent results
     *     const weatherAgent = result.injections.find(i => i.agentName === 'weather-widget-mod');
     *     t.true(weatherAgent.success, 'Weather agent should succeed');
     *     t.true(weatherAgent.injection.validation.valid, 'Weather agent should validate');
     * });
     *
     * @example
     * // Non-asserting mode for manual validation
     * test('multiple agents with manual validation', async (t) => {
     *     const result = await Utils.runMultipleAgentsTest(
     *         t,
     *         'launcher',
     *         ['weather-widget-mod', 'media-card-mod'],
     *         {
     *             assertSuccess: false,
     *             assertValidation: false
     *         }
     *     );
     *
     *     // Manual validation with custom logic
     *     for (const { agentName, injection } of result.injections) {
     *         if (agentName === 'weather-widget-mod') {
     *             t.true(injection.validation.valid, 'Weather agent should validate');
     *             t.true(injection.output.includes('Weather data loaded'), 'Should load weather data');
     *         }
     *
     *         if (agentName === 'media-card-mod') {
     *             t.true(injection.validation.valid, 'Media agent should validate');
     *             t.true(injection.output.includes('Media initialized'), 'Should initialize media');
     *         }
     *     }
     * });
     */
    static async runMultipleAgentsTest(t, processName, agentNames, options = {}) {
        const multiAgentOptions = this._createMultiAgentOptions(options);

        // Create orchestrator with configuration
        const orchestrator = new MultiAgent({
            health: {
                enabled: multiAgentOptions.healthCheck,
                maxMemoryMb: multiAgentOptions.maxMemoryMb,
                checkInterval: this.getTimeout('HEALTH_CHECK_INTERVAL'),
            },
            retries: {
                maxRetries: multiAgentOptions.retryAttempts || this.TEST_CONFIGS.RETRIES.DEFAULT,
                baseDelay: this.getTimeout('RETRY_BASE_DELAY'),
                backoff: this.TEST_CONFIGS.RETRIES.BACKOFF,
            },
            scheduling: {
                agentDelay: multiAgentOptions.agentDelay,
            },
        });

        try {
            // Execute the multi-agent test
            const result = await orchestrator.execute(processName, agentNames, {
                timeout: multiAgentOptions.timeout,
                maxRestarts: multiAgentOptions.maxRestarts,
                restartOnFailure: multiAgentOptions.restartOnFailure,
            });

            // Validate results if requested
            this._validateMultiAgentResult(t, result, multiAgentOptions);

            return result;
        } catch (error) {
            // Generate debug report if in debug mode
            if (process.env.DEBUG) {
                await Debug.generateDebugReport(
                    `runMultipleAgentsTest-${processName}`,
                    null, // PID not available at this level
                    agentNames.join(','),
                    error
                );
            }
            throw error;
        }
    }

    /**
     * Validates multi-agent test results based on options
     * @private
     * @param {Object} t - AVA test object
     * @param {MultipleAgentsTestResult} result - Multi-agent test result
     * @param {Object} options - Validation options
     */
    static async _validateMultiAgentResult(t, result, options) {
        if (options.assertSuccess) {
            const message = options.successMessage || `All agents should inject successfully`;
            t.true(
                result.injections.every((i) => i.success),
                message
            );
        }

        if (options.assertValidation) {
            for (const { agentName, injection } of result.injections) {
                t.true(injection.validation?.valid ?? true, `${agentName} validation failed`);
            }
        }
    }

    /**
     * Creates multi-agent test options with proper defaults
     * @private
     * @param {Object} options - User-provided options
     * @returns {Object} Complete options with defaults applied
     */
    static _createMultiAgentOptions(options = {}) {
        return {
            timeout: this.getTimeout('DEFAULT'),
            assertSuccess: true,
            assertValidation: true,
            successMessage: null,
            agentDelay: this.getTimeout('MULTI_AGENT_DELAY'),
            healthCheck: this.TEST_CONFIGS.MULTI_AGENT_HEALTH_CHECK,
            restartOnFailure: this.TEST_CONFIGS.MULTI_AGENT_RESTART_ON_FAILURE,
            maxMemoryMb: this.TEST_CONFIGS.RESOURCES.MAX_MEMORY_MB,
            maxRestarts: this.TEST_CONFIGS.RETRIES.MULTI_AGENT_MAX_RESTARTS,
            ...options,
        };
    }

    /**
     * Restarts a stub process with the same configuration
     * @param {string} processName - Name of the process to restart
     * @param {import('./Frida.js').StubProcess} oldStub - Old stub process to cleanup
     * @returns {Promise<import('./Frida.js').StubProcess>} New stub process
     * @private
     */
    static async _restartStub(processName, oldStub) {
        if (process.env.DEBUG) {
            console.log(`[DEBUG] Restarting ${processName} stub process`);
        }

        // Cleanup old stub
        if (oldStub) {
            await oldStub.stop?.();
            // Additional wait for cleanup
            await new Promise((resolve) => setTimeout(resolve, this.getTimeout('CLEANUP_DELAY')));
        }

        // Start new stub
        const newStub = await Frida.startStub(processName);

        if (process.env.DEBUG) {
            console.log(`[DEBUG] ${processName} stub restarted with new PID: ${newStub.pid}`);
        }

        return newStub;
    }

    /**
     * Cleans up all managed processes.
     * Delegates to FridaHelper for unified cleanup with a 10-second timeout.
     * Safe to call multiple times - will not throw errors.
     *
     * @returns {Promise<void>} Resolves when cleanup is complete or timeout is reached
     * @example
     * test.after.always(async () => {
     *     await Utils.cleanup();
     * });
     */
    static async cleanup() {
        if (process.env.DEBUG) {
            console.log('[DEBUG] Utils.cleanup() called');
        }
        try {
            // Delegate to FridaHelper for cleanup with timeout to prevent hanging
            await Promise.race([
                Frida.cleanupAllProcesses(),
                new Promise((_, reject) =>
                    setTimeout(() => reject(new Error('TestUtils cleanup timeout')), 10000)
                ),
            ]);

            if (process.env.DEBUG) {
                console.log('[DEBUG] Utils.cleanup() completed successfully');
            }
        } catch (error) {
            if (process.env.DEBUG) {
                console.log('[DEBUG] Utils.cleanup() failed:', error?.message || 'Unknown error');
            }
            // Don't throw error to prevent test suite from hanging
            // Just log the error and continue with test execution
        }
    }

    /**
     * Creates an agent test configuration with proper defaults
     * @param {string} agentName - Name of the agent
     * @param {string} processName - Name of the target process
     * @param {Object} [options={}] - Additional test options
     * @returns {AgentTestConfig} Complete agent test configuration
     */
    static createAgentTestConfig(agentName, processName, options = {}) {
        const agentConfig = getAgentConfig(agentName);

        return {
            agentName,
            processName,
            timeout: options.timeout || this.getTimeout('DEFAULT'),
            params: { ...agentConfig.params, ...options.params },
            customValidation: options.customValidation || null,
        };
    }

    /**
     * Gets agent-specific configuration with defaults
     * @param {string} agentName - Name of the agent
     * @returns {Object} Agent configuration with timeout and retries
     */
    static getAgentConfig(agentName) {
        const configs = {};

        return (
            configs[agentName] || {
                timeout: this.getTimeout('DEFAULT'),
                retries: 3,
            }
        );
    }
}

export default Utils;

// Type exports for IDE autocompletion
/**
 * @typedef {import('./Frida.js').InjectionResult} InjectionResult
 * @typedef {import('./Frida.js').StubProcess} StubProcess
 * @typedef {import('./Frida.js').ValidationResult} ValidationResult
 */
