import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

// ============================================
// phone-num-mod tests
// ============================================

test('phone-num-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'phone-num-mod', 'bluetoothphone');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);

    // Detailed checks for debugging
    if (!result.validation.valid) {
        t.log('Required matched:', result.validation.requiredMatched);
        t.log('Required missing:', result.validation.requiredMissing);
        t.log('Errors found:', result.validation.forbiddenFound);
        t.log('Output preview:', result.injection.output.substring(0, 500));
    }
});

test('phone-num-mod agent handles custom parameters', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'phone-num-mod', 'bluetoothphone', {
        params: {
            countryCode: 'US',
            format: 'international',
            enableValidation: true,
        },
    });

    t.true(result.validation.valid, result.validation.reason);
});

test('phone-num-mod agent lifecycle management', async (t) => {
    await TestUtils.runMemoryTest(t, 'phone-num-mod', 'bluetoothphone', {
        cycles: 2,
    });
    t.pass('Phone agent lifecycle management completed successfully');
});

test('phone-num-mod agent handles concurrent injections', async (t) => {
    const result = await TestUtils.runStressTest(t, 'phone-num-mod', 'bluetoothphone', {
        concurrentCount: 2,
    });
    t.true(
        result.injections.every((i) => i.success),
        'Concurrent injections should succeed'
    );
});

test('phone-num-mod agent error handling', async (t) => {
    await TestUtils.runErrorHandlingTest(t, 'phone-num-mod', 'bluetoothphone');
});

test('phone-num-mod agent handles missing Util class gracefully', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'phone-num-mod', 'bluetoothphone');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);

    // Detailed checks for debugging
    if (!result.validation.valid) {
        t.log('Required matched:', result.validation.requiredMatched);
        t.log('Required missing:', result.validation.requiredMissing);
        t.log('Errors found:', result.validation.forbiddenFound);
        t.log('Output preview:', result.injection.output.substring(0, 500));
    }
});
