import baseConfig from '../../voboost-codestyle/config-eslint.mjs';

export default [
    ...baseConfig,
    // Allow console in lib files temporarily (for debugging)
    {
        files: ['lib/**/*.js'],
        rules: {
            'no-console': 'off',
        },
    },
];
