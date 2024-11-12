package dremota.controller

import dremota.service.PaymentsService
import io.ktor.server.application.*
import io.ktor.server.response.*


object PaymentsController {

    suspend fun getPayments( call: ApplicationCall) {
        val userId= call.parameters["userId"]!!.toLong()
        call.respond(PaymentsService.getAllForUser(userId))
    }


}