import test from 'ava';
import { Frida } from '../lib/Frida.js';

test('injection: _hasValidOutput works correctly', (t) => {
    // Test the _hasValidOutput method
    const validOutput = 'Agent starting\nAgent started\nConfig loaded\n';
    const invalidOutput = 'Some other output\nNo agent messages here';

    t.true(Frida._hasValidOutput(validOutput));
    t.false(Frida._hasValidOutput(invalidOutput));
});
