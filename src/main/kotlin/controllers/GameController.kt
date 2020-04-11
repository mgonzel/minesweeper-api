package controllers

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import domains.Game
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

        val mapUser = gsonUs.fromJson(gameParams, HashMap::class.java)
                .plus(mapOf("id" to newId)) as Map<String, String>

        val newGame = gsonUs.fromJson(gsonUs.toJson(mapUser),Game::class.java)

        println("Game body: ${mapUser}")
        res.header(constants.RES_HEADER_NAME_CONTENT_TYPE, constants.RES_HEADER_CONTENT_TYPE_APP_JSON)
        res.status(constants.RES_STATUS_CREATED)

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