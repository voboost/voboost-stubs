import test from 'ava';
import { Utils } from '../lib/Utils.js';

test('app-multi-display-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'app-multi-display-mod', 'systemservice');
});

test('voboost-to-menu-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'voboost-to-menu-mod', 'systemservice');
});

test('forced-ev-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'forced-ev-mod', 'systemservice');
});

test('systemservice: multi-agent injection', async (t) => {
    const agents = ['app-multi-display-mod', 'voboost-to-menu-mod', 'forced-ev-mod'];
    await Utils.runMultipleAgentsTest(t, 'systemservice', agents);
});
