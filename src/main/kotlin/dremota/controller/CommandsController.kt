package dremota.controller

import dremota.models.CommandDTO
import dremota.service.CommandsService
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*


object CommandsController {

    suspend fun getAll(call: ApplicationCall) {
        call.respond(CommandsService.getCommands())
    }

    suspend fun getTypes(call: ApplicationCall){
        call.respond(CommandsService.getCommandTypes())
    }

    suspend fun addCommand(call: ApplicationCall){
        val commands = call.receive<CommandDTO>()
        CommandsService.addCommand(commands)
        call.respond(mapOf("status" to "ok"))
    }

    suspend fun updateCommand(call: ApplicationCall) {
        val commands = call.receive<CommandDTO>()

        if (commands.showInMenu && commands.description.isEmpty()) {
            throw BadRequestException("Описание не может быть пустым, если команда будет показана в меню")
        }

        CommandsService.updateCommand(commands)
        call.respond(mapOf("status" to "ok"))
    }

    suspend fun deleteCommand(call: ApplicationCall) {
        val commands = call.receive<CommandDTO>()

            CommandsService.deleteCommand(commands)
            call.respond(mapOf("status" to "ok"))

    }

}