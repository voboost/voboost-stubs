import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

// ============================================
// weather-widget-mod tests
// ============================================

test('weather-widget-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'weather-widget-mod', 'launcher');

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

test('weather-widget-mod agent handles weather request interception', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'weather-widget-mod', 'launcher');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// app-launcher-mod tests
// ============================================

test('app-launcher-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'app-launcher-mod', 'launcher');

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

// ============================================
// navbar-launcher-mod tests
// ============================================

test('navbar-launcher-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'navbar-launcher-mod', 'launcher');

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

// ============================================
// app-viewport-mod tests
// ============================================

test('app-viewport-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'app-viewport-mod', 'launcher');

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

// ============================================
// media-source-mod tests
// ============================================

test('media-source-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'media-source-mod', 'launcher');

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

// ============================================
// media-window-mod tests
// ============================================

test('media-window-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'media-window-mod', 'launcher');

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

// ============================================
// Stress and lifecycle tests
// ============================================

test('launcher agent lifecycle management', async (t) => {
    await TestUtils.runMemoryTest(t, 'weather-widget-mod', 'launcher', {
        cycles: 2,
    });
    t.pass('Launcher agent lifecycle management completed successfully');
});

test('launcher agents handle concurrent injections', async (t) => {
    const result = await TestUtils.runStressTest(t, 'app-launcher-mod', 'launcher', {
        concurrentCount: 2,
    });
    t.true(
        result.injections.every((i) => i.success),
        'Concurrent injections should succeed'
    );
});

test('multiple launcher agents can inject simultaneously', async (t) => {
    const agents = ['weather-widget-mod', 'app-launcher-mod', 'navbar-launcher-mod'];
    const result = await TestUtils.runMultipleAgentsTest(t, 'launcher', agents);
    t.true(
        result.injections.every((i) => i.success),
        'All agents should inject successfully'
    );
});
