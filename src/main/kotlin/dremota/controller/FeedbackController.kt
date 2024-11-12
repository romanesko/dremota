package dremota.controller

import dremota.service.FeedbackService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackMarkAsReadRequest(val id: Long)


object FeedbackController {

    suspend fun getAllFeedback(call: ApplicationCall) {
        val items = FeedbackService.getAllFeedback()
        call.respond(items)
    }

    suspend fun getAllForUser(call: ApplicationCall) {
        val userId = call.parameters["userId"]!!.toLong()
        call.respond(FeedbackService.getAllForUser(userId))
    }

    suspend fun markAsRead(call: ApplicationCall) {
        val req = call.receive<FeedbackMarkAsReadRequest>()
        FeedbackService.markAsRead(req.id)
        call.respond(mapOf("status" to "ok"))
    }

    suspend fun hasUnreadFeedback(call: ApplicationCall) {
        call.respond(FeedbackService.hasUnreadFeedback())
    }

}