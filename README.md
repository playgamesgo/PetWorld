# PetWorld

## Description
TODO: Add description

## Dependencies
- Docker
- Docker Compose
- Java 21

## Running
1. Clone the repository 
```sh
git clone https://github.com/playgamesgo/PetWorld.git
```
2. Change postgres password in `Backend/compose.yaml` file
3. Change postgres password in `Backend/src/main/resources/application.properties` file
4. Change jwtSecret in `Backend/src/main/resources/application.properties` file
5. Setup Mail Server in `Backend/src/main/resources/application.properties` file
6. Run the following command to start the backend
```sh
cd Backend
./gradlew bootRun
```

## Tips
- Running backend using jar file
  - Run the following command to compile the backend to jar
    - ```sh
      cd Backend
      ./gradlew bootJar
      ```
  - Jar file will be generated in `Backend/build/libs` folder
  - You need to start `compose.yaml` file first before running the jar file
    - ```sh
      docker-compose up -d
      ```
  - Run the following command to start the backend using jar file
    - ```sh
      java -jar <jar-file-name>.jar
      ```
