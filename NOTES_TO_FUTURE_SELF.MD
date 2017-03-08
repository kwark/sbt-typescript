In order to switch to newer typescript version:

* get tsc.js file (from https://github.com/fabioparra/TypeScript) and edit it to remove default execution and wrap it in an IFFE
 
```
(function() {
 var ts;

--- original tsc.js code ---

module.exports = ts;
})();
```

* sometimes the interface to the typescript compiler also changes, the sbt-typescript.js file then needs to be changed as well.