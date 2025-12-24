/**
 * Consolidated AVA Configuration
 * Merges parallel and serial configurations with optimized settings
 * Supports both parallel and serial execution modes via environment variables
 */

import { ProcessManager } from '../lib/test-process-manager.js';
import { FridaTestHelper } from '../lib/test-frida-helper.js';

// Determine execution mode from environment
const isParallel = process.env.AVA_PARALLEL !== 'false';
const concurrency = isParallel ? 100 : 1;

export default {
    // Test files pattern - include all test files
    files: ['test/**/*.js'],

    // Dynamic concurrency based on execution mode
    concurrency: concurrency,

    // Optimized timeout configuration
    timeout: isParallel ? '60s' : '5m',

    // Continue running tests even if some fail
    failFast: false,

    // Enhanced reporting
    verbose: true,

    // Disable TAP format for cleaner output
    tap: false,

    // Serial/parallel mode based on configuration
    serial: !isParallel,

    // Worker threads for parallel execution
    workerThreads: isParallel,

    // Environment variables for test execution
    environmentVariables: {
        NODE_ENV: 'test',
        AVA_CONCURRENCY: concurrency.toString(),
        AVA_PARALLEL: isParallel.toString(),
        // Worker isolation variables
        VOBOOST_PARALLEL_MODE: isParallel.toString(),
        VOBOOST_WORKER_ISOLATION: isParallel.toString(),
        VOBOOST_SERIAL_MODE: (!isParallel).toString(),
        // Phase 3 optimization variables
        VOBOOST_PHASE_3: 'true',
        VOBOOST_PID_INJECTION: 'true',
    },

    // Hooks for proper cleanup
    hooks: {
        // Final cleanup when all tests are done
        afterAll: async () => {
            try {
                await ProcessManager.cleanupAll();
                await FridaTestHelper.cleanupAllProcesses();

                // Additional aggressive cleanup
                const { exec } = await import('child_process');
                const { promisify } = await import('util');
                const execAsync = promisify(exec);

                // Kill any remaining processes (only our stubs, not all Java processes)
                await execAsync(
                    'pkill -f "java.*LauncherStub\\|java.*BluetoothPhoneStub\\|java.*SystemServiceStub\\|java.*QgimeStub\\|java.*VehicleSettingStub\\|frida" || true'
                );
                await new Promise((resolve) => setTimeout(resolve, 1000));
                process.exit(0);
            } catch (error) {
                process.exit(1);
            }
        },
    },
};
