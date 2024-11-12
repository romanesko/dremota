package dremota.controller

import dremota.models.PriceDTO
import dremota.service.PriceService

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object PriceController {

    suspend fun getAll(call: ApplicationCall) {
        call.respond(PriceService.getAll())
    }

    suspend fun update(call: ApplicationCall) {
        PriceService.upsert(call.receive<PriceDTO>())
        call.respond(mapOf("status" to "ok"))
    }

    suspend fun delete(call: ApplicationCall) {
        PriceService.delete(call.receive<PriceDTO>())
        call.respond(mapOf("status" to "ok"))
    }
}