package dremota.controller

import dremota.models.SettingsDTO
import dremota.service.SettingsService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object SettingsController {
    suspend fun getAll(call: ApplicationCall) {
        call.respond(SettingsService.getSettings())
    }
    suspend fun updateSettings(call: ApplicationCall) {
        val settings = call.receive<SettingsDTO>()
        SettingsService.updateSettings(settings)
        call.respond(mapOf("status" to "ok"))
    }
}