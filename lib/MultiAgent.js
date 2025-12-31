import { Frida } from './Frida.js';
import { Utils } from './Utils.js';
import { ProcessHealth } from './ProcessHealth.js';
import { Retry } from './Retry.js';
import { Injection } from './Injection.js';

/**
 * MultiAgent - Orchestrates multi-agent injection tests
 */
export class MultiAgent {
    constructor(options = {}) {
        this.healthManager = new ProcessHealth(options.health);
        this.retryStrategy = new Retry(options.retries);
        this.scheduler = new Injection(options.scheduling);
    }

    async execute(processName, agentNames, options = {}) {
        let stub = null;
        let restartCount = 0;

        try {
            stub = await Frida.startStub(processName);
            await this.waitForProcessReady(stub);

            return await this.scheduler.scheduleInjections(
                stub,
                agentNames,
                this.healthManager,
                this.retryStrategy,
                { ...options, restartCount }
            );
        } finally {
            if (stub) await stub.stop?.();
        }
    }

    async waitForProcessReady() {
        await new Promise((resolve) => setTimeout(resolve, Utils.getTimeout('PROCESS_WAIT')));
    }
}
