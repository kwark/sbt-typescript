/*global process, require, __dirname*/

/*
 * transpile a number of files.
 *
 * Arguments:
 * 0 - name given to the command invoking the script (unused)
 * 1 - filepath of this script (unused)
 * 2 - array of file paths to the files to be converted
 * 3 - the target folder to write to (unused - not required)
 * 4 - tsc options as a Json object
 *
 */
(function () {
    "use strict";

    var path = require('path');
    var console = require("console");

    var ts = require('./tsc');

    var args = process.argv;
    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(args[SOURCE_FILE_MAPPINGS_ARG]);
    var options = JSON.parse(args[OPTIONS_ARG]);
    var results = [], problems = [];
    var inputFileNames = [];

    for (var i = 0; i < sourceFileMappings.length; i++) {
        inputFileNames.push(sourceFileMappings[i][0]);
    }

    var host = ts.createCompilerHost(options);
    var program = ts.createProgram(inputFileNames, options, host);
    var result = program.emit();

    var diagnostics = program.getSyntacticDiagnostics();
    // If we didn't have any syntactic errors, then also try getting the global and
    // semantic errors.
    if (diagnostics.length === 0) {
        diagnostics = program.getOptionsDiagnostics().concat(program.getGlobalDiagnostics());
        if (diagnostics.length === 0) {
            diagnostics = program.getSemanticDiagnostics();
        }
    }

    var allDiagnostics = diagnostics.concat(result.diagnostics);
    var actualErrors = 0;
    allDiagnostics.forEach(function (diagnostic) {
        if (diagnostic.file) {
            var lineChar = ts.getLineAndCharacterOfPosition(diagnostic.file, diagnostic.start);
            var lineStarts = ts.getLineStarts(diagnostic.file);
            problems.push({
                message: ts.flattenDiagnosticMessageText(diagnostic.messageText),
                lineNumber: lineChar.line,
                characterOffset: lineChar.character,
                source: diagnostic.file.fileName,
                severity: diagnostic.category == 0 ? "warning" : "error",
                lineContent: diagnostic.file.text.substring(lineStarts[lineChar.line - 1], lineStarts[lineChar.line]).replace(/\n$/, "").replace(/\r$/, "")
            });
        } else {
            problems.push({
                message: ts.flattenDiagnosticMessageText(diagnostic.messageText),
                lineNumber: 0,
                characterOffset: 0,
                source: "unknown",
                severity: diagnostic.category == 0 ? "warning" : "error",
                lineContent: "-"
            });
        }
        actualErrors++;
    });
    if (actualErrors == 0) {
        sourceFileMappings.forEach(function(sourceFileMapping) {
            var inputFileName = sourceFileMapping[0];
			var outputDirectory = path.join(options.outDir, path.dirname(sourceFileMapping[1]));
            var outputFilePath = path.join(outputDirectory, path.basename(inputFileName, '.ts') + ".js");
            var generatedFiles = [outputFilePath];

            if (options.sourceMap) {
                generatedFiles.push(outputFilePath + ".map");
            }
            results.push({
                source: inputFileName,
                result: {
                    filesRead: [inputFileName],
                    filesWritten: generatedFiles
                }
            });
        });
    }

    console.log("\u0010" + JSON.stringify({results: results, problems: problems}));

})();
