import { Frida } from './Frida.js';
import { Utils } from './Utils.js';

/**
 * ProcessHealth - Manages process health monitoring
 */
export class ProcessHealth {
    constructor(options = {}) {
        this.enabled = options.enabled ?? true;
        this.maxMemoryMb = options.maxMemoryMb ?? Utils.TEST_CONFIGS.RESOURCES.MAX_MEMORY_MB;
        this.checkInterval = options.checkInterval ?? Utils.getTimeout('HEALTH_CHECK_INTERVAL');
    }

    async checkHealth(pid) {
        if (!this.enabled) return { healthy: true };

        try {
            process.kill(pid, 0); // Check if process exists

            if (this.maxMemoryMb > 0) {
                const memoryUsage = await Frida.getProcessMemory(pid);
                if (memoryUsage > this.maxMemoryMb) {
                    return {
                        healthy: false,
                        reason: `Memory usage (${memoryUsage}MB) exceeds threshold (${this.maxMemoryMb}MB)`,
                    };
                }
            }

            return { healthy: true };
        } catch (error) {
            return { healthy: false, reason: `Process check failed: ${error.message}` };
        }
    }

    async shouldRestart(pid, maxRestarts = 2, currentRestarts = 0) {
        if (currentRestarts >= maxRestarts) return false;

        const health = await this.checkHealth(pid);
        return !health.healthy;
    }
}
