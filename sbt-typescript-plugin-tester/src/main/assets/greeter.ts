// From http://www.typescriptlang.org/Tutorial
function greeter(person: string) {
    return "Hello, " + person;
}

function greeter2(person: string) {
    return "Hello, " + person;
}

var user = "Jane User";

document.body.innerHTML = greeter(user);