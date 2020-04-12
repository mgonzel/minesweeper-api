package services

import domains.Cell
import domains.Game
import domains.InputGame

class GameService (
    val utilsService : UtilsService = UtilsService(),
    val redisService : RedisService = RedisService(),
    val gsonService : GsonService = GsonService()
) {

    fun gameStats () : String? {

        return ""
    }

    private fun addRandomMine(game: Game){
        var added = false
        while (!added) {
            val cell = Cell(x = utilsService.getRandomInt(game.width),
                    y = utilsService.getRandomInt(game.height),
                    mined = constants.game.TRUE)

            if (!game.gameMap.containsKey(cell.cellKey)){
                game.gameMap.put(cell.cellKey, cell)
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
            redisService.setValue(game.gameKey, gsonService.gson.toJson(game))

            game
        }
        return game
    }

    fun updateGame() {

    }
}