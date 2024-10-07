# PetWorld

## Description
This is pet adoption platform where users can adopt pets and post ads for adoption. This project is developed using Spring Boot for backend and Angular for frontend.
Created for EPAM IT Technology Marathon 2024.

## Dependencies
- Docker
- Docker Compose
- Java 21
- Node 18.20+

## Running
1. Clone the repository 
```sh
git clone https://github.com/playgamesgo/PetWorld.git
```
2. Change postgres password in `Backend/compose.yaml` file
3. Change postgres password in `Backend/src/main/resources/application.properties` file
5. Change frontend url in `Backend/src/main/resources/application.properties` file
6. Change jwtSecret in `Backend/src/main/resources/application.properties` file
6. Setup Mail Server in `Backend/src/main/resources/application.properties` file
7. Run the following command to start the backend
```sh
cd Backend
./gradlew bootRun
```

8. In `Frontend/src/app/services/api.service.ts` change BACKEND_API_URL to your local backend url;
9. Run the following command to start the frontend
```sh
cd Frontend
npm install
npm run start
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

- Preventing default data from being loaded
  - In `Backend/src/main/resources/` remove `dictionary.json` file to prevent dictionary data from being loaded.
  You need to have a dictionary data in the database for running the frontend.
  - In `Backend/src/main/resources/` remove `data.json` file to prevent default ads data from being loaded.
  
- Running frontend in docker
  - Run the following command to build the frontend docker image
    - ```sh
      cd Frontend
      docker compose up -d
      ```
