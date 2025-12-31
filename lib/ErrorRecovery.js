import { Frida } from './Frida.js';

/**
 * ErrorRecovery - Provides recovery strategies for different error types
 */
export class ErrorRecovery {
    async handle(errorContext) {
        switch (errorContext.type) {
            case Frida.ERROR_TYPES.JAVA_BRIDGE:
                return {
                    action: 'retry',
                    delay: 2000,
                    message: 'Java bridge error - will retry with increased delay',
                };
            case Frida.ERROR_TYPES.PROCESS_TERMINATED:
                return {
                    action: 'restart',
                    delay: 1000,
                    message: 'Process terminated - will restart and retry',
                };
            case Frida.ERROR_TYPES.MEMORY:
                return {
                    action: 'retry_with_cleanup',
                    delay: 5000,
                    message: 'Memory issue detected - will retry after cleanup',
                };
            case Frida.ERROR_TYPES.NULL_POINTER:
                return {
                    action: 'fail',
                    message: 'Java stub needs implementation update',
                };
            default:
                return {
                    action: 'retry',
                    delay: 1000,
                    message: 'Unknown error - will retry',
                };
        }
    }
}
