# Just One

This project aims to recreate the popular table top game "Just One" as a web application, while staying as true as possible to the original. The goal of this web application is to allow for players around the world to play "Just One" together. 
This repository implements the server side of the said web application. To view the client side, please visit the following git repository: https://github.com/sopra-fs-20-group-15/client.git

## Technologies used

This project uses Gradle for its build automation and deployment.

The Client and Server communicate to each other through REST API Calls. This is also the reason why this project is implemented with Spring Boot, which allows for easy management of REST endpoints.

## High-level components







































# SoPra RESTful Service Template FS20

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sopra-fs-20-group-15_server&metric=alert_status)](https://sonarcloud.io/dashboard?id=sopra-fs-20-group-15_server)

## Getting started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/

## Setup this Template with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)) and make sure Java 13 is installed on your system.

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

## Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

## Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing

Have a look here: https://www.baeldung.com/spring-boot-testing

test_kai
