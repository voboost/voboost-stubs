import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

// ============================================
// app-multi-display tests
// ============================================

test('app-multi-display agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'app-multi-display', 'systemservice');

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

test('app-multi-display agent handles whitelist functionality', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'app-multi-display', 'systemservice', {
        params: {
            enableMultiDisplay: true,
            apps: [
                { package: 'com.example.transferable', screen: ['main', 'third'] },
                { package: 'com.example.locked', screen: ['main'] },
            ],
        },
    });

    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// voboost-to-menu-mod tests
// ============================================

test('voboost-to-menu-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'voboost-to-menu-mod', 'systemservice');

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
// forced-ev-mod tests
// ============================================

test('forced-ev-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'forced-ev-mod', 'systemservice');

    // Log output only when DEBUG=1
    if (process.env.DEBUG) {
        t.log('Full injection output:');
        t.log(result.injection.output);
        t.log('--- End of output ---');
    }

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

test('forced-ev-mod agent activates EV mode via CanBusManager', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'forced-ev-mod', 'systemservice');

    // Primary assertion - validation result
    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// Multiple agents and stress tests
// ============================================

test('multiple systemservice agents can inject simultaneously', async (t) => {
    const agents = ['app-multi-display', 'voboost-to-menu-mod', 'forced-ev-mod'];
    const result = await TestUtils.runMultipleAgentsTest(t, 'systemservice', agents);
    t.true(
        result.injections.every((i) => i.success),
        'All agents should inject successfully'
    );
});

test('systemservice agent lifecycle management', async (t) => {
    await TestUtils.runMemoryTest(t, 'forced-ev-mod', 'systemservice', {
        cycles: 2,
    });
    t.pass('Systemservice agent lifecycle management completed successfully');
});

test('systemservice agents error handling', async (t) => {
    await TestUtils.runErrorHandlingTest(t, 'app-multi-display', 'systemservice');
});
