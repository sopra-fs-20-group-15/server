# Just One
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sopra-fs-20-group-15_server&metric=alert_status)](https://sonarcloud.io/dashboard?id=sopra-fs-20-group-15_server)

This project aims to recreate the popular table top game "Just One" as a web application, while staying as true as possible to the original version. 

The goal of this web application is to let players all around the world to enjoy "Just One" together. This application  allows for players to add bots to their games (which as of now can only give clues), if they do not have enough players to play a game or simply want more players in the game. It's also possible to play a game without having other human co-players as long as you add at least two bots to meet the minimum required player size of three.

The web application is implemented in a way that it recognizes if a player has left the game during a game session and replaces the missing player with a bot at the end of the turn.

This repository implements the server side of the said web application. To view the client side, please visit the following git repository: https://github.com/sopra-fs-20-group-15/client.git

## Technologies used

This project is written in Java (JDK 13) and uses Spring Boot for additional tools. Repositories used in this project are managed and stored through JPA / Hibernate. The Client and Server communicate to each other through REST API calls. This is also the reason why this project is implemented with Spring Boot, which allows for easy management of REST endpoints.

This project uses Gradle for its build automation and deployment ([Building With Gradle](#building-with-gradle)).

## High-level components
### Game Setup
The Game Setup component handles the setup of a game. Meaning that it allows players to create public or private lobbies and other players to join them. It also allows the creator of a lobby to set a maximum amount of players allowed to join the lobby and add some bots to the game lobby. When creating a active game the values set by the game setup are taken into consideration.

The main class of this component would be the [GameSetupService](src/main/java/ch/uzh/ifi/seal/soprafs20/service/GameSetUpService.java). This service envelops all methods necessary to get the behaviour described above. The methods in this class have descriptive names, so that their function is self explanatory. The client interacts with this service through Rest-Requests which are handled by the services own [controller](src/main/java/ch/uzh/ifi/seal/soprafs20/controller/GameSetUpController.java).
### Active Game
The Active Game is responsible for handling the creation and deletion of a game. The creation of a game consists of initializing all the parameters and adding players, bots and cards to the game, which are necessary for the game logic to have something to work with.

The main class of this component is the [ActiveGameService](src/main/java/ch/uzh/ifi/seal/soprafs20/service/ActiveGameService.java). Within this service you will find various methods for the creation of a game and its initialization as well as methods for the deletion of a game and its related methods. This class' functionality again is accessed by an own [controller](src/main/java/ch/uzh/ifi/seal/soprafs20/controller/ActiveGamesController.java) which handles Rest-Requests.
### Game Logic
The Game Logic component is responsible for handling the entire game logic of a active game from start to finish.

The main class of this is the [LogicService](src/main/java/ch/uzh/ifi/seal/soprafs20/service/LogicService.java). The LogicService is responsible for the entire game logic. It's broken down into several self explanatory methods with clear functions. All the methods, that should not be accessible during certain phases of a round or should have different functionality depending on the phase the game currently is in, are implemented using the state pattern. Many of the methods within the LogicService need to leverage classes stored in the [GameLogic Folder](src/main/java/ch/uzh/ifi/seal/soprafs20/GameLogic). This component again is accessed by the client through its own [controller](src/main/java/ch/uzh/ifi/seal/soprafs20/controller/LogicController.java). 
### Player Validation
This component is meant to check if players making Rest-Requests on a specific active game are actually part of the game and/or if they have the necessary rights to make the request. This component is utilized throughout most of the methods game logic component, since it is essential to disallow unwanted requests to be made.

The main class of this component is the [ValidationService](src/main/java/ch/uzh/ifi/seal/soprafs20/service/ValidationService.java). This service includes three methods. One to simply check if the player is part of the game, one to check if the player is part of the game and the active player and one to check if the player is part of the game and a passive player. Since this service mainly accessed through the game logic component and there is no need to access the ValidationService for other reasons, there is no need for this service to have its own controller.
### Word Comparer
The Word Comparer is a component used for clue and guess evaluation and analysis. It interacts with the game logic through calls in the game logic methods.

The main and only class of this component is the [WordComparer](src/main/java/ch/uzh/ifi/seal/soprafs20/GameLogic/WordComparer.java) itself. It has a method that checks the clues and returns a  map that indicates which clues are valid and which ones are not. It also has a few of helper classes, which aid in that task. There is also a method to check if a given guess was right or not. Again, since this class is not meant to be accessed by the client, but is automatically invoked by the game logic component, there is no controller which allows for that.

## Launch and Deployment
This section covers how to launch and deploy the server side of the application. To view how to do the same for the client side, please visit the following git repository: https://github.com/sopra-fs-20-group-15/client.git

### Building with Gradle 

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

#### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Tests
If you wish to run the tests without having to build the application with gradle. Feel free to run the entire test suite found [here](src/test) through your IDE.

### Deployment
This project is set to deploy to Heroku (production build) and SonarCloud (quality control). This is done by using Github Actions. If you wish to deploy to your own Heroku and SonarCloud accounts you will have to create the corresponding secrets mentioned in [deploy.yml](.github/workflows/deploy.yml) (config file) for your branch / fork.

## Roadmap
For any developers wanting to fork this project or contribute to this project by implementing new features, we highly recommend implementing following features:

* Allow for bots to take the role of an active player as well (even more sophisticated bots needed).
* Implement a spectator mode for running games.
* Add (optional) rule sets, that make the game more interesting.

## Authors and acknowledgement

Contributors to this project in alphabetical order:

* Cedeño, Jordan
* Eder, Charlotte
* Haemmerli, Raphael
* Mitiyamulle Arachchige, Kai
* Vu, Minh Phuong


Special Thanks to:
* Scheitlin, Alex (Teaching Assistant, General Development Advisor)
* Friends and Family (Beta Testers)

## License
   Copyright 2020 Sopra Group 15

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.




