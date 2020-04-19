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

Response
```{"status":"created","game_id": "0C899"}```

#### Flag a cell

`POST /game/:gameId/flag`

Headers
- Content-type: application/json

Body (all parameters are required)
```
{
    "x" : 5,  // from left (1) to right (width)
    "y" : 5,  // from top (1) to bottom (height)
}
```

Response
```{"status":"OK", "message":"updated"}```

### Unflag a cell

`DELETE /game/:gameId/flag`

Body, responses and checks are the same as "flag"

#### Press a cell
When you press a cell, some checks are validated
- if there is not an adjacent mined cell, automatically clicks the adjacents, recursively
- automatically end games if win or loose
- response size may vary according to game status ('active', 'winner' or 'looser') but they will have the same json structure

`POST /game/:gameId/press`

Headers
- Content-type: application/json

Body (all parameters are required)
```
{
    "x" : 5,  // from left (1) to right (width)
    "y" : 5,  // from top (1) to bottom (height)
}
```

Responses
If game is still active, it will return the list of modified cells, according to the adjacence rule
```
{
	code": "ok",
	"message": "Clic accepted, game updated",
	"game": {
		"id": "DMDP5",
		"status": "active",
		"game_map": {
			"5-5": {
				"cell_key": "5-5",
				"x": 5,
				"y": 5,
				"clicked": true,
				"mined": false,
				"flag": false,
				"adjacents": 0
			},
            ...
			},
			"5-3": {
				"cell_key": "5-3",
				"x": 5,
				"y": 3,
				"clicked": true,
				"mined": false,
				"flag": false,
				"adjacents": 0
			}
		}
	}
}
```

if game is won, it will return all the map, including mined cells
```
{
	"code": "ok",
	"message": "Clic accepted, game updated",
	"game": {
		"id": "DMDP5",
		"status": "winner",
		"game_map": {
			"2-3": {
				"cell_key": "2-3",
				"x": 2,
				"y": 3,
				"clicked": true,
				"mined": false,
				"flag": false,
				"adjacents": 0
			},
			"1-2": {
				"cell_key": "1-2",
				"x": 1,
				"y": 2,
				"clicked": false,
				"mined": true,
				"flag": false,
				"adjacents": 0
			},
            ...
			"5-3": {
				"cell_key": "5-3",
				"x": 5,
				"y": 3,
				"clicked": true,
				"mined": false,
				"flag": false,
				"adjacents": 0
			}
		}
	}
}
```

if game is lost, it will return all the map, including mined cells
```
{
	"code": "ok",
	"message": "Clicked on a mine, game lost",
	"game": {
		"id": "DMDP5",
		"status": "loser",
		"game_map": {
			"2-3": {
				"cell_key": "2-3",
				"x": 2,
				"y": 3,
				"clicked": true,
				"mined": true,
				"flag": false,
				"adjacents": 0
			},
			"1-2": {
				"cell_key": "1-2",
				"x": 1,
				"y": 2,
				"clicked": false,
				"mined": true,
				"flag": false,
				"adjacents": 0
			},
            ...
			"5-3": {
				"cell_key": "5-3",
				"x": 5,
				"y": 3,
				"clicked": true,
				"mined": false,
				"flag": false,
				"adjacents": 0
			}
		}
	}
}
```

Checks and errors
Pressing a cell will fail if:
- Game is not active (winner or looser cases) - (400 - Bad request)
- Cell is out of range (also applies to flag) - (400 - Bad request)
- Cell is already clicked - (400 - Bad request)
- Game is not found (also applies for flag and game status) - (404 - Not found)

#### Game status (for debug)
It will return the game status, including full-map with clicked, flagged and mined cells

`GET /game/:gameId`

Response
```
{
  "game_key": "game.FT98G",
  "id": "FT98G",
  "status": "active",
  "width": 5,
  "height": 5,
  "mines": 5,
  "game_map": {
    "4-2": {
      "cell_key": "4-2",
      "x": 4,
      "y": 2,
      "clicked": false,
      "mined": true,
      "flag": false,
      "adjacents": 0
    },
    ...
    "2-4": {
      "cell_key": "2-4",
      "x": 2,
      "y": 4,
      "clicked": false,
      "mined": true,
      "flag": true,
      "adjacents": 0
    },
    "2-1": {
      "cell_key": "2-1",
      "x": 2,
      "y": 1,
      "clicked": true,
      "mined": false,
      "flag": false,
      "adjacents": 0
    }
  },
  "click_count": 1
}
```


## Public site
#### https://mgonzel-minesweeper.herokuapp.com/health (app health, use root for API URIs)

### TODO

- Swagger (although you can read this documentation)
- Automatic tests (unit or functional)

