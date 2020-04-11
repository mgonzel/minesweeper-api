package controllers

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import domains.Game
import domains.InputGame
import services.GameService
import services.UtilsService
import spark.Request
import spark.Response

private const val GAME_NAME_LENGHT: Int = 5

class GameController(){
    val utilsService = UtilsService()
    val gameService = GameService()

    val gsonUs : Gson= GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    val createGame = { req: Request, res: Response ->
        val newId = utilsService.getRandomName(GAME_NAME_LENGHT).toUpperCase()

        val gameParams = req.body()

        val gameRequest = gsonUs.fromJson(gameParams, InputGame::class.java)

        val newGame = gameService.createGame(newId, gameRequest)

        println("Game body: ${gameRequest} - Game: ${newGame}")
        res.header(constants.http.RES_HEADER_NAME_CONTENT_TYPE, constants.http.RES_HEADER_CONTENT_TYPE_APP_JSON)
        res.status(constants.http.RES_STATUS_CREATED)

        """{"status":"created","game_id": "${newId}"}"""

    }

    val getGameStats = { req: Request, res: Response ->
        gameService.gameStats()
    }

    val press = { req: Request, res: Response ->

    }

    val addFlag = { req: Request, res: Response ->

    }

    val removeFlag = { req: Request, res: Response ->

    }

}