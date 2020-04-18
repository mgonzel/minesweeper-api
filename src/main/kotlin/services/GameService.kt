package services

import domains.Cell
import domains.Game
import domains.InputCell
import domains.InputGame
import exceptions.BadRequestException
import exceptions.NotFoundException
import exceptions.notCause

class GameService (
    val utilsService : UtilsService = UtilsService(),
    val redisService : RedisService = RedisService(),
    val gsonService : GsonService = GsonService()
) {

    fun gameStats (gameId: String) : String? {
        val game = getActiveGame(gameId)
        return gsonService.gson.toJson(game)
    }

    private fun addRandomMine(game: Game){
        var added = false
        while (!added) {
            val cell = Cell(x = utilsService.getRandomInt(game.width),
                    y = utilsService.getRandomInt(game.height),
                    mined = constants.game.TRUE)

            if (!game.contains(cell)){ // .gameMap.containsKey(cell.cellKey)){
                game.setCell(cell)
                added = true
            }
        }
    }
    private fun initializeMines(game: Game) : Game {
        for (x in 1..game.mines) {
            addRandomMine(game)
        }
        return game
    }

    fun createGame(id: String, gameRequest: InputGame ) : Game {
        val game = gameRequest.let {
            Game(id = id,
                    width = it.width ?: constants.game.DEFAULT_WIDTH,
                    height = it.height ?: constants.game.DEFAULT_HEIGHT,
                    mines = it.mines ?: constants.game.DEFAULT_MINES)
        }.let { game: Game ->

            initializeMines(game)
        }.let {game : Game ->
            saveOrUpdateGame(game)

            game
        }
        return game
    }

    private fun getGame(id: String) : Game {
        val game = Game.getGameKey(id).let { gameKey ->
            redisService.getValue(gameKey)
        }?.let {jsonData ->
            gsonService.gson.fromJson(jsonData, Game::class.java)
        }

        return game ?: throw NotFoundException( message = "Game not found. Id = ${id}", cause = notCause() )
    }

    private fun getActiveGame(id: String) : Game {
        val game = getGame(id)
        if (!game.isActive()){
            throw BadRequestException(message = "Game is not active any more or has ended", cause = notCause())
        }
        return game
    }

    private fun saveOrUpdateGame(game: Game) {
        redisService.setValue(game.gameKey, gsonService.gson.toJson(game))
    }

    fun flagCell(gameId: String, inputCell: InputCell, newFlag: Boolean): String {
        val game = getActiveGame(gameId)

        val newCell = inputCell.toCell(flag = newFlag)
        if (!game.isValidCell(newCell)){
            throw BadRequestException(message = "Cell is out of bounds", cause = notCause())
        }
        if (game.contains(newCell)){
            val actualCell = game.getCell(newCell)
            if (actualCell!!.clicked) {
                throw BadRequestException(message = "Cell is already clicked", cause = notCause())
            } else {
                val updatedCell = actualCell.changeFlag(newFlag)
                game.setCell(updatedCell)
                saveOrUpdateGame(game)
                return gsonService.gson.toJson(updatedCell)
            }
        } else {
            game.setCell(newCell)
            saveOrUpdateGame(game)
            return gsonService.gson.toJson(newCell)
        }
    }

    fun pressCell(gameId: String, inputCell: InputCell) : String {
        val game = getActiveGame(gameId)

        val newCell = inputCell.toCell(clicked = constants.game.TRUE)

        val gameHasCell = game.contains(newCell)

        val workingCell = if (gameHasCell) { game.getCell(newCell) as Cell } else { newCell }

        if (gameHasCell && workingCell.clicked){
            throw BadRequestException(message = "Cell is already clicked", cause = notCause())
        } else if (gameHasCell && workingCell.mined) { //Game over
            val clickedCell = workingCell.click()
            game.setCell(clickedCell)
            game.lost()
            saveOrUpdateGame(game)

            return gsonService.gson.toJson(clickedCell)
        } else {
            val clickedCell = workingCell.click()
            clickedCell.adjacents = game.getAdjacency(clickedCell)

            game.setCell(clickedCell)
            // saveOrUpdateGame(game)
            return gsonService.gson.toJson(clickedCell)
        }
    }
}