# Pelotech Payments Facade

This repo exists as a small playground for discussing some code/repository patterns that I'm fond of. It models a fake "payments" service that writes Authorization Requests and Settlement Requests to a Postgres Database.

## Prerequisites
- Java 21
- Docker

## Building

```
./gradlew build
```

## Repo Structure/Ideas

```
  server/
  client/ (TBD)
```

Basic principles of the repository:

- Modeled after "data oriented programming" in the Java world (read: Functional programming with class ceremony)
- A class never has more than one public entrypoint
  - (Combined with DI this allows for an approximation of Partial Function Application)
- Anything that can be verified at compile time *should* be verified at compile time.
- As much as possible we avoid duplicating data definitions (read: contract first development)

### Projects Leveraged

Behind the scenes this project leverages:

- Micronaut
- OpenAPI Generator
- TestContainers
- Flyway
- jOOQ

### Possible Improvements
- Fix micronaut/test-resources-plugin to avoid bringing up the test-resources server during unit tests
- Flesh out client generation example
  - (Can do this in the most Gradle way possible as well)
- Custom OpenAPI templates to ensure models are records instead of mutable Java "bean" styled classes
- Add custom static analysis rules to enforce programming guidelines
- Github Actions CICD pipeline

### Extraneous Notes

Initially this was meant to be focused on spec-driven code generation for servers and clients (with the opportunity to create utilities on top to make that easier) however I decided to use Micronaut as I don't have much experience with it and found they've done much of what I would typically want out of code generation utilities. As a result there is only currently a "server" project.

The main things this repository highlights are "contract" driven development practices. The basic idea is that anytime we have a data format contract we should derive all of the code that interacts with those data contracts. 

In the case of this repository that means the OpenAPI spec drives the code models and controller interfaces, and the SQL table definitions drive the generation of code that interacts with the database (via jOOQ).

Oftentimes these ideas sound good in practice but require a lot of heavy lifting in order to make them work together, while I would traditionally write that glue myself in this case I was able to find some good plugins that did most of the work for me.
