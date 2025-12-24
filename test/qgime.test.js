import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

// ============================================
// keyboard-ru-mod tests
// ============================================

test('keyboard-ru-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'keyboard-ru-mod', 'qgime');

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

test('keyboard-ru-mod agent handles Russian keyboard layout', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'keyboard-ru-mod', 'qgime', {
        params: {
            enableVoice: false,
            layout: 'ru',
        },
    });

    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// keyboard-lock-en-mod tests
// ============================================

test('keyboard-lock-en-mod agent injects and outputs success message', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'keyboard-lock-en-mod', 'qgime');

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

test('keyboard-lock-en-mod agent disables voice input', async (t) => {
    const result = await TestUtils.runStandardAgentTest(t, 'keyboard-lock-en-mod', 'qgime', {
        params: {
            enableVoice: false,
            lockLayout: 'en',
        },
    });

    t.true(result.validation.valid, result.validation.reason);
});

// ============================================
// Multiple agents and stress tests
// ============================================

test('multiple qgime agents can inject simultaneously', async (t) => {
    const agents = ['keyboard-ru-mod', 'keyboard-lock-en-mod'];
    const result = await TestUtils.runMultipleAgentsTest(t, 'qgime', agents);
    t.true(
        result.injections.every((i) => i.success),
        'All agents should inject successfully'
    );
});

test('keyboard agent lifecycle management', async (t) => {
    await TestUtils.runMemoryTest(t, 'keyboard-lock-en-mod', 'qgime', {
        cycles: 2,
    });
    t.pass('Keyboard agent lifecycle management completed successfully');
});

test('keyboard agents handle concurrent injections', async (t) => {
    const result = await TestUtils.runStressTest(t, 'keyboard-ru-mod', 'qgime', {
        concurrentCount: 2,
    });
    t.true(
        result.injections.every((i) => i.success),
        'Concurrent injections should succeed'
    );
});

test('keyboard agents error handling', async (t) => {
    await TestUtils.runErrorHandlingTest(t, 'keyboard-ru-mod', 'qgime');
});
