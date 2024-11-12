package dremota.controller

import dremota.models.MessageDTO
import dremota.service.MessagesService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object MessagesController {

    suspend fun add(call: ApplicationCall) {
        val body = call.receive<MessageDTO>()

        if (body.message.isEmpty()) {
            throw BadRequestException("Message is empty")
        }

        MessagesService.add(body)
        call.respond(mapOf("status" to "ok"))


    }

    suspend fun getAllForUser(call: ApplicationCall) {
        val items = MessagesService.getAllForUser(call.parameters["userId"]!!.toLong())
        call.respond(items)
    }
}