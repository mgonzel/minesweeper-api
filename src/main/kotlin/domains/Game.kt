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
    fun lost () {
        this.status = constants.game.LOSER
    }
    fun getAdjacency(cell: Cell) : Int {
        val left = if (cell.x > 1) { cell.x -1 } else { 1 }
        val right = if (cell.x < this.width) { cell.x + 1 } else { this.width }
        val top = if (cell.y > 1) { cell.y -1 } else { 1 }
        val bottom = if (cell.y < this.height) { cell.x +1 } else { this.height }

        println("Revisar adyacentes en x=[${left}-${right}]; y=[${top}-${bottom}]")
        var adjacents : Int = 0
        for (x in left..right) {
            for (y in top..bottom){
                if (x != cell.x || y != cell.y){
                    val adjacentCell = getCell(Cell.getCellKey(x, y))
                    println("Buscando celda ${Cell.getCellKey(x,y)}... = ${adjacentCell} ")
                    if (adjacentCell != null){
                        println ("Celda encontrada: ${adjacentCell}")
                    }
                    if (adjacentCell!=null && adjacentCell.mined){
                        "Celda minada encontrada: ${adjacentCell.cellKey}. Sumando"
                        adjacents ++
                    }
                }
            }
        }

        return adjacents
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
