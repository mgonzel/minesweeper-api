package domains

import com.google.gson.annotations.SerializedName


data class InputGame (
        val width : Int?,
        val height : Int?,
        val mines : Int?
)

data class Game (
        val id: String,
        @SerializedName("status") private val _status: String? = "active",

        // Config info
        val width : Int = constants.game.DEFAULT_WIDTH,
        val height : Int = constants.game.DEFAULT_HEIGHT,
        val mines : Int = constants.game.DEFAULT_MINES,

        // Game info
        val gameMap : MutableMap<String, Cell> = mutableMapOf()

        // Personal info

)

data class Cell (
        val x: Int,
        val y: Int,
        val clicked : Boolean = false,
        val mined: Boolean = false,
        val flag: Boolean = false
) {
    val cellKey = "${this.x}-${this.y}"
}