package controllers

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import domains.Game
import domains.InputCell
import domains.InputGame
import services.GameService
import services.GsonService
import services.UtilsService
import spark.Request
import spark.Response

private const val GAME_NAME_LENGHT: Int = 5

private val logger = mu.KotlinLogging.logger {}


class GameController(){
    val utilsService = UtilsService()
    val gameService = GameService()

    val gsonService : GsonService = GsonService()

    val createGame = { req: Request, res: Response ->
        val newId = utilsService.getRandomName(GAME_NAME_LENGHT).toUpperCase()

        val gameParams = req.body()

        val gameRequest = gsonService.gson.fromJson(gameParams, InputGame::class.java)

        val newGame = gameService.createGame(newId, gameRequest)

        println("Game body: ${gameRequest} - Game: ${newGame}")
        res.header(constants.http.HEADER_NAME_CONTENT_TYPE, constants.http.HEADER_CONTENT_TYPE_APP_JSON)
        res.status(constants.http.RES_STATUS_CREATED)

        """{"status":"created","game_id": "${newId}"}"""

    }

    val getGameStats = { req: Request, res: Response ->
        gameService.gameStats()
    }

    val press = { req: Request, res: Response ->

    }

    val setFlag = { req: Request, res: Response, newFlag: Boolean ->
        val gameId = req.params("gameId")
        val inputCell = gsonService.gson.fromJson(req.body(), InputCell::class.java).validate()
        logger.info ( "Input Cell selected for flag: ${inputCell}")

        gameService.flagCell(gameId, inputCell, newFlag)

        res.header(constants.http.HEADER_NAME_CACHE_CONTROL,constants.http.HEADER_CONTENT_TYPE_APP_JSON)
        res.status(constants.http.RES_STATUS_OK)
        """{"status":"OK", "message":"updated"}"""
    }

}