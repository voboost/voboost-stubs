import { Frida } from './Frida.js';
import { Utils } from './Utils.js';

/**
 * Injection - Schedules and manages agent injections
 */
export class Injection {
    constructor(options = {}) {
        this.agentDelay = options.agentDelay ?? Utils.getTimeout('MULTI_AGENT_DELAY');
    }

    async scheduleInjections(stub, agentNames, healthManager, retryStrategy, options = {}) {
        const injections = [];
        let restartCount = options.restartCount ?? 0;

        for (let i = 0; i < agentNames.length; i++) {
            const agentName = agentNames[i];

            // Health check before injection (except first agent)
            if (i > 0 && healthManager.enabled) {
                const shouldRestart = await healthManager.shouldRestart(
                    stub.pid,
                    options.maxRestarts ?? Utils.TEST_CONFIGS.RETRIES.MULTI_AGENT_MAX_RESTARTS,
                    restartCount
                );

                if (shouldRestart) {
                    restartCount++;
                }
            }

            try {
                const injection = await this.injectAgent(stub.pid, agentName, options);
                injections.push({
                    agentName,
                    injection,
                    success: injection.success,
                });
            } catch (error) {
                injections.push({
                    agentName,
                    injection: { success: false, error: error.message },
                    success: false,
                });
            }

            // Delay between injections (except after last)
            if (i < agentNames.length - 1 && this.agentDelay > 0) {
                await new Promise((resolve) => setTimeout(resolve, this.agentDelay));
            }
        }

        // Validate all injections
        await this.validateInjections(injections);

        return { stub, injections, pid: stub.pid };
    }

    async injectAgent(pid, agentName, options = {}) {
        return await Utils._injectAgent(
            pid,
            agentName,
            options.timeout ?? Utils.getTimeout('DEFAULT')
        );
    }

    async validateInjections(injections) {
        const validationPromises = injections.map(async ({ agentName, injection }) => {
            try {
                const validation = await Frida.validateInjection(injection.output, agentName);
                injection.validation = validation;
                injection.success = injection.success && validation.valid;
            } catch (error) {
                injection.validation = { valid: false, reason: error.message };
                injection.success = false;
            }
        });

        await Promise.all(validationPromises);
    }
}
