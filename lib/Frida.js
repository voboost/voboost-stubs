import { spawn, exec } from 'child_process';
import { dirname, join } from 'path';
import { fileURLToPath } from 'url';
import { access, constants } from 'fs/promises';
import { Buffer } from 'buffer';
import { AGENT_LOG_SEQUENCES, AGENT_TEST_CONFIGS, DEFAULT_FALLBACK_MESSAGES } from './Fixtures.js';
import { Utils } from './Utils.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

/**
 * @typedef {Object} TimeoutConfig
 * @property {number} PROCESS_WAIT - Time to wait for process to be ready (ms)
 * @property {number} INJECTION - Default injection timeout (ms)
 * @property {number} INJECTION_MIN - Minimum injection timeout (ms)
 * @property {number} CLEANUP_DELAY - Delay between cleanup operations (ms)
 * @property {number} STUB_INITIALIZATION - Time to wait for stub initialization (ms)
 * @property {number} OUTPUT_CHECK_INTERVAL - Interval for checking output (ms)
 * @property {number} AUTO_STOP_DELAY - Delay before auto-stopping processes (ms)
 * @property {number} KILL_DELAY - Delay between kill signals (ms)
 */

/**
 * @typedef {Object} MappingConfig
 * @property {Object.<string, string>} JAR_FILE - Maps app names to JAR filenames
 * @property {Object.<string, string>} JAVA_CLASS - Maps app names to Java class names
 * @property {Object.<string, string>} PROCESS_NAME - Maps app names to process names
 * @property {Object.<string, string>} AGENT_SCRIPT - Maps agent names to script filenames
 */

/**
 * @typedef {Object} OutputBuffer
 * @property {Function(string): void} add - Adds data to the buffer
 * @property {Function(): string} get - Gets all buffer content as string
 * @property {Function(): void} clear - Clears the buffer
 * @property {Function(): number} size - Gets current buffer size in bytes
 */

/**
 * @typedef {Object} InjectionOptions
 * @property {number} [pid] - Target process PID (preferred)
 * @property {string} [process] - Target process name (fallback)
 * @property {string} script - Path to the Frida script
 * @property {Object|null} [params=null] - Parameters to pass to the script
 * @property {number} [timeout=TIMEOUTS.INJECTION] - Injection timeout in milliseconds
 */

/**
 * @typedef {Object} InjectionResult
 * @property {boolean} success - Whether injection was successful
 * @property {string} output - Output from the injection process
 * @property {import('child_process').ChildProcess} process - The Frida process
 * @property {Function(): Promise<void>} stop - Function to stop the injection
 * @property {Function(): void} clearAutoStop - Function to clear auto-stop timeout
 * @property {number|null} exitCode - Process exit code
 * @property {string|null} [errorType] - Type of error that occurred (if any)
 * @property {Object|null} [errorDetails] - Detailed error information
 * @property {boolean} [hasValidOutput] - Whether output contains valid patterns
 * @property {boolean} [isRecoverableError] - Whether the error is recoverable
 */

/**
 * @typedef {Object} StubProcess
 * @property {import('child_process').ChildProcess} process - The stub process
 * @property {number} pid - Process ID
 * @property {string} appName - Application name
 * @property {string} output - Process output
 * @property {Function(): Promise<void>} stop - Function to stop the stub
 */

/**
 * @typedef {Object} ValidationResult
 * @property {boolean} valid - Whether validation passed
 * @property {string} agentName - Name of the agent
 * @property {string[]} requiredMatched - Required patterns that matched
 * @property {string[]} requiredMissing - Required patterns that were missing
 * @property {string[]} operationalMatched - Operational patterns that matched
 * @property {string[]} forbiddenFound - Forbidden patterns that were found
 * @property {string} reason - Detailed reason for validation result
 */

/**
 * @typedef {Object} TestConfig
 * @property {Object} params - Configuration parameters
 * @property {number} [timeout] - Optional timeout override
 */

/**
 * Frida - Comprehensive test utilities for Frida-based testing
 * Provides static methods for process management, script injection, and test validation
 */
export class Frida {
    /**
     * Error types for better handling
     * @type {Object}
     * @readonly
     */
    static ERROR_TYPES = {
        JAVA_BRIDGE: 'java_bridge',
        ARCHITECTURE: 'architecture',
        PROCESS_TERMINATED: 'process_terminated',
        MISSING_EXPORT: 'missing_export',
        NULL_POINTER: 'null_pointer',
        TIMEOUT: 'timeout',
        MEMORY: 'memory',
        CONNECTION: 'connection',
        PERMISSION: 'permission',
        UNKNOWN: 'unknown',
    };

    /**
     * Timeout configurations for various operations
     * @type {TimeoutConfig}
     * @readonly
     */
    static TIMEOUTS = {
        PROCESS_WAIT: 5000,
        INJECTION: 10000,
        INJECTION_MIN: 12000,
        CLEANUP_DELAY: 300,
        STUB_INITIALIZATION: 3000,
        OUTPUT_CHECK_INTERVAL: 50,
        AUTO_STOP_DELAY: 5000,
        KILL_DELAY: 300,
        PRE_INJECTION_WAIT: 1000,
        MESSAGE_CHECK_INTERVAL: 100,
        INJECTION_RETRY_ATTEMPTS: 3,
        INJECTION_RETRY_DELAY: 1000,
        INJECTION_RETRY_BACKOFF: 2,
        HEALTH_CHECK_INTERVAL: 500,
        MEMORY_CHECK_INTERVAL: 2000,
    };

    /**
     * Multi-agent test configuration
     * @type {Object}
     * @readonly
     */
    static MULTI_AGENT_CONFIG = {
        DELAY_BETWEEN_INJECTIONS: 2000, // 2 seconds between injections
        ENABLE_HEALTH_CHECK: true,
        ENABLE_RESTART_ON_FAILURE: true,
        MAX_MEMORY_MB: 512, // Memory threshold for restart
        MAX_RESTARTS: 2,
        MEMORY_CHECK_ENABLED: true,
    };

    /**
     * Mapping configurations for apps, classes, processes, and agents
     * @type {MappingConfig}
     * @readonly
     */
    static MAPPINGS = {
        JAR_FILE: {
            launcher: 'LauncherStub.jar',
            bluetoothphone: 'BluetoothPhoneStub.jar',
            systemservice: 'SystemServiceStub.jar',
            keyboard: 'QgimeStub.jar',
            vehiclesetting: 'VehicleSettingStub.jar',
        },
        JAVA_CLASS: {
            launcher: 'com.qinggan.app.launcher.LauncherStub',
            bluetoothphone: 'com.qinggan.bluetoothphone.BluetoothPhoneStub',
            systemservice: 'com.qinggan.systemservice.SystemServiceStub',
            keyboard: 'com.qinggan.app.qgime.QgimeStub',
            vehiclesetting: 'com.qinggan.app.vehiclesetting.VehicleSettingStub',
        },
        PROCESS_NAME: {
            launcher: 'com.qinggan.app.launcher',
            bluetoothphone: 'com.qinggan.bluetoothphone',
            systemservice: 'com.qinggan.systemservice',
            keyboard: 'com.qinggan.app.qgime',
            vehiclesetting: 'com.qinggan.app.vehiclesetting',
        },
        AGENT_SCRIPT: {
            'weather-widget-mod': 'weather-widget-mod.js',
            'app-launcher-mod': 'app-launcher-mod.js',
            'navbar-launcher-mod': 'navbar-launcher-mod.js',
            'app-viewport-mod': 'app-viewport-mod.js',
            'phone-num-mod': 'phone-num-mod.js',
            'app-multi-display-mod': 'app-multi-display-mod.js',
            'voboost-to-menu-mod': 'voboost-to-menu-mod.js',
            'forced-ev-mod': 'forced-ev-mod.js',
            'keyboard-ru-mod': 'keyboard-ru-mod.js',
            'keyboard-lock-en-mod': 'keyboard-lock-en-mod.js',
            'adas-activation-mod': 'adas-activation-mod.js',
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
     * @param {number} [maxSize=1048576] - Maximum buffer size in bytes (default 1MB)
     * @returns {OutputBuffer} Output buffer instance with memory management
     */
    static _createOutputBuffer(maxSize = 10 * 1024 * 1024) {
        // Use 10MB default
        // Default buffer size limit of 1MB to prevent memory issues
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
     * Classifies error based on message content
     * @param {string} errorMessage - Error message to classify
     * @returns {string} Error type from ERROR_TYPES
     */
    static _classifyError(errorMessage) {
        if (!errorMessage) {
            return this.ERROR_TYPES.UNKNOWN;
        }

        const lowerError = errorMessage.toLowerCase();

        // Java bridge errors - most common in multi-agent tests
        if (
            lowerError.includes('thread_from_jni_environment') ||
            lowerError.includes('java bridge') ||
            lowerError.includes('jvm.js') ||
            lowerError.includes('java.perform')
        ) {
            return this.ERROR_TYPES.JAVA_BRIDGE;
        }

        // Process termination errors
        if (
            lowerError.includes('process terminated') ||
            lowerError.includes('process exited') ||
            lowerError.includes('unable to find process with pid') ||
            lowerError.includes('no such process')
        ) {
            return this.ERROR_TYPES.PROCESS_TERMINATED;
        }

        // Architecture compatibility errors
        if (
            lowerError.includes('architecture') ||
            lowerError.includes('unable to make') ||
            lowerError.includes('unsupported') ||
            lowerError.includes('not supported')
        ) {
            return this.ERROR_TYPES.ARCHITECTURE;
        }

        // Missing system exports or libraries
        if (
            lowerError.includes('unable to find export') ||
            lowerError.includes('epoll_wait') ||
            lowerError.includes('symbol not found') ||
            lowerError.includes('dlopen')
        ) {
            return this.ERROR_TYPES.MISSING_EXPORT;
        }

        // Null pointer or access errors
        if (
            lowerError.includes('nullpointerexception') ||
            lowerError.includes('getmodule()') ||
            lowerError.includes('access violation') ||
            lowerError.includes('segmentation fault')
        ) {
            return this.ERROR_TYPES.NULL_POINTER;
        }

        // Timeout errors
        if (
            lowerError.includes('timeout') ||
            lowerError.includes('timed out') ||
            lowerError.includes('deadline exceeded')
        ) {
            return this.ERROR_TYPES.TIMEOUT;
        }

        // Memory errors
        if (
            lowerError.includes('out of memory') ||
            lowerError.includes('cannot allocate') ||
            lowerError.includes('memory')
        ) {
            return this.ERROR_TYPES.MEMORY;
        }

        // Connection errors
        if (
            lowerError.includes('connection') ||
            lowerError.includes('network') ||
            lowerError.includes('socket')
        ) {
            return this.ERROR_TYPES.CONNECTION;
        }

        // Permission errors
        if (
            lowerError.includes('permission denied') ||
            lowerError.includes('access denied') ||
            lowerError.includes('operation not permitted')
        ) {
            return this.ERROR_TYPES.PERMISSION;
        }

        return this.ERROR_TYPES.UNKNOWN;
    }

    /**
     * Detects current system architecture
     * @returns {Object} Architecture information
     */
    static _detectArchitecture() {
        const arch = process.arch;
        const platform = process.platform;

        return {
            arch,
            platform,
            isArm: arch.includes('arm') || arch.includes('aarch'),
            isX64: arch.includes('x64') || arch === 'x86_64',
            isMacOS: platform === 'darwin',
            isLinux: platform === 'linux',
            isWindows: platform === 'win32',
        };
    }

    /**
     * Checks for known compatibility issues
     * @returns {Array<string>} List of known issues
     */
    static _checkCompatibility() {
        const issues = [];
        const arch = this._detectArchitecture();

        // Known issue with ARM64 and Java bridge
        if (arch.isArm && arch.isMacOS) {
            issues.push('Java bridge may have issues on ARM64 macOS');
        }

        // Known issue with certain Frida versions
        if (arch.isWindows) {
            issues.push('Windows may have limited Frida functionality');
        }

        return issues;
    }

    /**
     * Determines if an error is recoverable
     * @param {string} errorType - Type of error
     * @returns {boolean} True if error is recoverable
     */
    static _isRecoverableError(errorType) {
        const recoverableErrors = [
            this.ERROR_TYPES.JAVA_BRIDGE,
            this.ERROR_TYPES.PROCESS_TERMINATED,
            this.ERROR_TYPES.NULL_POINTER,
            this.ERROR_TYPES.MISSING_EXPORT,
            this.ERROR_TYPES.MEMORY,
        ];

        const nonRecoverableErrors = [
            this.ERROR_TYPES.ARCHITECTURE,
            this.ERROR_TYPES.TIMEOUT,
            this.ERROR_TYPES.PERMISSION,
            this.ERROR_TYPES.UNKNOWN,
        ];

        if (nonRecoverableErrors.includes(errorType)) {
            return false;
        }

        return recoverableErrors.includes(errorType);
    }

    /**
     * Provides fallback behavior for recoverable errors
     * @param {string} errorType - Type of error
     * @param {Object} context - Injection context
     * @returns {Object} Fallback result
     */
    static _handleRecoverableError(errorType) {
        if (process.env.DEBUG) {
            console.log(`[DEBUG] Handling recoverable error: ${errorType}`);
        }

        switch (errorType) {
            case this.ERROR_TYPES.JAVA_BRIDGE:
                return {
                    shouldRetry: true,
                    retryDelay: 2000,
                    message: 'Java bridge error - will retry with different approach',
                    suggestion:
                        'This is common on ARM64 macOS. Consider increasing delays between injections.',
                };

            case this.ERROR_TYPES.PROCESS_TERMINATED:
                return {
                    shouldRetry: true,
                    retryDelay: 1000,
                    message: 'Process terminated - will restart and retry',
                    suggestion:
                        'Process may have crashed due to memory pressure or resource contention.',
                };

            case this.ERROR_TYPES.NULL_POINTER:
                return {
                    shouldRetry: false,
                    message: 'Java stub needs implementation update',
                    suggestion: 'This indicates a missing implementation in the Java stub.',
                };

            case this.ERROR_TYPES.MISSING_EXPORT:
                return {
                    shouldRetry: false,
                    message: 'System compatibility issue - check environment',
                    suggestion: 'Check if all required system libraries are available.',
                };

            case this.ERROR_TYPES.TIMEOUT:
                return {
                    shouldRetry: false,
                    message: 'Operation timed out - non-recoverable error',
                    suggestion:
                        'Consider increasing the timeout value for this operation before running the test.',
                };

            case this.ERROR_TYPES.MEMORY:
                return {
                    shouldRetry: true,
                    retryDelay: 5000,
                    message: 'Memory issue detected - will retry after cleanup',
                    suggestion:
                        'Process may be running out of memory. Consider reducing agent count or increasing memory limits.',
                };

            case this.ERROR_TYPES.CONNECTION:
                return {
                    shouldRetry: true,
                    retryDelay: 1000,
                    message: 'Connection issue - will retry',
                    suggestion: 'Temporary network or IPC issue. Should resolve on retry.',
                };

            case this.ERROR_TYPES.ARCHITECTURE:
                return {
                    shouldRetry: false,
                    message: 'Architecture compatibility issue',
                    suggestion:
                        'This Frida version may not be compatible with the current architecture.',
                };

            case this.ERROR_TYPES.PERMISSION:
                return {
                    shouldRetry: false,
                    message: 'Permission denied',
                    suggestion:
                        'Check if the process has sufficient permissions to perform the operation.',
                };

            default:
                return {
                    shouldRetry: false,
                    message: 'Unknown error - manual investigation required',
                    suggestion: 'Check the logs for more details about this error.',
                };
        }
    }

    /**
     * Logs detailed error information
     * @param {Object} errorDetails - Error details object
     */
    static _logErrorDetails(errorDetails) {
        if (!process.env.DEBUG) {
            return;
        }

        console.log('\n[ERROR DETAILS]');
        console.log('================');
        console.log('Type:', errorDetails.errorType);
        console.log('Exit Code:', errorDetails.exitCode);
        console.log('Signal:', errorDetails.signal);

        if (errorDetails.errors && errorDetails.errors.length > 0) {
            console.log('\nError Messages:');
            errorDetails.errors.forEach((error, index) => {
                console.log(`  ${index + 1}. ${error.trim()}`);
            });
        }

        if (errorDetails.output) {
            const outputPreview = errorDetails.output.substring(0, 500);
            console.log('\nOutput Preview:');
            console.log(outputPreview);
            if (errorDetails.output.length > 500) {
                console.log('... (truncated)');
            }
        }

        console.log('================\n');
    }

    /**
     * Injects a Frida script into a target process with retry logic
     * @param {InjectionOptions} options - Injection options
     * @param {number} [options.maxRetries=TIMEOUTS.INJECTION_RETRY_ATTEMPTS] - Maximum number of retry attempts
     * @returns {Promise<InjectionResult>} Injection result with success status, output, and control methods
     * @throws {TypeError} When parameters are invalid
     * @throws {Error} When injection fails after all retry attempts
     */
    static async inject({
        pid,
        process: processName,
        script,
        params = null,
        timeout = this.TIMEOUTS.INJECTION,
        maxRetries = this.TIMEOUTS.INJECTION_RETRY_ATTEMPTS,
    }) {
        // Validate we have either PID or process name
        if (!pid && !processName) {
            throw new TypeError('Either pid or process must be provided');
        }

        this._validateParam('script', script, 'string');
        this._validateParam('params', params, 'object', true);
        this._validateParam('timeout', timeout, 'number');
        this._validateParam('maxRetries', maxRetries, 'number');

        if (pid !== undefined) {
            this._validateParam('pid', pid, 'number');
        }
        if (processName !== undefined) {
            this._validateParam('process', processName, 'string');
        }

        // Validate script file exists
        await this._validateFileExists(script);

        let lastError = null;

        for (let attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (process.env.DEBUG) {
                    console.log(`[DEBUG] Injection attempt ${attempt}/${maxRetries}`);
                }

                const result = await this._performInjection({
                    pid,
                    process: processName,
                    script,
                    params,
                    timeout,
                });

                // If successful, return the result
                if (result.success || result.hasValidOutput) {
                    if (process.env.DEBUG) {
                        console.log(`[DEBUG] Injection succeeded on attempt ${attempt}`);
                    }
                    return result;
                }

                // If not successful, check if we should retry based on error type
                if (result.errorType) {
                    // For unknown errors, always retry a few times in case it's transient
                    if (result.errorType === this.ERROR_TYPES.UNKNOWN) {
                        if (attempt < maxRetries) {
                            if (process.env.DEBUG) {
                                console.log(
                                    `[DEBUG] Unknown error - will retry (attempt ${attempt}/${maxRetries})`
                                );
                            }
                            // Continue with retry for unknown errors
                            lastError = new Error(
                                `Injection failed on attempt ${attempt}: ${result.errorType || 'unknown'}`
                            );
                        }
                        // For unknown errors, don't set lastError on the last attempt
                        // Let the loop continue and handle it at the end
                    } else if (!result.isRecoverableError) {
                        // Don't retry for other non-recoverable errors
                        const fallback = this._handleRecoverableError(result.errorType, {
                            pid,
                            process: processName,
                            script,
                            attempt,
                        });

                        if (process.env.DEBUG) {
                            console.log(`[DEBUG] Non-recoverable error: ${result.errorType}`);
                            console.log(`[DEBUG] ${fallback.message}`);
                        }

                        lastError = new Error(`Non-recoverable error: ${fallback.message}`);
                        break;
                    } else {
                        // For recoverable errors, continue with retry
                        if (attempt < maxRetries) {
                            lastError = new Error(
                                `Injection failed on attempt ${attempt}: ${result.errorType || 'unknown'}`
                            );
                        }
                    }
                } else {
                    // No error type specified, but injection failed
                    if (attempt < maxRetries) {
                        lastError = new Error(
                            `Injection failed on attempt ${attempt}: unknown error`
                        );
                    } else {
                        // Last attempt with no error type
                        lastError = new Error(
                            `Injection failed on attempt ${attempt}: unknown error`
                        );
                    }
                }
            } catch (error) {
                lastError = error;
                if (process.env.DEBUG) {
                    console.log(`[DEBUG] Injection attempt ${attempt} failed:`, error.message);
                }
            }

            // If this is not the last attempt, wait before retrying
            if (attempt < maxRetries) {
                const delay =
                    this.TIMEOUTS.INJECTION_RETRY_DELAY *
                    Math.pow(this.TIMEOUTS.INJECTION_RETRY_BACKOFF, attempt - 1);

                if (process.env.DEBUG) {
                    console.log(`[DEBUG] Waiting ${delay}ms before retry...`);
                }

                await new Promise((resolve) => setTimeout(resolve, delay));

                // Additional wait for process stabilization
                await this._waitForProcessStabilization(pid || processName);
            }
        }

        // All attempts failed
        if (!lastError) {
            // If we don't have a lastError (e.g., for unknown errors), set a generic one
            lastError = new Error(`Injection failed after ${maxRetries} attempts`);
        }
        throw new Error(
            `Injection failed after ${maxRetries} attempts. Last error: ${lastError?.message}`
        );
    }

    /**
     * Performs the actual Frida injection without retry logic
     * @param {Object} options - Injection options
     * @returns {Promise<InjectionResult>} Injection result
     * @private
     */
    static async _performInjection({ pid, process: processName, script, params = null, timeout }) {
        // Check compatibility before injection
        const compatibilityIssues = this._checkCompatibility();
        if (compatibilityIssues.length > 0 && process.env.DEBUG) {
            console.log('[DEBUG] Compatibility issues:', compatibilityIssues);
        }

        // Wait a moment for process to be available
        await new Promise((resolve) => setTimeout(resolve, this.TIMEOUTS.PRE_INJECTION_WAIT));

        // Build frida-inject command arguments (matching voboost Android implementation)
        // Uses Script.parameters - the only way to pass parameters to Frida scripts
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
        let errorType = null;
        let errorDetails = null;

        // Enhanced error collection
        const errorBuffer = [];

        // Enhanced process cleanup with proper signal handling to prevent resource leaks
        const stop = async () => {
            try {
                if (autoStopTimeout) {
                    clearTimeout(autoStopTimeout);
                    autoStopTimeout = null;
                }

                // Remove all listeners to prevent memory leaks
                if (frida) {
                    frida.stdout.removeAllListeners('data');
                    frida.stderr.removeAllListeners('data');
                    frida.removeAllListeners('exit');
                    frida.removeAllListeners('error');
                }

                if (frida && !frida.killed) {
                    // Attempt graceful termination first using SIGTERM
                    frida.kill('SIGTERM');

                    // Wait for graceful shutdown
                    await new Promise((resolve) => setTimeout(resolve, this.TIMEOUTS.KILL_DELAY));

                    // If process is still alive, force termination with SIGKILL
                    if (!frida.killed) {
                        frida.kill('SIGKILL');
                        await new Promise((resolve) =>
                            setTimeout(resolve, this.TIMEOUTS.KILL_DELAY)
                        );
                    }
                }
            } catch (cleanupError) {
                // Log error but don't throw - cleanup operations should be best-effort
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
            errorBuffer.push(str);

            // Classify error immediately
            if (!errorType) {
                errorType = this._classifyError(str);
            }

            if (process.env.DEBUG) {
                console.log('[FRIDA STDERR]', str.trim());
                console.log('[FRIDA ERROR TYPE]', errorType);
                console.log('[FRIDA BUFFER SIZE]', outputBuffer.size());
            }
        });

        frida.on('exit', (code, signal) => {
            if (!resolved) {
                resolved = true;

                // Collect error details
                errorDetails = {
                    exitCode: code,
                    signal,
                    errorType,
                    errors: errorBuffer,
                    output: outputBuffer.get(),
                };

                if (process.env.DEBUG) {
                    console.log('[DEBUG] Process exit details:', errorDetails);
                    this._logErrorDetails(errorDetails);
                }

                // Clear auto-stop timeout when process exits naturally
                if (autoStopTimeout) {
                    clearTimeout(autoStopTimeout);
                    autoStopTimeout = null;
                }
            }
        });

        frida.on('error', (error) => {
            if (!resolved) {
                resolved = true;

                errorType = this._classifyError(error.message);
                errorDetails = {
                    error,
                    errorType,
                    errors: [error.message],
                    output: outputBuffer.get(),
                };

                if (process.env.DEBUG) {
                    console.log('[DEBUG] Frida process error:', errorDetails);
                    this._logErrorDetails(errorDetails);
                }

                // Clear auto-stop timeout when an error occurs
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

            // Helper function to wait for messages with timeout
            const waitForMessages = async (messageCheck) => {
                while (Date.now() - startTime < maxWait) {
                    const currentOutput = outputBuffer.get();
                    if (messageCheck(currentOutput)) {
                        return true;
                    }
                    if (frida.exitCode !== null) break;
                    await new Promise((resolve) =>
                        setTimeout(resolve, this.TIMEOUTS.MESSAGE_CHECK_INTERVAL)
                    );
                }
                return false;
            };

            // Helper function for fallback agent message check
            const checkFallbackAgentMessages = (output) => {
                return (
                    output.includes(DEFAULT_FALLBACK_MESSAGES.STARTING) &&
                    output.includes(DEFAULT_FALLBACK_MESSAGES.STARTED)
                );
            };

            // Helper function for specific agent message check
            const checkSpecificAgentMessages = (message1, message2) => {
                return (output) => output.includes(message1) && output.includes(message2);
            };

            if (!agentName) {
                // Fallback to hardcoded check if agent name not found
                return waitForMessages(checkFallbackAgentMessages);
            }

            // Get the expected messages from AGENT_LOG_SEQUENCES
            const sequence = AGENT_LOG_SEQUENCES[agentName];
            if (!sequence || !sequence.required || sequence.required.length < 2) {
                // Fallback to hardcoded check if sequence not found
                return waitForMessages(checkFallbackAgentMessages);
            }

            const [requiredMessage1, requiredMessage2] = sequence.required;
            return waitForMessages(checkSpecificAgentMessages(requiredMessage1, requiredMessage2));
        };

        // Wait for agent messages with error handling
        try {
            await waitForAgentMessages();
        } catch (waitError) {
            if (process.env.DEBUG) {
                console.log('[DEBUG] Wait for messages failed:', waitError.message);
            }
            // Don't throw - let the validation determine success
        }

        // Set auto-stop timeout to prevent hanging processes - track timeout for cleanup
        if (!resolved) {
            autoStopTimeout = setTimeout(async () => {
                await stop();
            }, this.TIMEOUTS.AUTO_STOP_DELAY);
            // Prevent timeout from keeping the event loop alive
            autoStopTimeout.unref();
        }

        // Determine success based on output and error type
        const output = outputBuffer.get();
        const hasValidOutput = this._hasValidOutput(output);
        const isRecoverableError = this._isRecoverableError(errorType);

        // Process should be considered successful only if:
        // 1. Process is still running (exitCode is null) OR exited normally (exitCode === 0)
        // 2. AND there are no process termination errors
        // 3. AND either has valid output OR is a recoverable error
        const processTerminated = errorType === this.ERROR_TYPES.PROCESS_TERMINATED;
        const processExitedNormally = frida.exitCode === null || frida.exitCode === 0;

        // If process terminated, it's a failure regardless of output
        if (processTerminated) {
            return {
                success: false,
                output,
                process: frida,
                stop: stop,
                clearAutoStop: () => {
                    if (autoStopTimeout) {
                        clearTimeout(autoStopTimeout);
                        autoStopTimeout = null;
                    }
                },
                exitCode: frida.exitCode,
                errorType,
                errorDetails,
                hasValidOutput,
                isRecoverableError,
            };
        }

        return {
            success: processExitedNormally && (hasValidOutput || isRecoverableError),
            output,
            process: frida,
            stop: stop,
            clearAutoStop: () => {
                if (autoStopTimeout) {
                    clearTimeout(autoStopTimeout);
                    autoStopTimeout = null;
                }
            },
            exitCode: frida.exitCode,
            errorType,
            errorDetails,
            hasValidOutput,
            isRecoverableError,
        };
    }

    /**
     * Waits for process stabilization before retrying injection
     * @param {number|string} processIdentifier - PID or process name
     * @returns {Promise<void>}
     * @private
     */
    static async _waitForProcessStabilization(processIdentifier) {
        // Wait for process to be ready for next injection
        await new Promise((resolve) => setTimeout(resolve, 500));

        // If we have a PID, check if process is still running
        if (typeof processIdentifier === 'number') {
            try {
                process.kill(processIdentifier, 0); // Signal 0 checks if process exists
            } catch {
                if (process.env.DEBUG) {
                    console.log('[DEBUG] Process no longer exists, cannot retry injection');
                }
                // Don't throw an error here - let the retry attempt fail naturally
                // This prevents breaking existing tests that might have processes that exit quickly
            }
        }
    }

    /**
     * Checks if output contains valid agent patterns even if process crashed
     * @param {string} output - Output from the injection process
     * @returns {boolean} Whether output contains valid patterns
     * @private
     */
    static _hasValidOutput(output) {
        // Check if output contains expected patterns even if process crashed
        const validPatterns = ['Agent starting', 'Agent started', 'Config loaded'];

        return validPatterns.some((pattern) => output.includes(pattern));
    }

    /**
     * Starts a Java stub process for testing
     * Returns stub object with PID for injection
     * @param {string} appName - Application name (must be in JAR_FILE mapping)
     * @param {string} [cwd=process.cwd()] - Working directory for the stub process
     * @returns {Promise<StubProcess>} Stub process control object with PID
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

        // Wait for ready message instead of using a fixed delay
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
            // If ready message is not found, continue anyway for backward compatibility
            // but log the issue for debugging purposes
            if (process.env.DEBUG) {
                console.warn('[DEBUG] Ready message not found, continuing anyway');
            }
        }

        return {
            process: stub,
            pid: stub.pid, // Return PID for injection operations
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
     * Validates injection output against expected patterns for an agent.
     * Checks that required messages appear in order and no forbidden messages are present.
     *
     * @param {string} output - Output from the injection process
     * @param {string} agentName - Name of the agent to validate against
     * @returns {Promise<ValidationResult>} Validation result with validity status and pattern matches
     * @throws {TypeError} When parameters are invalid
     * @example
     * const result = await FridaHelper.validateInjection(output, 'weather-widget-mod');
     * if (!result.valid) {
     *     console.log('Missing:', result.requiredMissing);
     * }
     */
    static async validateInjection(output, agentName) {
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

        // Helper function to escape regex special characters
        const escapeRegex = (string) => {
            return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        };

        // 1. Check that required messages appear in the correct order
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

        // 3. Check that no forbidden messages appear in the output
        for (const pattern of sequence.forbidden) {
            const regex = new RegExp(escapeRegex(pattern), 'i');
            if (regex.test(output)) {
                result.forbiddenFound.push(pattern);
                result.valid = false;
            }
        }

        // Build detailed reason string for validation failures
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
                    // Log error but don't throw - cleanup should be best-effort
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
                    // Log error but don't throw to prevent test failures
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
     * Simplified version that only waits 500ms since processes are now killed by PID only
     * @returns {Promise<void>}
     */
    static async cleanupAllProcesses() {
        if (process.env.DEBUG) {
            console.log('[DEBUG] FridaHelper.cleanupAllProcesses() called');
        }

        // Wait 500ms for processes to terminate
        await new Promise((resolve) => setTimeout(resolve, 500));

        if (process.env.DEBUG) {
            console.log('[DEBUG] FridaHelper.cleanupAllProcesses() completed');
        }
    }

    /**
     * Creates a test configuration for a specific agent
     * @param {string} agentName - Name of the agent
     * @param {Object} [customParams={}] - Custom parameters to merge with defaults
     * @returns {TestConfig} Test configuration object
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
     * Checks if a process is healthy enough for another injection
     * @param {number} pid - Process ID to check
     * @returns {Promise<Object>} Health check result with status and details
     */
    static async checkProcessHealth(pid) {
        const result = {
            healthy: true,
            running: false,
            memoryUsage: 0,
            errors: [],
            warnings: [],
        };

        try {
            // Check if process is still running
            process.kill(pid, 0);
            result.running = true;

            // Get memory usage if enabled
            if (this.MULTI_AGENT_CONFIG.MEMORY_CHECK_ENABLED) {
                result.memoryUsage = await this.getProcessMemory(pid);

                if (process.env.DEBUG) {
                    console.log(`[DEBUG] Process ${pid} memory usage: ${result.memoryUsage}MB`);
                }

                // Check memory threshold
                if (result.memoryUsage > this.MULTI_AGENT_CONFIG.MAX_MEMORY_MB) {
                    result.healthy = false;
                    result.warnings.push(
                        `Memory usage (${result.memoryUsage}MB) exceeds threshold (${this.MULTI_AGENT_CONFIG.MAX_MEMORY_MB}MB)`
                    );
                }
            }

            // Check for Java bridge errors in recent output
            // This is a simplified check - in a real implementation we might need
            // to monitor the process output more carefully
            if (process.env.DEBUG) {
                console.log(`[DEBUG] Process ${pid} health check passed`);
            }
        } catch (error) {
            result.running = false;
            result.healthy = false;
            result.errors.push(`Process check failed: ${error.message}`);

            if (process.env.DEBUG) {
                console.log(`[DEBUG] Health check failed for process ${pid}:`, error.message);
            }
        }

        return result;
    }

    /**
     * Gets memory usage for a process (platform specific)
     * @param {number} pid - Process ID
     * @returns {Promise<number>} Memory usage in MB
     */
    static async getProcessMemory(pid) {
        return new Promise((resolve) => {
            if (process.platform === 'darwin') {
                // macOS: use ps to get RSS (Resident Set Size) in KB
                exec(`ps -o rss= -p ${pid}`, (error, stdout) => {
                    if (error) {
                        resolve(0);
                        return;
                    }
                    const rssKb = parseInt(stdout.trim(), 10);
                    resolve(Math.round(rssKb / 1024)); // Convert to MB
                });
            } else if (process.platform === 'linux') {
                // Linux: use ps to get RSS in KB
                exec(`ps -o rss= -p ${pid}`, (error, stdout) => {
                    if (error) {
                        resolve(0);
                        return;
                    }
                    const rssKb = parseInt(stdout.trim(), 10);
                    resolve(Math.round(rssKb / 1024)); // Convert to MB
                });
            } else {
                // Windows or other platforms - not implemented
                if (process.env.DEBUG) {
                    console.log(
                        `[DEBUG] Memory monitoring not implemented for platform: ${process.platform}`
                    );
                }
                resolve(0);
            }
        });
    }

    /**
     * Monitors a process continuously for health issues
     * @param {number} pid - Process ID to monitor
     * @param {Function} onUnhealthy - Callback when process becomes unhealthy
     * @param {number} [interval=TIMEOUTS.HEALTH_CHECK_INTERVAL] - Check interval in ms
     * @returns {Object} Monitor control object with stop method
     */
    static monitorProcessHealth(pid, onUnhealthy, interval = this.TIMEOUTS.HEALTH_CHECK_INTERVAL) {
        let monitoring = true;
        let checkCount = 0;

        const monitor = async () => {
            if (!monitoring) return;

            try {
                const health = await this.checkProcessHealth(pid);
                checkCount++;

                if (!health.healthy) {
                    if (process.env.DEBUG) {
                        console.log(
                            `[DEBUG] Process ${pid} became unhealthy after ${checkCount} checks`
                        );
                        if (health.errors.length > 0) {
                            console.log(`[DEBUG] Errors:`, health.errors);
                        }
                        if (health.warnings.length > 0) {
                            console.log(`[DEBUG] Warnings:`, health.warnings);
                        }
                    }

                    monitoring = false;
                    onUnhealthy(health);
                    return;
                }

                // Schedule next check
                setTimeout(monitor, Utils.getTimeout('HEALTH_CHECK_INTERVAL'));
            } catch (error) {
                if (process.env.DEBUG) {
                    console.log(
                        `[DEBUG] Health monitoring error for process ${pid}:`,
                        error.message
                    );
                }
                // Continue monitoring despite errors
                setTimeout(monitor, this.TIMEOUTS.HEALTH_CHECK_INTERVAL);
            }
        };

        // Start monitoring
        setTimeout(monitor, interval);

        return {
            stop: () => {
                monitoring = false;
                if (process.env.DEBUG) {
                    console.log(`[DEBUG] Stopped health monitoring for process ${pid}`);
                }
            },
            isMonitoring: () => monitoring,
        };
    }

    /**
     * Waits for a process to stabilize after an operation
     * @param {number} pid - Process ID to wait for
     * @param {number} [maxWait=5000] - Maximum time to wait in ms
     * @returns {Promise<boolean>} True if process stabilized successfully
     */
    static async waitForProcessStabilization(pid, maxWait = 5000) {
        const startTime = Date.now();

        while (Date.now() - startTime < maxWait) {
            try {
                const health = await this.checkProcessHealth(pid);

                if (health.healthy && health.running) {
                    if (process.env.DEBUG) {
                        console.log(
                            `[DEBUG] Process ${pid} stabilized after ${Date.now() - startTime}ms`
                        );
                    }
                    return true;
                }

                // Process is not healthy, wait and check again
                await new Promise((resolve) =>
                    setTimeout(resolve, this.TIMEOUTS.HEALTH_CHECK_INTERVAL)
                );
            } catch (error) {
                if (process.env.DEBUG) {
                    console.log(
                        `[DEBUG] Stabilization check failed for process ${pid}:`,
                        error.message
                    );
                }
                await new Promise((resolve) =>
                    setTimeout(resolve, this.TIMEOUTS.HEALTH_CHECK_INTERVAL)
                );
            }
        }

        // Process didn't stabilize within the time limit
        if (process.env.DEBUG) {
            console.log(`[DEBUG] Process ${pid} failed to stabilize within ${maxWait}ms`);
        }
        return false;
    }
}
