package domains

import exceptions.BadRequestException
import exceptions.notCause


data class InputGame (
        val width : Int?,
        val height : Int?,
        val mines : Int?
)

data class Game (
        val id: String,
        private var status: String = constants.game.ACTIVE,

        // Config info
        val width : Int = constants.game.DEFAULT_WIDTH,
        val height : Int = constants.game.DEFAULT_HEIGHT,
        val mines : Int = constants.game.DEFAULT_MINES,

        // Game info
        val gameMap : MutableMap<String, Cell> = mutableMapOf(),
        var clickCount : Int = 0

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
        if (cell.clicked) {
            val actualCell = gameMap.get(cell.cellKey)
            if (actualCell == null || !actualCell.clicked) {
                this.clickCount++
            }
        }
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
    fun lost () {
        this.status = constants.game.LOSER
    }
    fun getStatus() : String {
        return this.status
    }

    fun getAdjacency(cell: Cell) : Int {
        val left = if (cell.x > 1) { cell.x -1 } else { 1 }
        val right = if (cell.x < this.width) { cell.x + 1 } else { this.width }
        val top = if (cell.y > 1) { cell.y -1 } else { 1 }
        val bottom = if (cell.y < this.height) { cell.y +1 } else { this.height }

        var adjacents : Int = 0
        for (x in left..right) {
            for (y in top..bottom){
                if (x != cell.x || y != cell.y){
                    val adjacentCell = getCell(Cell.getCellKey(x, y))

                    if (adjacentCell!=null && adjacentCell.mined){
                        adjacents ++
                    }
                }
            }
        }

        return adjacents
    }
    fun getAdjacentCells (cell: Cell) : MutableMap<String, Cell> {
        val left = if (cell.x > 1) { cell.x -1 } else { 1 }
        val right = if (cell.x < this.width) { cell.x + 1 } else { this.width }
        val top = if (cell.y > 1) { cell.y -1 } else { 1 }
        val bottom = if (cell.y < this.height) { cell.y +1 } else { this.height }

        val adjacents = mutableMapOf<String, Cell>()
        for (x in left..right) {
            for (y in top..bottom){
                if (x != cell.x || y != cell.y){
                    val adjacentKey = Cell.getCellKey(x, y)
                    val adjacentCell = getCell(adjacentKey)

                    if (adjacentCell!=null){
                        adjacents.put(adjacentCell.cellKey, adjacentCell)
                    } else {
                        adjacents.put(adjacentKey, Cell(x, y))
                    }
                }
            }
        }

        return adjacents

    }

    fun checkAndUpdateStatus() {
        val totalCells = this.clickCount + this.mines
        val matrixSize : Int = this.height * this.width

        if (totalCells >= matrixSize){
            this.status = constants.game.WINNER
        }
    }
}

data class Cell (
        val x: Int,
        val y: Int,
        val clicked : Boolean = constants.game.FALSE,
        val mined: Boolean = constants.game.FALSE,
        val flag: Boolean = constants.game.FALSE,
        var adjacents: Int = 0
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
                adjacents = this.adjacents)
    }
    fun click () : Cell {
        val cell = Cell(x = this.x,
                y = this.y,
                mined = this.mined,
                flag = this.flag,
                clicked = constants.game.TRUE,
                adjacents = this.adjacents)

        return cell
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
                adjacents = adyancents,
                mined = constants.game.FALSE)
    }
}

data class GameResponse(
        val code : String,
        val message : String,
        val game : GameStatus
)
data class GameStatus (
        val id : String,
        val status : String,
        val gameMap : Map<String, Cell>?
)
