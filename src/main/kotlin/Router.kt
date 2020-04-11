import spark.Request
import spark.Response
import spark.Spark.*
import spark.servlet.SparkApplication
import controllers.*

val gameController = GameController()

class Router : SparkApplication {
    override fun init() {
        port(8080)

        routes()

        appContextRoutes()
    }

    fun appContextRoutes(){
        path("/api/minesweeper-api/v1"){
            routes()
        }
    }

    fun routes() {
        get("/health") { req : Request , res: Response ->
            res.status(constants.RES_STATUS_OK)
            res.header(constants.RES_HEADER_NAME_CONTENT_TYPE, constants.RES_HEADER_CONTENT_TYPE_APP_JSON)
            "Server is ON"
        }
        path("/game"){
            post("", gameController.createGame)
            get("/:gameId", gameController.getGameStats)
            post("/:gameId/press", gameController.press)
            post("/:gameId/flag", gameController.addFlag)
            delete("/:gameId/flag", gameController.removeFlag)
        }
    }
}