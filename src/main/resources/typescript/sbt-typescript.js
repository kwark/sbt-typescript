/*global process, require, __dirname*/

(function () {
    "use strict";

    var args = process.argv;

    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var TARGET_ARG = 3;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(args[SOURCE_FILE_MAPPINGS_ARG]);
    var target = args[TARGET_ARG];
    var options = JSON.parse(args[OPTIONS_ARG]);

    var TypeScript = require('./tsc');

    console.log('TypeScript args:', args);
    console.log('Started from directory:', __dirname);

    var typeScriptArgs = ['--outDir', options.outDir, '--module', options.module];
    if (options.targetES5) {
        typeScriptArgs = typeScriptArgs.concat(['--target', 'ES5']);
    }
    if (options.sourceMap) {
        typeScriptArgs.push('--sourcemap');
    }
    if (options.noImplicitAny) {
        typeScriptArgs.push('--noImplicitAny');
    }
    if (options.removeComments) {
        typeScriptArgs.push('--removeComments');
    }
    typeScriptArgs.push(sourceFileMappings[0][0]);

    console.log('Passing args to tsc:', process.argv);
    var io = TypeScript.IO;
    io.arguments = typeScriptArgs;

    var batch = new TypeScript.BatchCompiler(io);
    batch.batchCompile();
})();