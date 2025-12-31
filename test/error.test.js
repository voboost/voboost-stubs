import test from 'ava';
import { Frida } from '../lib/Frida.js';

/**
 * Shared error handling tests for all stubs.
 * These tests verify proper error handling for invalid scenarios.
 */

test('error: invalid process name throws error', async (t) => {
    await t.throwsAsync(
        async () => {
            await Frida.startStub('nonexistent-process');
        },
        {
            message: /Unknown app name|ENOENT|no such file/,
        }
    );
});

test('error: invalid agent script throws error', async (t) => {
    const stub = await Frida.startStub('launcher');
    await new Promise((resolve) => setTimeout(resolve, 500));

    try {
        await t.throwsAsync(
            async () => {
                await Frida.inject({
                    pid: stub.pid,
                    script: '/nonexistent/script.js',
                });
            },
            {
                message: /Unknown agent|ENOENT|Agent script file not found/,
            }
        );
    } finally {
        await Frida.cleanup(stub);
    }
});

/**
 * Process isolation verification test.
 * Verifies that each test gets its own isolated process with unique PID.
 */
test('isolation: each stub gets unique PID', async (t) => {
    const stub1 = await Frida.startStub('launcher');
    const stub2 = await Frida.startStub('launcher');

    try {
        t.truthy(stub1.pid, 'First stub should have a PID');
        t.truthy(stub2.pid, 'Second stub should have a PID');
        t.not(stub1.pid, stub2.pid, 'Each stub should have a unique PID');
        t.true(stub1.pid > 0, 'First PID should be positive');
        t.true(stub2.pid > 0, 'Second PID should be positive');
    } finally {
        await Frida.cleanup(stub1, stub2);
    }
});

/**
 * Validation pattern tests.
 * These tests verify that the validation logic works correctly.
 */

test('validation: detects missing required patterns', async (t) => {
    // Output missing the 'started' message
    const incompleteOutput = 'Agent starting';
    const result = await Frida.validateInjection(incompleteOutput, 'weather-widget-mod');

    t.false(result.valid, 'Missing required patterns should fail validation');
    t.true(result.requiredMissing.length > 0, 'Should report missing patterns');
});

test('validation: passes with all required patterns', async (t) => {
    // Output with both required messages in correct order
    const completeOutput = 'Agent starting\nSome other log\nAgent started';
    const result = await Frida.validateInjection(completeOutput, 'weather-widget-mod');

    t.true(result.valid, 'All required patterns present should pass validation');
    t.is(result.requiredMissing.length, 0, 'Should have no missing patterns');
});

/**
 * Error classification tests.
 * These tests verify that errors are properly classified based on their content.
 */

test('error: _classifyError method correctly classifies errors', (t) => {
    // Test Java bridge errors
    t.is(Frida._classifyError('thread_from_jni_environment failed'), Frida.ERROR_TYPES.JAVA_BRIDGE);
    t.is(Frida._classifyError('Java bridge initialization error'), Frida.ERROR_TYPES.JAVA_BRIDGE);

    // Test architecture errors
    t.is(Frida._classifyError('architecture mismatch detected'), Frida.ERROR_TYPES.ARCHITECTURE);
    t.is(Frida._classifyError('unable to make executable'), Frida.ERROR_TYPES.ARCHITECTURE);

    // Test process termination errors
    t.is(
        Frida._classifyError('process terminated unexpectedly'),
        Frida.ERROR_TYPES.PROCESS_TERMINATED
    );
    t.is(Frida._classifyError('process exited with code 1'), Frida.ERROR_TYPES.PROCESS_TERMINATED);

    // Test missing export errors
    t.is(
        Frida._classifyError('unable to find export epoll_wait'),
        Frida.ERROR_TYPES.MISSING_EXPORT
    );
    t.is(Frida._classifyError('epoll_wait not found'), Frida.ERROR_TYPES.MISSING_EXPORT);

    // Test null pointer errors
    t.is(Frida._classifyError('NullPointerException in Java stub'), Frida.ERROR_TYPES.NULL_POINTER);
    t.is(Frida._classifyError('getmodule() returned null'), Frida.ERROR_TYPES.NULL_POINTER);

    // Test timeout errors
    t.is(Frida._classifyError('operation timeout after 5000ms'), Frida.ERROR_TYPES.TIMEOUT);
    t.is(Frida._classifyError('connection timed out'), Frida.ERROR_TYPES.TIMEOUT);

    // Test unknown errors
    t.is(Frida._classifyError('some random error message'), Frida.ERROR_TYPES.UNKNOWN);
    t.is(Frida._classifyError(''), Frida.ERROR_TYPES.UNKNOWN);
    t.is(Frida._classifyError(null), Frida.ERROR_TYPES.UNKNOWN);
});

test('error: _isRecoverableError method correctly identifies recoverable errors', (t) => {
    // Test recoverable errors
    t.true(Frida._isRecoverableError(Frida.ERROR_TYPES.JAVA_BRIDGE));
    t.true(Frida._isRecoverableError(Frida.ERROR_TYPES.PROCESS_TERMINATED));
    t.true(Frida._isRecoverableError(Frida.ERROR_TYPES.NULL_POINTER));
    t.true(Frida._isRecoverableError(Frida.ERROR_TYPES.MISSING_EXPORT));

    // Test non-recoverable errors
    t.false(Frida._isRecoverableError(Frida.ERROR_TYPES.ARCHITECTURE));
    t.false(Frida._isRecoverableError(Frida.ERROR_TYPES.TIMEOUT));
    t.false(Frida._isRecoverableError(Frida.ERROR_TYPES.UNKNOWN));
});

test('error: _handleRecoverableError method returns appropriate fallback behavior', (t) => {
    // Test Java bridge error
    const javaBridgeFallback = Frida._handleRecoverableError(Frida.ERROR_TYPES.JAVA_BRIDGE, {});
    t.true(javaBridgeFallback.shouldRetry);
    t.is(javaBridgeFallback.retryDelay, 2000);
    t.truthy(javaBridgeFallback.message);

    // Test process terminated error
    const processTerminatedFallback = Frida._handleRecoverableError(
        Frida.ERROR_TYPES.PROCESS_TERMINATED,
        {}
    );
    t.true(processTerminatedFallback.shouldRetry);
    t.is(processTerminatedFallback.retryDelay, 1000);
    t.truthy(processTerminatedFallback.message);

    // Test null pointer error
    const nullPointerFallback = Frida._handleRecoverableError(Frida.ERROR_TYPES.NULL_POINTER, {});
    t.false(nullPointerFallback.shouldRetry);
    t.truthy(nullPointerFallback.message);

    // Test missing export error
    const missingExportFallback = Frida._handleRecoverableError(
        Frida.ERROR_TYPES.MISSING_EXPORT,
        {}
    );
    t.false(missingExportFallback.shouldRetry);
    t.truthy(missingExportFallback.message);

    // Test unknown error
    const unknownFallback = Frida._handleRecoverableError(Frida.ERROR_TYPES.UNKNOWN, {});
    t.false(unknownFallback.shouldRetry);
    t.truthy(unknownFallback.message);
});
