package dremota.controller

import dremota.models.AuthDTO
import dremota.service.UsersService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val chatId: Long
)


object UsersController {

    suspend fun getAll(call: ApplicationCall) {
        call.respond(UsersService.getAll())
    }

    suspend fun update(call: ApplicationCall) {
        val request = call.receive<UserUpdateRequest>()
        UsersService.updateLastVisit(request.chatId)
        call.respondText("ok")
    }

    suspend fun getMe(call: ApplicationCall) {
        // TODO: get user by token
        val principal = call.principal<AuthDTO>()
        if (principal == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return
        }

        call.respond(principal.user)
    }

    suspend fun getUser(call: ApplicationCall) {
        call.parameters["userId"]?.toLongOrNull()
            ?.let { userId -> UsersService.getUser(userId) }
            ?.let { user -> call.respond(user) }
            ?: call.respond(HttpStatusCode.NotFound)
    }


    suspend fun getUserInfo(call: ApplicationCall) {
        call.parameters["userId"]?.toLongOrNull()
            ?.let { userId -> UsersService.getUserInfo(userId) }
            ?.let { user -> call.respond(user) }
            ?: call.respond(HttpStatusCode.NotFound)
    }

}