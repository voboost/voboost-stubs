/**
 * Debug - Provides debugging utilities for test processes
 */
export class Debug {
    static async dumpProcessState(pid) {
        if (!process.env.DEBUG) return null;

        try {
            const { exec } = await import('child_process');
            const { promisify } = await import('util');
            const execAsync = promisify(exec);

            const { stdout: psOutput } = await execAsync(
                `ps -p ${pid} -o pid,ppid,pcpu,pmem,rss,vsz,command`
            );
            const { stdout: lsofOutput } = await execAsync(`lsof -p ${pid} 2>/dev/null | head -20`);

            return {
                processInfo: psOutput.trim(),
                openFiles: lsofOutput.trim(),
                timestamp: new Date().toISOString(),
            };
        } catch (error) {
            return { error: error.message };
        }
    }

    static logDebugInfo(message, data = {}) {
        if (process.env.DEBUG) {
            console.log(`[DEBUG] ${message}`, data);
        }
    }

    static async generateDebugReport(testName, pid, agentName, error = null) {
        if (!process.env.DEBUG) return null;

        const report = {
            testName,
            pid,
            agentName,
            timestamp: new Date().toISOString(),
            error: error
                ? {
                      message: error.message,
                      stack: error.stack,
                      type: error.constructor.name,
                  }
                : null,
            processState: await this.dumpProcessState(pid),
        };

        this.logDebugInfo('Debug report generated', report);
        return report;
    }
}
