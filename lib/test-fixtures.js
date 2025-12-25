/**
 * TestFixtures - Shared test configurations and data
 *
 * This module provides centralized test configurations to reduce duplication
 * and ensure consistency across test files.
 */

// Import all agent log constants from voboost-script using new named exports
import {
    INFO as FORCED_EV_INFO,
    DEBUG as FORCED_EV_DEBUG,
    ERROR as FORCED_EV_ERROR,
} from '../../voboost-script/agents/forced-ev-log.js';

import {
    INFO as KEYBOARD_LOCK_EN_INFO,
    DEBUG as KEYBOARD_LOCK_EN_DEBUG,
    ERROR as KEYBOARD_LOCK_EN_ERROR,
} from '../../voboost-script/agents/keyboard-lock-en-log.js';

import {
    INFO as KEYBOARD_RU_INFO,
    DEBUG as KEYBOARD_RU_DEBUG,
    ERROR as KEYBOARD_RU_ERROR,
} from '../../voboost-script/agents/keyboard-ru-log.js';

import {
    INFO as ADAS_ACTIVATION_INFO,
    DEBUG as ADAS_ACTIVATION_DEBUG,
    ERROR as ADAS_ACTIVATION_ERROR,
} from '../../voboost-script/agents/ADAS-activation-log.js';

import {
    INFO as APP_MULTI_DISPLAY_INFO,
    DEBUG as APP_MULTI_DISPLAY_DEBUG,
    ERROR as APP_MULTI_DISPLAY_ERROR,
} from '../../voboost-script/agents/app-multi-display-log.js';

import {
    INFO as VOBOOST_TO_MENU_INFO,
    DEBUG as VOBOOST_TO_MENU_DEBUG,
    ERROR as VOBOOST_TO_MENU_ERROR,
} from '../../voboost-script/agents/voboost-to-menu-log.js';

import {
    INFO as WEATHER_WIDGET_INFO,
    DEBUG as WEATHER_WIDGET_DEBUG,
    ERROR as WEATHER_WIDGET_ERROR,
} from '../../voboost-script/agents/weather-widget-log.js';

import {
    INFO as APP_LAUNCHER_INFO,
    DEBUG as APP_LAUNCHER_DEBUG,
    ERROR as APP_LAUNCHER_ERROR,
} from '../../voboost-script/agents/app-launcher-log.js';

import {
    INFO as NAVBAR_LAUNCHER_INFO,
    DEBUG as NAVBAR_LAUNCHER_DEBUG,
    ERROR as NAVBAR_LAUNCHER_ERROR,
} from '../../voboost-script/agents/navbar-launcher-log.js';

import {
    INFO as APP_VIEWPORT_INFO,
    DEBUG as APP_VIEWPORT_DEBUG,
    ERROR as APP_VIEWPORT_ERROR,
} from '../../voboost-script/agents/app-viewport-log.js';

import {
    INFO as PHONE_NUM_INFO,
    DEBUG as PHONE_NUM_DEBUG,
    ERROR as PHONE_NUM_ERROR,
} from '../../voboost-script/agents/phone-num-log.js';

import {
    INFO as LOW_SPEED_SOUND_INFO,
    DEBUG as LOW_SPEED_SOUND_DEBUG,
    ERROR as LOW_SPEED_SOUND_ERROR,
} from '../../voboost-script/agents/low-speed-sound-log.js';

import {
    INFO as MEDIA_SOURCE_INFO,
    DEBUG as MEDIA_SOURCE_DEBUG,
    ERROR as MEDIA_SOURCE_ERROR,
} from '../../voboost-script/agents/media-source-log.js';

import {
    INFO as MEDIA_WINDOW_INFO,
    DEBUG as MEDIA_WINDOW_DEBUG,
    ERROR as MEDIA_WINDOW_ERROR,
} from '../../voboost-script/agents/media-window-log.js';

/**
 * Creates log sequence from INFO/DEBUG/ERROR exports
 * @param {Object} INFO - Info level log messages
 * @param {Object} DEBUG - Debug level log messages
 * @param {Object} ERROR - Error level log messages
 * @returns {Object} Log sequence configuration
 */
function createLogSequence(INFO, DEBUG, ERROR, allowErrors = []) {
    const forbiddenErrors = Object.values(ERROR || {}).filter(
        (error) => !allowErrors.some((allowedError) => error.includes(allowedError))
    );

    return {
        required: [INFO.STARTING, INFO.STARTED],
        operational: [
            ...Object.values(INFO).filter((msg) => msg !== INFO.STARTING && msg !== INFO.STARTED),
            ...Object.values(DEBUG || {}),
        ],
        forbidden: forbiddenErrors,
    };
}

/**
 * Agent log sequences for validation
 * Maps agent names to expected message sequences with order checking
 * @type {Object.<string, {required: string[], operational: string[], forbidden: string[]}>}
 */
export const AGENT_LOG_SEQUENCES = {
    'weather-widget-mod': createLogSequence(
        WEATHER_WIDGET_INFO,
        WEATHER_WIDGET_DEBUG,
        WEATHER_WIDGET_ERROR
    ),

    'phone-num-mod': createLogSequence(PHONE_NUM_INFO, PHONE_NUM_DEBUG, PHONE_NUM_ERROR),

    'forced-ev-mod': createLogSequence(FORCED_EV_INFO, FORCED_EV_DEBUG, FORCED_EV_ERROR),

    'app-launcher-mod': createLogSequence(
        APP_LAUNCHER_INFO,
        APP_LAUNCHER_DEBUG,
        APP_LAUNCHER_ERROR
    ),

    'navbar-launcher-mod': createLogSequence(
        NAVBAR_LAUNCHER_INFO,
        NAVBAR_LAUNCHER_DEBUG,
        NAVBAR_LAUNCHER_ERROR
    ),

    'app-multi-display': createLogSequence(
        APP_MULTI_DISPLAY_INFO,
        APP_MULTI_DISPLAY_DEBUG,
        APP_MULTI_DISPLAY_ERROR
    ),

    'keyboard-ru-mod': createLogSequence(KEYBOARD_RU_INFO, KEYBOARD_RU_DEBUG, KEYBOARD_RU_ERROR),

    'keyboard-lock-en-mod': createLogSequence(
        KEYBOARD_LOCK_EN_INFO,
        KEYBOARD_LOCK_EN_DEBUG,
        KEYBOARD_LOCK_EN_ERROR
    ),

    'ADAS-activation-mod': createLogSequence(
        ADAS_ACTIVATION_INFO,
        ADAS_ACTIVATION_DEBUG,
        ADAS_ACTIVATION_ERROR
    ),

    'low-speed-sound-mod': createLogSequence(
        LOW_SPEED_SOUND_INFO,
        LOW_SPEED_SOUND_DEBUG,
        LOW_SPEED_SOUND_ERROR
    ),

    'voboost-to-menu-mod': createLogSequence(
        VOBOOST_TO_MENU_INFO,
        VOBOOST_TO_MENU_DEBUG,
        VOBOOST_TO_MENU_ERROR
    ),

    'app-viewport-mod': createLogSequence(
        APP_VIEWPORT_INFO,
        APP_VIEWPORT_DEBUG,
        APP_VIEWPORT_ERROR
    ),

    'media-source-mod': createLogSequence(
        MEDIA_SOURCE_INFO,
        MEDIA_SOURCE_DEBUG,
        MEDIA_SOURCE_ERROR
    ),

    'media-window-mod': createLogSequence(
        MEDIA_WINDOW_INFO,
        MEDIA_WINDOW_DEBUG,
        MEDIA_WINDOW_ERROR
    ),
};

/**
 * Process to agent mappings
 * Defines which agents should be tested with which processes
 * @type {Object.<string, string[]>}
 */
export const PROCESS_AGENT_MAP = {
    launcher: [
        'weather-widget-mod',
        'app-launcher-mod',
        'navbar-launcher-mod',
        'app-viewport-mod',
        'media-source-mod',
        'media-window-mod',
    ],
    bluetoothphone: ['phone-num-mod'],
    systemservice: ['app-multi-display', 'voboost-to-menu-mod', 'forced-ev-mod'],
    qgime: ['keyboard-ru-mod', 'keyboard-lock-en-mod'],
    vehiclesetting: ['ADAS-activation-mod', 'low-speed-sound-mod'],
};

/**
 * Default test timeouts for different scenarios (in milliseconds)
 * @type {Object.<string, number>}
 */
export const TEST_TIMEOUTS = {
    STANDARD: 8000, // Standard test timeout (8s) - reduced from 10s
    EXTENDED: 15000, // Extended timeout for complex tests (15s) - reduced from 20s
    QUICK: 3000, // Quick tests (3s) - reduced from 5s
    STRESS: 20000, // Stress tests (20s) - reduced from 30s
};

/**
 * Expected output patterns for stub initialization
 * Maps process names to arrays of expected log messages
 * @type {Object.<string, string[]>}
 */
export const STUB_INIT_PATTERNS = {
    launcher: [
        '[com.qinggan.app.launcher] Starting Java stub application',
        '[com.qinggan.app.launcher] Ready for Frida injection',
    ],
    bluetoothphone: [
        '[com.qinggan.bluetoothphone] Starting Java stub application',
        '[com.qinggan.bluetoothphone] Ready for Frida injection',
    ],
    systemservice: [
        '[com.qinggan.systemservice] Starting Java stub application',
        '[com.qinggan.systemservice] Ready for Frida injection',
    ],
    qgime: [
        '[com.qinggan.app.qgime] Starting Java stub application',
        '[com.qinggan.app.qgime] Ready for Frida injection',
    ],
    vehiclesetting: [
        '[com.qinggan.app.vehiclesetting] Starting Java stub application',
        '[com.qinggan.app.vehiclesetting] Ready for Frida injection',
    ],
};

/**
 * Test configurations for specific agents
 * Maps agent names to configuration objects with parameters and timeouts
 * @type {Object.<string, {params: Object, timeout: number}>}
 */
export const AGENT_TEST_CONFIGS = {
    'weather-widget-mod': {
        params: {
            config: {
                api_key: 'test-api-key-12345',
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'app-launcher-mod': {
        params: {
            config: {
                apps: [
                    {
                        package: 'com.example.app1',
                        name: ['Test App 1', 'Test App 1 RU'],
                        icon_big: 'base64-icon-data',
                        icon_small: 'base64-icon-data',
                    },
                ],
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'navbar-launcher-mod': {
        params: {
            config: {
                apps: [
                    {
                        package: 'com.example.app1',
                        navigation_bar: true,
                    },
                ],
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'app-viewport-mod': {
        params: {
            config: {
                apps: [
                    {
                        package: 'com.example.app1',
                        screen: ['main'],
                        padding: ['left'],
                        scale: 1.0,
                        dpi: 320,
                    },
                ],
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'phone-num-mod': {
        params: {
            countryCode: 'RU',
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'app-multi-display': {
        params: {
            config: {
                apps: [
                    {
                        package: 'com.example.app1',
                        screen: ['main', 'second'],
                    },
                ],
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'voboost-to-menu-mod': {
        params: {
            config: {
                language: 'EN',
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'forced-ev-mod': {
        params: {},
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'keyboard-ru-mod': {
        params: {
            config: {
                keyboard: {
                    attrs: {
                        balloon: true,
                        height: 0.243,
                        key_type: 0,
                        key_xmargin: 0.0031,
                        key_ymargin: 0.0172,
                        qwerty: true,
                        qwerty_uppercase: true,
                        repeat: false,
                        skb_cache_flag: true,
                        skb_sticky_flag: true,
                        skb_template: '@xml/skb_template1_no_voice',
                        width: 0.0823,
                    },
                    rows: [
                        {
                            start_pos_x: 0.0094,
                            start_pos_y: 0.0172,
                            keys: [
                                { label: 'Q', code: 45 },
                                { label: 'W', code: 51 },
                                { label: 'E', code: 33 },
                                { label: 'R', code: 46 },
                                { label: 'T', code: 48 },
                                { label: 'Y', code: 53 },
                                { label: 'U', code: 49 },
                                { label: 'I', code: 37 },
                                { label: 'O', code: 43 },
                                { label: 'P', code: 44 },
                                { label: '[', code: 10001 },
                                { label: ']', code: 10002 },
                            ],
                        },
                    ],
                },
                drawable: {
                    english_input_method: '',
                    english_input_method_white: '',
                    russian_input_method: '',
                    russian_input_method_white: '',
                },
                layout: 'qwerty',
                enable_voice: false,
                custom_keys: [],
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'keyboard-lock-en-mod': {
        params: {
            config: {
                drawable: {
                    english_input_method: '',
                    english_input_method_white: '',
                    russian_input_method: '',
                    russian_input_method_white: '',
                },
                enabled: true,
                auto_lock_timeout: 30000,
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'ADAS-activation-mod': {
        params: {},
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'low-speed-sound-mod': {
        params: {},
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'media-source-mod': {
        params: {
            config: {
                media: {
                    WECAR_FLOW: {
                        pageName: 'com.example.media1',
                        name: { EN: 'Test Media 1', RU: 'Test Media 1 RU' },
                        icon: 'base64-icon-data',
                        autoPlay: true,
                    },
                },
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
    'media-window-mod': {
        params: {
            config: {
                media: {
                    WECAR_FLOW: {
                        pageName: 'com.example.media1',
                        name: { EN: 'Test Media 1', RU: 'Test Media 1 RU' },
                        iconLarge: 'base64-icon-data',
                    },
                },
            },
        },
        timeout: TEST_TIMEOUTS.STANDARD,
    },
};

/**
 * Gets all agents for a specific process
 * @param {string} processName - Name of the process
 * @returns {Array<string>} Array of agent names
 */
export function getAgentsForProcess(processName) {
    return PROCESS_AGENT_MAP[processName] || [];
}

/**
 * Gets the process name for a specific agent
 * @param {string} agentName - Name of the agent
 * @returns {string|null} Process name or null if not found
 */
export function getProcessForAgent(agentName) {
    for (const [process, agents] of Object.entries(PROCESS_AGENT_MAP)) {
        if (agents.includes(agentName)) {
            return process;
        }
    }
    return null;
}

/**
 * Gets test configuration for a specific agent
 * @param {string} agentName - Name of the agent
 * @returns {Object} Test configuration
 */
export function getAgentConfig(agentName) {
    return (
        AGENT_TEST_CONFIGS[agentName] || {
            params: {},
            timeout: TEST_TIMEOUTS.STANDARD,
        }
    );
}

/**
 * Gets all process names
 * @returns {Array<string>} Array of process names
 */
export function getAllProcesses() {
    return Object.keys(PROCESS_AGENT_MAP);
}

/**
 * Gets all agent names
 * @returns {Array<string>} Array of agent names
 */
export function getAllAgents() {
    return Object.values(PROCESS_AGENT_MAP).flat();
}

/**
 * Creates a cross-process test map for comprehensive testing
 * @returns {Array<Object>} Array of {agent, process, params} objects
 */
export function createCrossProcessTestMap() {
    const testMap = [];

    for (const [process, agents] of Object.entries(PROCESS_AGENT_MAP)) {
        for (const agent of agents) {
            const config = getAgentConfig(agent);
            testMap.push({
                agent,
                process,
                params: config.params,
            });
        }
    }

    return testMap;
}

/**
 * Success log patterns for each agent
 * These are the patterns that indicate successful agent initialization
 * @type {Object.<string, string>}
 */
export const AGENT_SUCCESS_PATTERNS = {
    'weather-widget-mod': WEATHER_WIDGET_INFO.STARTED,
    'app-launcher-mod': APP_LAUNCHER_INFO.STARTED,
    'navbar-launcher-mod': NAVBAR_LAUNCHER_INFO.STARTED,
    'app-viewport-mod': APP_VIEWPORT_INFO.STARTED,
    'phone-num-mod': PHONE_NUM_INFO.STARTED,
    'app-multi-display': APP_MULTI_DISPLAY_INFO.STARTED,
    'voboost-to-menu-mod': VOBOOST_TO_MENU_INFO.STARTED,
    'forced-ev-mod': FORCED_EV_INFO.STARTED,
    'keyboard-ru-mod': KEYBOARD_RU_INFO.STARTED,
    'keyboard-lock-en-mod': KEYBOARD_LOCK_EN_INFO.STARTED,
    'ADAS-activation-mod': ADAS_ACTIVATION_INFO.STARTED,
    'low-speed-sound-mod': LOW_SPEED_SOUND_INFO.STARTED,
    'media-source-mod': MEDIA_SOURCE_INFO.STARTED,
    'media-window-mod': MEDIA_WINDOW_INFO.STARTED,
};

/**
 * Gets the success pattern for an agent
 * @param {string} agentName - Name of the agent
 * @returns {string|null} Success pattern or null if not found
 */
export function getAgentSuccessPattern(agentName) {
    return AGENT_SUCCESS_PATTERNS[agentName] || null;
}

export default {
    PROCESS_AGENT_MAP,
    TEST_TIMEOUTS,
    STUB_INIT_PATTERNS,
    AGENT_TEST_CONFIGS,
    AGENT_SUCCESS_PATTERNS,
    getAgentsForProcess,
    getProcessForAgent,
    getAgentConfig,
    getAllProcesses,
    getAllAgents,
    createCrossProcessTestMap,
    getAgentSuccessPattern,
};
