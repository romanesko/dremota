package dremota.controller

import dremota.service.QueryService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object QueriesController {
    suspend fun getAll(call: ApplicationCall) {
        val offset = call.parameters["offset"]?.toLong() ?: 0
        val limit = call.parameters["limit"]?.toLong() ?: 1000
        call.respond(QueryService.getAll(offset,limit))
    }

    suspend fun getForUser(call: RoutingCall) {
        val userId = call.parameters["userId"]?.toLong() ?: 0
        call.respond(QueryService.getForUser(userId))

    }
}