# minesweeper-API

Minesweeper game backend API for Deviget Code Test. [Instructions](CHALLENGE.md)

## LocalRunning

#### With gradle (Redis is required)

`gradle run`

#### With Docker

Create a network

`docker network create docker_net`

Start up Redis first on created newtork

`docker run --rm -d --name redis.local --network docker_net redis:alpine`

Start up Minesweeper-API with Gradle under Docker

`docker run --rm -p "8080:8080" --name=$(pwd | sed "s/.*\///") --network docker_net -v $HOME/.docker-gradle:/home/gradle/.gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:alpine gradle run`

## API documentation

#### Create a new game

`POST /game`

Headers
- Content-type: application/json

Body (all parameters are optional)
```
{
    "width" : 15,  // Default = 10
    "height" : 15, // Default = 10
    "mines" : 20   // Default = 10
}
```

## Public site


### TODO

- Swagger
- Automatic tests (unit or functional)

