export default {
    files: ['test/**/*.js'],

    // Lower concurrency to avoid race conditions in verbose mode
    // Each test runs in its own process with PID isolation
    concurrency: 100,

    // Timeout configuration for parallel tests
    timeout: '120s',

    // Continue running all tests even if some fail
    // This ensures we get complete results from all test processes
    failFast: false,

    // Enhanced reporting with verbose output for better debugging
    // Shows individual test process results clearly
    verbose: true,

    // Disable TAP format for cleaner console output
    // Makes it easier to see which parallel tests passed/failed
    tap: false,

    // Each test runs in its own worker thread/process
    workerThreads: true,
};
