package dremota.service


import dremota.Command
import dremota.lib.BotCommandsChangedEvent
import dremota.lib.EventManager
import dremota.lib.dbBool
import dremota.lib.fromDbBool
import dremota.models.CommandDTO
import dremota.models.CommandType
import dremota.models.toDTO
import dremota.plugins.Db
import io.ktor.server.plugins.*


object CommandsService {

    init {

//        Db.commandsQueries.deleteCommand("balance")

        val added =
            addSystemCommand("start", "Команда запуска", CommandType.START, "Добро пожаловать!\nЗдесь вы можете заказывать толкования снов.", "") +
            addSystemCommand("feedback", "Обратная связь", CommandType.FEEDBACK, "Напишите свой отзыв в сообщении ниже", "Cпасибо за обратную связь, мы обязательно примем во внимание") +
            addSystemCommand("pay", "Пополнить баланс", CommandType.PAY, "Выберите один из вариантов", "Спасибо за ваш платёж!\nУслуга оплачена до {{ paid_until }}")

        try {
            addCommand(
                CommandDTO(
                    key = "balance",
                    description = "Узнать свой баланс",
                    type = CommandType.MESSAGE,
                    message = "{{#if paid_until}}\nУслуга оплачена до {{ paid_until }}\n{{else}}\nУслуга не оплачена\n{{/if}}\nПополните баланс: /pay\nПоделитесь ссылкой на бота: {{ referral }} и получите персональный бонус за нового участника",
                    postMessage = "",
                    showInMenu = true,
                    enabled = true,
                    system = false
                )
            )
        } catch (_: Exception) {

        }



        if (added>0) {
            EventManager.notify(BotCommandsChangedEvent(""))
        }
    }


    private fun addSystemCommand(
        key: String,
        description: String,
        type: CommandType,
        message: String,
        postMessage: String,

    ): Int {
        if (Db.commandsQueries.getCommand(key).executeAsOneOrNull() != null) {
            return 0
        }
        Db.commandsQueries.insertIfNotExists(key, description, type.name, message,  postMessage, 1L, 1L, 1L)
        return 1

    }

    fun getCommands(): List<CommandDTO> {
        return Db.commandsQueries.selectCommands().executeAsList().map(Command::toDTO)
    }

    fun getCommandTypes(): Map<String, String> {
        return CommandType.entries.associate { it.name to it.description }
    }

    fun updateCommand(command: CommandDTO) {
        Db.commandsQueries.getCommand(command.key).executeAsOneOrNull()
            ?: throw NotFoundException("Команда не найдена")
        command.apply {
            Db.commandsQueries.update(description, message, dbBool(showInMenu), dbBool(enabled), key)
        }
        EventManager.notify(BotCommandsChangedEvent(command.key))
    }

    fun addCommand(command: CommandDTO) {
        if (Db.commandsQueries.getCommand(command.key).executeAsOneOrNull() != null) {
            throw BadRequestException("Команда с таким именем уже существует")
        }

        command.apply {
            Db.commandsQueries.insertIfNotExists(
                key, description, CommandType.MESSAGE.name, message,postMessage, dbBool(showInMenu), dbBool(enabled),
                dbBool(false)
            )
        }
        EventManager.notify(BotCommandsChangedEvent(command.key))
    }


    fun deleteCommand(command: CommandDTO) {
        val storedCommand =
            Db.commandsQueries.getCommand(command.key).executeAsOneOrNull() ?: throw Exception("Command not found")

        if (fromDbBool(storedCommand.system)) {
            throw BadRequestException("Can't delete system command")
        }
        Db.commandsQueries.deleteCommand(command.key)
        EventManager.notify(BotCommandsChangedEvent(command.key))
    }

    fun selectActiveVisibleCommands(): List<CommandDTO> {
        return Db.commandsQueries.selectActiveVisibleCommands().executeAsList().map(Command::toDTO)
    }

    fun selectActiveCommands() : List<CommandDTO> {
        return Db.commandsQueries.selectActiveCommands().executeAsList().map(Command::toDTO)
    }

}


