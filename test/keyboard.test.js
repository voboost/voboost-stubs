import test from 'ava';
import { Utils } from '../lib/Utils.js';

test('keyboard-ru-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'keyboard-ru-mod', 'keyboard');
});

test('keyboard-lock-en-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'keyboard-lock-en-mod', 'keyboard');
});

test('keyboard: multi-agent injection', async (t) => {
    const agents = ['keyboard-ru-mod', 'keyboard-lock-en-mod'];
    await Utils.runMultipleAgentsTest(t, 'keyboard', agents);
});
