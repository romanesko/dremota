package dremota.controller

import dremota.models.ReferralDTO
import dremota.plugins.ErrorResponse
import dremota.service.ReferralService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*


object ReferralController {

    suspend fun getSettings(call: ApplicationCall) {
        call.respond(ReferralService.getSettings())
    }

    suspend fun updateSettings(call: ApplicationCall) {
        val settings = call.receive<ReferralDTO>()

        if (settings.referrerReceives <0 || settings.holderReceives <0) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(message = "Количество дней не может быть отрицательным", status = 400)
            )
            return
        }

        ReferralService.updateSettings(settings)
        call.respond(mapOf("status" to "ok"))
    }
}