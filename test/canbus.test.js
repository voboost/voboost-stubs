import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

/**
 * Tests for agents that use CanBusManager
 * These agents inject into different processes but hook the same classes
 */

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

// ============================================
// forced-ev-mod (systemservice process)
// ============================================

test('forced-ev-mod uses CanBusManager.getInstance correctly', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'forced-ev-mod', 'systemservice');

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

test('forced-ev-mod sets IVI_SOC_MODESET state', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'forced-ev-mod', 'systemservice');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// low-speed-sound-mod (vehiclesetting process)
// ============================================

test('low-speed-sound-mod uses CanBusManager.getInstance correctly', async (t) => {
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

test('low-speed-sound-mod sets HUM_VSP_FUNCTION_SW state', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'low-speed-sound-mod', 'vehiclesetting');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// Cross-process CanBusManager tests
// ============================================

test('CanBusManager works in both systemservice and vehiclesetting', async (t) => {
    // Test forced-ev-mod in systemservice
    const forcedEvResult = await TestUtils.runStandardAgentTest(
        t,
        'forced-ev-mod',
        'systemservice'
    );

    // Test low-speed-sound-mod in vehiclesetting
    const lowSpeedResult = await TestUtils.runStandardAgentTest(
        t,
        'low-speed-sound-mod',
        'vehiclesetting'
    );

    t.true(forcedEvResult.success, 'forced-ev-mod should inject successfully');
    t.true(lowSpeedResult.success, 'low-speed-sound-mod should inject successfully');

    // Both should have valid validation
    t.true(forcedEvResult.validation.valid, forcedEvResult.validation.reason);
    t.true(lowSpeedResult.validation.valid, lowSpeedResult.validation.reason);
});
