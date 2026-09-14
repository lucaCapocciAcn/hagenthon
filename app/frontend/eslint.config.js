// @ts-check
const eslint = require("@eslint/js");
const { defineConfig } = require("eslint/config");
const tseslint = require("typescript-eslint");
const angular = require("angular-eslint");
const prettier = require("eslint-config-prettier/flat");

module.exports = defineConfig([
  {
    files: ["**/*.ts"],
    extends: [
      eslint.configs.recommended,
      tseslint.configs.recommended,
      tseslint.configs.stylistic,
      angular.configs.tsRecommended,
      // Spegne le regole ESLint che litigherebbero con Prettier sulla formattazione.
      // Deve restare l'ultimo elemento di `extends`.
      prettier,
    ],
    processor: angular.processInlineTemplates,
    rules: {
      "@angular-eslint/directive-selector": [
        "error",
        {
          type: "attribute",
          prefix: "app",
          style: "camelCase",
        },
      ],
      "@angular-eslint/component-selector": [
        "error",
        {
          type: "element",
          prefix: "app",
          style: "kebab-case",
        },
      ],
    },
  },
  {
    files: ["**/*.html"],
    extends: [
      angular.configs.templateRecommended,
      // Accessibilità: su questo progetto è il prodotto, non una rifinitura.
      angular.configs.templateAccessibility,
      prettier,
    ],
    rules: {
      // Attivate esplicitamente: non fanno parte dei preset sopra.
      "@angular-eslint/template/no-positive-tabindex": "error",
      "@angular-eslint/template/button-has-type": "error",
      "@angular-eslint/template/prefer-ngsrc": "error",
    },
  },
]);
