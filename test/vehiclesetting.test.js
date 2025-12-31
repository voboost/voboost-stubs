import test from 'ava';
import { Utils } from '../lib/Utils.js';

test('adas-activation-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'adas-activation-mod', 'vehiclesetting');
});

test('low-speed-sound-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'low-speed-sound-mod', 'vehiclesetting');
});

test('vehiclesetting: multi-agent injection', async (t) => {
    const agents = ['adas-activation-mod', 'low-speed-sound-mod'];
    await Utils.runMultipleAgentsTest(t, 'vehiclesetting', agents);
});
