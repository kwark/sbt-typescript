sbt-typescript
================

An SBT plugin to run the [TypeScript](http://www.typescriptlang.org/) compiler inside of SBT.
The Typescript compiler is modified to generate ExtJs compatible classes. 
The actual tsc.js compiler is provided by this [fork](https://github.com/fabioparra/TypeScript).

Currently it supports typescript 1.8

To use this plugin add the following to your `plugins.sbt` file:

    addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.0")
    addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.1.4")
    addSbtPlugin("be.venneborg.sbt" % "sbt-typescript-extjs" % "1.0.0-SNAPSHOT")

This plugin is compatible with [sbt-web](https://github.com/sbt/sbt-web).

Usage
=====

Simply run the `tsext` command to invoke the TypeScript compiler.

Use with sbt-web
================

To use this plugin with sbt-web start by enabling SbtWeb in your build.sbt file:

    lazy val root = (project in file(".")).enablePlugins(SbtWeb)

Once configured, any `*.ts` files placed in `src/main/assets` will be compiled to JavaScript code in `target/web/public`.

Supported settings:

* `sourceMap` When set, generates sourceMap files. Defaults to `false`.

  `TypeScriptKeys.sourceMap := true`

* `targetES5` When set, target ECMAScript 5. Defaults to `false` (target ECMAScript 3).

  `TypeScriptKeys.targetES5 := true`

* `noImplicitAny` When set, warn on expressions and declarations with an implied 'any' type. Default to `false`.

  `TypeScriptKeys.noImplicitAny := true`

* `removeComments` When set, do not emit comments to output. Defaults to `false`.

  `TypeScriptKeys.removeComments := true`

* `moduleType` Specify module code generation: Can be 'Commonjs' or 'Amd'. Defaults to `Amd`.

  `TypeScriptKeys.moduleType in Assets := TypeScriptKeys.ModuleType.Amd`
  `TypeScriptKeys.moduleType in TestAssets := TypeScriptKeys.ModuleType.CommonJs`

The plugin is built on top of [JavaScript Engine](https://github.com/typesafehub/js-engine) which supports different JavaScript runtimes.

