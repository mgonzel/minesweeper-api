package domains

data class Game (
        val id: String,

        // Config info

        // Game info

        // Personal info

        val status: String
) {
    fun validate() : Boolean{

        return true
    }
}