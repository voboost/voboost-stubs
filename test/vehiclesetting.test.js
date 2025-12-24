import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

// ============================================
// ADAS-activation-mod tests
// ============================================

test('ADAS-activation-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'ADAS-activation-mod', 'vehiclesetting');

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

test('ADAS-activation-mod agent hooks BaiduProviderUtil', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'ADAS-activation-mod', 'vehiclesetting');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);
});

test('ADAS-activation-mod agent handles parameters', async (t) => {
    const result = await TestUtils.runStandardAgentTest(
        t,
        'ADAS-activation-mod',
        'vehiclesetting',
        {
            params: {
                subscriptionStatus: '1',
                remainDays: '30',
                expireStatus: '0',
            },
        }
    );

    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// low-speed-sound-mod tests
// ============================================

test('low-speed-sound-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'low-speed-sound-mod', 'vehiclesetting');

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

test('low-speed-sound-mod agent activates via CanBusManager', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'low-speed-sound-mod', 'vehiclesetting');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// Multiple agents and stress tests
// ============================================

test('multiple vehiclesetting agents can inject simultaneously', async (t) => {
    const agents = ['ADAS-activation-mod', 'low-speed-sound-mod'];
    const result = await TestUtils.runMultipleAgentsTest(t, 'vehiclesetting', agents);
    t.true(
        result.injections.every((i) => i.success),
        'All agents should inject successfully'
    );
});

test('ADAS-activation-mod agent lifecycle management', async (t) => {
    await TestUtils.runMemoryTest(t, 'ADAS-activation-mod', 'vehiclesetting', {
        cycles: 2,
    });
    t.pass('ADAS agent lifecycle management completed successfully');
});

test('ADAS-activation-mod agent error handling', async (t) => {
    await TestUtils.runErrorHandlingTest(t, 'ADAS-activation-mod', 'vehiclesetting');
});

test('low-speed-sound-mod agent error handling', async (t) => {
    await TestUtils.runErrorHandlingTest(t, 'low-speed-sound-mod', 'vehiclesetting');
});
