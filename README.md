# Just One
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sopra-fs-20-group-15_server&metric=alert_status)](https://sonarcloud.io/dashboard?id=sopra-fs-20-group-15_server)

This project aims to recreate the popular table top game "Just One" as a web application, while staying as true to the original as possible. 

The goal of this web application is to allow for players around the world to play "Just One" together. This application  allows for players to add bots to their games, if they do not have enough players to play a game or simply want more players in the game. It's also possible to play a game without having other human co-players as long as you add at least two bots to meet the minimum required player size of three.

The web application is implemented in a way that it recognizes if a player has left the game during a game session and replaces the missing player with a bot at the end of the turn.

This repository implements the server side of the said web application. To view the client side, please visit the following git repository: https://github.com/sopra-fs-20-group-15/client.git

## Technologies used

This project is written in Java (JDK 13) and uses Spring Boot for additional tools. Repositories used in this project are managed and stored through hibernate. The Client and Server communicate to each other through REST API Calls. This is also the reason why this project is implemented with Spring Boot, which allows for easy management of REST endpoints.

This project uses Gradle for its build automation and deployment ([Building With Gradle](##building-with-gradle)).

This project is set to deploy to Heroku (running build on the web) and SonarCloud (quality control). If you wish to deploy to your own Heroku and SonarCloud create secrets for the secrets in [deploy.yml](.github/workflows/deploy.yml).

## High-level components





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


