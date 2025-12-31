import test from 'ava';
import { Utils } from '../lib/Utils.js';

test('phone-num-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'phone-num-mod', 'bluetoothphone');
});
