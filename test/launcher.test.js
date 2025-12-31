import test from 'ava';
import { Utils } from '../lib/Utils.js';

test('weather-widget-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'weather-widget-mod', 'launcher');
});

test('app-launcher-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'app-launcher-mod', 'launcher');
});

test('navbar-launcher-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'navbar-launcher-mod', 'launcher');
});

test('app-viewport-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'app-viewport-mod', 'launcher');
});

test('media-source-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'media-source-mod', 'launcher');
});

test('media-window-mod: default injection', async (t) => {
    await Utils.runBasicInjectionTest(t, 'media-window-mod', 'launcher');
});

test('weather-widget-mod: injection with custom params', async (t) => {
    const result = await Utils.runStandardAgentTest(t, 'weather-widget-mod', 'launcher', {
        params: {
            config: {
                api_key: 'custom-test-key',
            },
        },
    });
    t.true(result.validation.valid, result.validation.reason);
});

test('launcher: multi-agent injection', async (t) => {
    const agents = [
        'weather-widget-mod',
        'app-launcher-mod',
        'navbar-launcher-mod',
        'app-viewport-mod',
        'media-source-mod',
        'media-window-mod',
    ];

    await Utils.runMultipleAgentsTest(t, 'launcher', agents);
});
