package dremota.plugins

import dremota.controller.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.io.File


@Serializable
data class ErrorResponse(val message: String, val status: Int)

var logger = LoggerFactory.getLogger("app")

fun getErrorMessage(cause: Throwable): String {
    return when (cause.cause) {
        is JsonConvertException -> cause.cause?.localizedMessage ?: "Invalid JSON format"
        else -> cause.localizedMessage ?: "Invalid request"
    }
}

fun Application.configureRouting() {


    routing {
        options("*") {
            call.respond(HttpStatusCode.OK)
        }
        staticResources("/", "static")
        staticFiles("/images", File("src/main/resources/images"))
//        staticResources("/images", "images")

        post("/tgauth") { AuthController.auth(call) }
        authenticate("auth-bearer") {
            route("/api") {
                get("/status") { call.respond(mapOf("status" to "OK")) }
                route("/users") {
                    get { UsersController.getAll(call) }
                    post("/users/update") { UsersController.update(call) }
                    get("/me") { UsersController.getMe(call) }
                    get("/{userId}") { UsersController.getUser(call) }
                    get("/{userId}/info") { UsersController.getUserInfo(call) }
                }
                route("/queries") {
                    get { QueriesController.getAll(call) }
                    route("/{userId}") {
                        get { QueriesController.getForUser( call) }
                    }
                }
                route("/payments") {
                    route("/{userId}") {
                        get { PaymentsController.getPayments( call) }
                    }
                }
                route("/balance") {
                    route("/{userId}") {
                        get { BalanceController.getPayments( call) }
                        post { BalanceController.create(call) }
                    }
                }
                route("/settings") {
                    get { SettingsController.getAll(call) }
                    post { SettingsController.updateSettings(call) }
                    route("/commands") {
                        get { CommandsController.getAll(call) }
                        put { CommandsController.updateCommand(call) }
                        post { CommandsController.addCommand(call) }

                        get("/types") {
                            CommandsController.getTypes(call)
                        }
                        post("/delete") { CommandsController.deleteCommand(call) }
                    }
                    route("/price") {
                        get { PriceController.getAll(call) }
                        post { PriceController.update(call) }
                        post("/delete") { PriceController.delete(call) }
                    }
                    route("/referral") {
                        get { ReferralController.getSettings(call) }
                        post { ReferralController.updateSettings(call) }
                    }
                }

                route("/feedback") {
                    get { FeedbackController.getAllFeedback(call) }
                    get("/{userId}") { FeedbackController.getAllForUser(call) }
                    post("/read") { FeedbackController.markAsRead(call) }
                    get("/unread") { FeedbackController.hasUnreadFeedback(call) }
                }
                route("messages") {
                    post { MessagesController.add(call) }
                    get("/{userId}") { MessagesController.getAllForUser(call) }
                }

            }
        }

    }
}
