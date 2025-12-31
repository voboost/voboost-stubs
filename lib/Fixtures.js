/**
 * Fixtures - Shared test configurations and data
 *
 * This module provides centralized test configurations to reduce duplication
 * and ensure consistency across test files.
 *
 * @module Fixtures
 *
 * ## External Dependencies
 *
 * This module imports log constants from `voboost-script/agents/*-log.js` files.
 * These imports are required for test validation and must be available at:
 * `../../voboost-script/agents/`
 *
 * Required agent log files:
 * - forced-ev-log.js
 * - keyboard-lock-en-log.js
 * - keyboard-ru-log.js
 * - adas-activation-log.js
 * - app-multi-display-log.js
 * - voboost-to-menu-log.js
 * - weather-widget-log.js
 * - app-launcher-log.js
 * - navbar-launcher-log.js
 * - app-viewport-log.js
 * - phone-num-log.js
 * - low-speed-sound-log.js
 * - media-source-log.js
 * - media-window-log.js
 *
 * Each log file must export: INFO, DEBUG, ERROR objects with log message constants.
 * INFO object must contain at minimum: STARTING, STARTED properties.
 */

/**
 * @typedef {Object} LogSequence
 * @property {string[]} required - Required log messages that must appear in order
 * @property {string[]} operational - Optional operational log messages
 * @property {string[]} forbidden - Log messages that should not appear
 */

/**
 * @typedef {Object} AgentTestConfig
 * @property {Object} params - Configuration parameters for the agent
 * @property {number} timeout - Timeout in milliseconds for the agent test
 */

/**
 * @typedef {Object} AgentConfig
 * @property {Object} params - Agent parameters
 * @property {number} timeout - Agent timeout
 */

/**
 * @typedef {Object} AppConfiguration
 * @property {string} package - Package name of the app
 * @property {string|string[]} name - App name(s) in different languages
 * @property {string} [icon_big] - Base64 encoded large icon data
 * @property {string} [icon_small] - Base64 encoded small icon data
 * @property {string} [iconLarge] - Base64 encoded large icon data
 * @property {boolean} [navigation_bar] - Whether app appears in navigation bar
 * @property {string|string[]} [screen] - Screen name(s) where app appears
 * @property {string|string[]} [padding] - Padding configuration
 * @property {number} [scale] - Scale factor
 * @property {number} [dpi] - DPI setting
 */

/**
 * @typedef {Object} MediaConfiguration
 * @property {Object.<string, MediaItem>} media - Media items by flow name
 */

/**
 * @typedef {Object} MediaItem
 * @property {string} pageName - Page name for the media item
 * @property {Object.<string, string>} name - Localized names (EN, RU, etc.)
 * @property {string} [icon] - Base64 encoded icon data
 * @property {string} [iconLarge] - Base64 encoded large icon data
 * @property {boolean} [autoPlay] - Whether to auto-play the media
 */

/**
 * @typedef {Object} KeyboardConfiguration
 * @property {Object} attrs - Keyboard attributes
 * @property {boolean} attrs.balloon - Whether balloon is enabled
 * @property {number} attrs.height - Keyboard height
 * @property {number} attrs.key_type - Key type
 * @property {number} attrs.key_xmargin - Horizontal key margin
 * @property {number} attrs.key_ymargin - Vertical key margin
 * @property {boolean} attrs.qwerty - Whether QWERTY layout is enabled
 * @property {boolean} attrs.qwerty_uppercase - Whether QWERTY uppercase is enabled
 * @property {boolean} attrs.repeat - Whether key repeat is enabled
 * @property {boolean} attrs.skb_cache_flag - Keyboard cache flag
 * @property {boolean} attrs.skb_sticky_flag - Keyboard sticky flag
 * @property {string} attrs.skb_template - Keyboard template
 * @property {number} attrs.width - Keyboard width
 * @property {Object[]} rows - Keyboard rows
 * @property {Object} drawable - Drawable resources
 * @property {string} drawable.english_input_method - English input method drawable
 * @property {string} drawable.english_input_method_white - White English input method drawable
 * @property {string} drawable.russian_input_method - Russian input method drawable
 * @property {string} drawable.russian_input_method_white - White Russian input method drawable
 * @property {string} layout - Keyboard layout
 * @property {boolean} enable_voice - Whether voice input is enabled
 * @property {Array} custom_keys - Custom keys configuration
 */

/**
 * @typedef {Object} KeyboardRow
 * @property {number} start_pos_x - Starting X position
 * @property {number} start_pos_y - Starting Y position
 * @property {Object[]} keys - Array of keys in the row
 */

/**
 * @typedef {Object} KeyboardKey
 * @property {string} label - Key label
 * @property {number} code - Key code
 */

// Import all agent log constants from voboost-script using named exports
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
} from '../../voboost-script/agents/adas-activation-log.js';

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
 * @param {string[]} [allowErrors=[]] - Array of error messages to allow
 * @returns {LogSequence} Log sequence configuration
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
 * @type {Object.<string, LogSequence>}
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

    'app-multi-display-mod': createLogSequence(
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

    'adas-activation-mod': createLogSequence(
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
 * Default fallback messages for agent validation when specific agent config is not found
 * @type {Object}
 */
export const DEFAULT_FALLBACK_MESSAGES = {
    STARTING: 'Agent starting',
    STARTED: 'Agent started',
};

/**
 * Default test timeout for tests (in milliseconds)
 * @type {Object.<string, number>}
 */
export const TEST_TIMEOUTS = {
    STANDARD: 8000, // Standard test timeout (8 seconds) - reduced from 10 seconds for faster execution
};

/**
 * Test configurations for specific agents
 * Maps agent names to configuration objects with parameters and timeouts
 * @type {Object.<string, AgentTestConfig>}
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
    'app-multi-display-mod': {
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
    'adas-activation-mod': {
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
 * Gets test configuration for a specific agent
 * @param {string} agentName - Name of the agent
 * @returns {AgentConfig} Test configuration
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
 * Re-export typedefs for external use
 * @typedef {import('./TestFixtures.js').LogSequence} LogSequence
 * @typedef {import('./TestFixtures.js').AgentTestConfig} AgentTestConfig
 * @typedef {import('./TestFixtures.js').AgentConfig} AgentConfig
 * @typedef {import('./TestFixtures.js').AppConfiguration} AppConfiguration
 * @typedef {import('./TestFixtures.js').MediaConfiguration} MediaConfiguration
 * @typedef {import('./TestFixtures.js').MediaItem} MediaItem
 * @typedef {import('./TestFixtures.js').KeyboardConfiguration} KeyboardConfiguration
 * @typedef {import('./TestFixtures.js').KeyboardRow} KeyboardRow
 * @typedef {import('./TestFixtures.js').KeyboardKey} KeyboardKey
 */

export default {
    TEST_TIMEOUTS,
    AGENT_TEST_CONFIGS,
    AGENT_LOG_SEQUENCES,
    DEFAULT_FALLBACK_MESSAGES,
    getAgentConfig,
};
