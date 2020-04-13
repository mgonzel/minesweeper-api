package domains

import com.google.gson.annotations.SerializedName
import exceptions.BadRequestException
import exceptions.notCause


data class InputGame (
        val width : Int?,
        val height : Int?,
        val mines : Int?
)

data class Game (
        val id: String,
        private val status: String = constants.game.ACTIVE,

        // Config info
        val width : Int = constants.game.DEFAULT_WIDTH,
        val height : Int = constants.game.DEFAULT_HEIGHT,
        val mines : Int = constants.game.DEFAULT_MINES,

        // Game info
        val gameMap : MutableMap<String, Cell> = mutableMapOf()

        // Personal info

) {
    val gameKey = getGameKey(this.id)

    companion object GameFactory {
        fun getGameKey(gameId: String):String {
            return "game.${gameId}"
        }
    }

    fun contains (cell: Cell) : Boolean {
        return gameMap.containsKey(cell.cellKey)
    }
    fun setCell(cell: Cell) {
        gameMap.put(cell.cellKey, cell)
    }
    fun getCell(cell: Cell) : Cell? {
        return getCell(cell.cellKey)
    }
    fun getCell(idCell: String) : Cell? {
        return gameMap.get(idCell)
    }
    fun isValidCell(cell: Cell) : Boolean {
        return cell.x <= width && cell.y <= height
    }
    fun isActive() : Boolean {
        return constants.game.ACTIVE == this.status
    }
}

data class Cell (
        val x: Int,
        val y: Int,
        val clicked : Boolean = constants.game.FALSE,
        val mined: Boolean = constants.game.FALSE,
        val flag: Boolean = constants.game.FALSE,
        var adyacents: Int = 0
) {
    val cellKey = getCellKey(this.x, this.y)

    companion object CellFactory {
        fun getCellKey(x: Int, y: Int):String {
            return "${x}-${y}"
        }
    }

    fun changeFlag (newFlag : Boolean) : Cell {
        return Cell(x = this.x,
                y = this.y,
                mined = this.mined,
                flag = newFlag,
                adyacents = this.adyacents)
    }
}

data class InputCell (
        val x : Int,
        val y : Int
) {
    val cellKey = Cell.getCellKey(this.x, this.y)

    fun validate() : InputCell {
        if (x <= 0 || y <= 0) {
            throw BadRequestException(message = "Both coordinates are required and must be positive", cause = notCause())
        }
        return this
    }
    fun toCell(clicked: Boolean = constants.game.FALSE,
               flag: Boolean = constants.game.FALSE,
               adyancents: Int = 0) : Cell{
        return Cell(x = this.x, y = this.y,
                clicked = clicked,
                flag = flag,
                adyacents = adyancents,
                mined = constants.game.FALSE)
    }
}
