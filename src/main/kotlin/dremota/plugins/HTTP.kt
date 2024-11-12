package dremota.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json


fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true

        })
    }

    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    message = "Unauthorized access"
                )
            )
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    message = "Not found"
                )
            )
        }
        exception<Throwable> { call, cause ->
            when (cause) {
                is IllegalArgumentException,
                is NoSuchElementException,
                is BadRequestException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = getErrorMessage(cause), status = 400)
                    )
                }


                else -> {
                    logger.error("Internal server error", cause)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "An internal error occurred", status = 500)
                    )
                }
            }
        }
    }

}
