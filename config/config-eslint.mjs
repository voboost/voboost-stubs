import baseConfig from '../../voboost-codestyle/config-eslint.mjs';

export default [
    ...baseConfig,
    // Allow console statements in lib files temporarily (for debugging purposes)
    {
        files: ['lib/**/*.js'],
        rules: {
            'no-console': 'off',
        },
    },
];
