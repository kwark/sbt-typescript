/*global process, require, __dirname*/

/*
 * transpile a number of files.
 *
 * Arguments:
 * 0 - name given to the command invoking the script (unused)
 * 1 - filepath of this script (unused)
 * 2 - array of file paths to the files to be converted
 * 3 - the target folder to write to (unused - not required)
 * 4 - jshint options as a Json object
 *
 * Json array tuples are sent to stdout for each file in error (if any). Each tuple is an array where the
 * element 0 corresponds to the file path of the file linted, and element 1 is JSHINT.errors.
 */
(function () {
    "use strict";

    var path = require('path');
    var console = require("console");
    //console.log(__dirname);

    var ts = require('./tsc');

    var args = process.argv;
    var SOURCE_FILE_MAPPINGS_ARG = 2;
    var TARGET_ARG = 3;
    var OPTIONS_ARG = 4;

    var sourceFileMappings = JSON.parse(args[SOURCE_FILE_MAPPINGS_ARG]);
    var target = args[TARGET_ARG];
    var options = JSON.parse(args[OPTIONS_ARG]);

    var inputFileName = sourceFileMappings[0][0],
        outputDirectory = path.join(options.outDir, path.dirname(sourceFileMappings[0][1]));

    var baseName = path.basename(inputFileName, '.ts');
    var outputFileName = baseName +".js";
    var outputFilePath = path.join(outputDirectory, outputFileName);
    var generatedFiles = [ outputFilePath ];

    //console.log('Output file:', outputFilePath);

    if (options.sourceMap) {
	    generatedFiles.push(outputFilePath+".map");
    }

    var results = [], problems = [];

    var host = ts.createCompilerHost(options);
    var program = ts.createProgram([inputFileName], options, host);
    var checker = ts.createTypeChecker(program, true);
    var result = checker.emitFiles();

    var actualErrors = 0;
    var allDiagnostics = program.getDiagnostics().concat(checker.getDiagnostics()).concat(result.diagnostics);
    allDiagnostics.forEach(function (diagnostic) {
        var lineChar = diagnostic.file.getLineAndCharacterFromPosition(diagnostic.start);
		var lineStarts = diagnostic.file.getLineStarts();
		//console.log(diagnostic);
		problems.push({
			message: diagnostic.messageText,
			lineNumber: lineChar.line,
            characterOffset: lineChar.character,
			source: diagnostic.file.filename,
			severity: diagnostic.category == 0 ? "warning" : "error",
			lineContent: diagnostic.file.text.substring(lineStarts[lineChar.line-1], lineStarts[lineChar.line]).replace(/\n$/,"").replace(/\r$/,"")
		});
		actualErrors++;
    });
	

	if (actualErrors == 0) {
		results.push({
			source: inputFileName,
			result: {
				filesRead: [inputFileName],
				filesWritten: generatedFiles
			}
		});
	}

    //console.log(JSON.stringify({results: results, problems: problems}));
    console.log("\u0010" + JSON.stringify({results: results, problems: problems}));

})();
