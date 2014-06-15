/*global process, require */

(function () {
    "use strict";

    var args = process.argv;

    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var TARGET_ARG = 3;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(args[SOURCE_FILE_MAPPINGS_ARG]);
    var target = args[TARGET_ARG];
    var options = JSON.parse(args[OPTIONS_ARG]);

    console.log('TypeScript args:', args);
    console.log('Started from directory:', __dirname);

    args = ['--outDir', options.outDir, '--module', options.module];
    if (options.targetES5) {
        args = args.concat(['--target', 'ES5']);
    }
    if (options.sourceMap) {
        args.push('--sourcemap');
    }
    if (options.noImplicitAny) {
        args.push('--noImplicitAny');
    }
    if (options.removeComments) {
        args.push('--removeComments');
    }
    args.push(sourceFileMappings[0][0]);
    process.argv = args;

    console.log('Passing args to tsc:', process.argv);
    
    require('./tsc');
})();