import { Frida } from './Frida.js';

/**
 * ErrorHandler - Handles error processing and context creation
 */
export class ErrorHandler {
    static createErrorContext(error, context = {}) {
        return {
            type: Frida._classifyError(error.message),
            message: error.message,
            stack: error.stack,
            context: {
                pid: context.pid,
                agentName: context.agentName,
                processName: context.processName,
                timestamp: new Date().toISOString(),
                ...context,
            },
            recoverable: Frida._isRecoverableError(Frida._classifyError(error.message)),
        };
    }

    static async handleError(error, context, retryStrategy) {
        const errorContext = this.createErrorContext(error, context);

        if (process.env.DEBUG) {
            console.log('[ERROR]', errorContext);
        }

        if (
            errorContext.recoverable &&
            retryStrategy.shouldRetry(context.attempt || 1, errorContext.type)
        ) {
            const delay = retryStrategy.getDelay(context.attempt || 1);
            await new Promise((resolve) => setTimeout(resolve, delay));

            return {
                shouldRetry: true,
                delay,
                errorContext,
            };
        }

        return {
            shouldRetry: false,
            errorContext,
        };
    }

    static formatErrorMessage(errorContext) {
        const { type, message, context } = errorContext;

        let formatted = `${type}: ${message}`;

        // Add helpful suggestions based on error type
        if (type === Frida.ERROR_TYPES.JAVA_BRIDGE) {
            formatted += '\nSuggestion: Try increasing AGENT_DELAY environment variable';
        }
        if (type === Frida.ERROR_TYPES.MEMORY) {
            formatted += '\nSuggestion: Try increasing maxMemoryMb in test options';
        }

        if (context.pid) formatted += ` (PID: ${context.pid})`;
        if (context.agentName) formatted += ` (Agent: ${context.agentName})`;
        if (context.processName) formatted += ` (Process: ${context.processName})`;

        return formatted;
    }
}
