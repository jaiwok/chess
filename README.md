# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Phase 3: Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAEYAdE8z2oEAK7YYAYjRgKksYACUUeyRVCzkkCDQAgHcACyQwMURUUgBaAD5yShooAC4YAG0ABQB5MgAVAF0YAHpvAygAHTQAbwAiNsoggFsUXpLemF6AGkncdSToDjGJ6cmUIeAkBGXJgF9MYWKYfNZ2LkoyvoGoYdHxyZneudUFqCX71d71ze2P-bZONxYCdDqIylBItEMlAABQRKIxSgRACO3jUYAAlAciqJjgVZPIlCp1GV7CgwABVdow663LEExTKNSqPFGHQlABiSE4MCplAZMB0oVpwBGmAZROZxxBOJUZTQ3gQCGxIhUrIlTJJIAhchQfNh1wZ9O0kvUrOMJQUHA4vPaDJVVFxJw1xNUJW1KF1Cm8YBSMOAPpSRvFJs1LJOFqtNu9vvtoLVOROAPOpXCkMRUAiqkVWGTQOlBUOFxgV3atwmZRWjwDvrqEAA1ugK5M9g7KAX4MhzGUAEwABj7XVLg1FoxglYekxrKTrjbQzZW+3QHFMnh8fn80HYZJgABkIFFEv5UulMl3coWisWqrVGi0DOp4mgh5MRSNlp9nq93r0Jvsi8CBR5sWw43KOH6PF+iwfv8Zz5jKqooGUCAHtyML7oeKJojEWLxigzqhq6ZTFEgABmlj6jSZajjMUFvMahJhhGOjjjApHcja3IwIa2iCsK1EjLR+gvIsIaMa6iaXohZTXGxPicA6Tr4oRzLujqGQxn607Bi6UrMZa1owJpcayoYkmnICxYYdyWY5pgwFQOZAGsd0r4CXcv7VoGs5NuMf5tsCJxZOYrH9oOiSuf07kQVO3kNr5v4wEuCkeF4vgBJ4KDoHuB4+Mwx5pBkmDBRehTUNe0gAKK7pVdSVU0zQPqoT5dNOPloAFrIOSUfRtfF85-PZcGUPkeHIblProRNYBYeiuGmfkukkuxnCaf6cVzgxjISXkFpsRxMBccZvFCjAfVzmJ23MpJY1yd4PLTpAF14YtKkkhCQwQDQa3negW2mqo+R7REn00EZgYmYhHbdTl9h5bZCC5sNgVScUlz-leKOdtkYC9gOQ7JSuqXrgEEI2ruUIwAA4qOLIFaexXnswCFoxUVO1Q19ijq1G3oJ1SbIz1vS-QNi5DZZjl5LdyAxOhUKzThikqK94mqStHA-bzaD-Uxu3svtPJHRDJ2hCLl0AzdpllKR8k2o9-WmHh6pvSypLkgoSo0yMqgwjrrodsYrHq2dSowFz3uHYkXsoPaStmQLEtlBTMQI0jEsdgBlyTOHahjBUfQ5wAktIeeeZMJ4ZPq5Z+Y8OgIKA9ZV+BNfZ6OAByo5+bsMANBj5VYyVuMwGFXQlq33t5+UBejsXpefBXerRS3vR1w3TfvsvOcdyMXc94Tq5pRu2DeFA2DcPA6mGNHySFWeOOspnFQ1PUnPc0EWtDlvze-n3xT5N1vUtYQV6F-DeP9xYplGlbGAHpdQwjgJfBWmI44qyustDimtaz9T9tdPW1sDpG1jCbM6WtzZMSgdJO6D1AxPT5nHZ2qszRlFgRkaOMJQEoBwWacygcyjAEMmgFASRqajljk7cyMMEGegyKnCB8EWYgRATPEulZei-3bOZQeeNwrTxGLPVR+9ibpX8JYFASoIDCIAFIQC4tffwq8QD1kZvfBRqYqgUjvM0HOPMsFziHGfYApioBwAgChKAMwi7SHUZLABwsgEfGmE8euQSQlhJikovRKjwEOQoY6OUMAABWNi0AwiKTZFAqI5ooLyEtN06tMEzmwWQnae1g6EKDMQs2tTLaUJtvdO2NCHb0IIow12BsOBsMiVw8MzEg4HRzpHERIwBSnUic066uSwRUJtDnR2C0akuxKAYMA0dUnQHXigGEASUmhOgNMoG+sQApBQI4mA6xsBgFCNcygZzYC2NEdodZ3CWZbO+cE25sB3mfOqbUko-COD6jrGwkW9y8FnUMpAJZMdeJvkMDbWAYLfnDITimMo1jynZkRnIkauSQLRI7Fo4e+MeiGLXMYzwgTOyelgMAbAZ9CBxASDfBmg8H6YzKOUKqNU6oNWMPzICgsSywXTiC-JIBuB4F9sM5SozmEaqgNIAAQlq2p5p2QfS+oYYAocc4MhmPqO14MiHyGJQqxOXK8CyIchncVSr+ZBSZtogmZgFJAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
