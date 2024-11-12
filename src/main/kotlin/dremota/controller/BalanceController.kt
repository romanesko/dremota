package dremota.controller

import dremota.service.BalanceService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class BalanceCreateRequest(
    val days: Long,
    val rationale: String,
)

object BalanceController {

    suspend fun getPayments( call: ApplicationCall) {
        val userId= call.parameters["userId"]!!.toLong()
        call.respond(BalanceService.getAllForUser(userId))
    }

    suspend fun create(call: RoutingCall) {
        val userId= call.parameters["userId"]!!.toLong()
        val request = call.receive<BalanceCreateRequest>()
        BalanceService.create(userId,request.days, request.rationale, payment = null)
        call.respond(mapOf("status" to "OK"))
    }


}