import js from '@eslint/js';
import reactHooks from 'eslint-plugin-react-hooks';
import globals from 'globals';

export default [
  { ignores: ['dist'] },
  {
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: globals.browser,
      parserOptions: { ecmaFeatures: { jsx: true } },
    },
    plugins: { 'react-hooks': reactHooks },
    rules: {
      ...js.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,
      curly: ['error', 'all'],
      eqeqeq: ['error', 'always'],
      'no-unused-vars': 'error',
      'no-var': 'error',
      'prefer-const': 'error',
      'no-duplicate-imports': 'error',
      'max-lines': ['error', 500],
      'max-len': ['error', { code: 120, ignoreUrls: true }],
      camelcase: ['error', { properties: 'never' }],
    },
  },
  {
    files: ['src/**/*.{js,jsx}'],
    rules: {
      'no-magic-numbers': ['error', { ignore: [-1, 0, 1], ignoreArrayIndexes: true, ignoreDefaultValues: true }],
    },
  },
];
