import spark.Request
import spark.Response
import spark.Spark.*
import spark.servlet.SparkApplication
import controllers.*
import exceptions.HttpException

private val logger = mu.KotlinLogging.logger {}

class Router : SparkApplication {
    val gameController = GameController()

    fun getHerokuAssignedPort() : Int {
        val processBuilder = ProcessBuilder()
        try {
            if (processBuilder.environment().get("PORT") != null) {
                return Integer.parseInt(processBuilder.environment().get("PORT"));
            }
        } catch (e: Exception) {
            logger.error ("Error trying to get port from Heroku")
        }
        return 8080; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    override fun init() {
        port(getHerokuAssignedPort())

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
            res.status(constants.http.RES_STATUS_OK)
            res.header(constants.http.HEADER_NAME_CONTENT_TYPE, constants.http.HEADER_CONTENT_TYPE_APP_JSON)
            """{"status": "${constants.game.OK}", "message": "Server is ON"}"""
        }
        path("/game"){
            post("", gameController.createGame)
            get("/:gameId", gameController.getGameStats)
            post("/:gameId/press", gameController.press)
            post("/:gameId/flag", { req: Request, res: Response ->
                gameController.setFlag(req, res, constants.game.TRUE) }
            )
            delete("/:gameId/flag", { req: Request, res: Response ->
                gameController.setFlag(req, res, constants.game.FALSE) }
            )
        }

        // Error control
        notFound({_, res ->
            logger.info ("Exception: NOT_FOUND")
            res.type("application/json");
            res.status (constants.http.RES_STATUS_NOT_FOUND)
            res.header(constants.http.HEADER_NAME_CACHE_CONTROL, constants.http.HEADER_NOT_FOUND_CACHE) //
            "{\"message\":\"The resource you requested was not found\"}";
        });
        internalServerError({_, res ->
            logger.info ("Exception: INTERNAL_ERROR")
            res.type(constants.http.HEADER_CONTENT_TYPE_APP_JSON)
            res.status (constants.http.RES_STATUS_INTERNAL_ERROR)
            "{\"message\":\"An Internal Server Error has ocurred (500).\"}";
        });
        exception (Exception::class.java, { e, _, res ->
            logger.info ("Exception: GENERIC EXCEPTION", e)
            res.type(constants.http.HEADER_CONTENT_TYPE_APP_JSON)
            res.status (constants.http.RES_STATUS_INTERNAL_ERROR)
            res.body("{\"message\":\"Hubo una excepcion debido a una acción inesperada\"}");
            //NewRelic.noticeError(e)

        })
        exception (HttpException::class.java, { e, _, res ->
            logger.info ("Exception: HTTP_EXCEPTION", e)
            res.type(constants.http.HEADER_CONTENT_TYPE_APP_JSON)
            res.status (e.status)
            res.body("{\"message\":\"${e.message}\"}");
            //NewRelic.noticeError(e)
        })
    }
}