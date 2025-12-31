import { Frida } from './Frida.js';
import { Utils } from './Utils.js';

/**
 * Retry - Manages retry logic for operations
 */
export class Retry {
    constructor(options = {}) {
        this.maxRetries = options.maxRetries ?? Utils.TEST_CONFIGS.RETRIES.DEFAULT;
        this.baseDelay = options.baseDelay ?? Utils.getTimeout('RETRY_BASE_DELAY');
        this.backoff = options.backoff ?? Utils.TEST_CONFIGS.RETRIES.BACKOFF;
    }

    shouldRetry(attempt, errorType) {
        if (attempt >= this.maxRetries) return false;

        const recoverableErrors = [
            Frida.ERROR_TYPES.JAVA_BRIDGE,
            Frida.ERROR_TYPES.PROCESS_TERMINATED,
            Frida.ERROR_TYPES.MEMORY,
        ];

        return recoverableErrors.includes(errorType);
    }

    getDelay(attempt) {
        return this.baseDelay * Math.pow(this.backoff, attempt - 1);
    }
}
