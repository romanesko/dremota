package dremota.controller

import dremota.models.AuthCodeDTO
import dremota.service.AuthService
import dremota.service.UsersService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class TgAuthRequest(
    val id: Long,
    val username: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("auth_date")
    val authDate: Long,
    val hash: String
)


@Serializable
data class AuthState(val token: String?)


object AuthController {

    suspend fun auth(call: ApplicationCall) {

        val req = call.receive<TgAuthRequest>()

        if (!AuthService.validateHash(req)) {
            call.respondText("Invalid hash", status = HttpStatusCode.Unauthorized)
            return
        }

        val user = UsersService.getUser(req.id)?: throw BadRequestException("User not found")

        if (!AuthService.isAdmin(user.chatId)){
            call.respondText("Not admin", status = HttpStatusCode.Unauthorized)
            return
        }
        val token = AuthService.updateToken(user)

        call.response.cookies.append(
            name = "token",
            value = token,
            maxAge = 60 * 60 * 24 * 3, // Cookie expires in 2 day
            path = "/",
            httpOnly = false,
            secure = false
        )

        call.respond(mapOf("token" to token))
    }

   suspend fun generateCode(call: RoutingCall) {
        call.respond(AuthService.generateCode())
    }

    suspend fun checkUserLoggedIn(call: RoutingCall) {
        call.respond(AuthService.checkUserLoggedIn(call.receive<AuthCodeDTO>()))
    }
}


