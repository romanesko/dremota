package dremota.bot.models


data class GeneralResponse(
    var telegramMessages: List<TelegramMessage>? = null,
    var buttonsHeader: String? = null,
    var buttons: List<Button>? = null,
    var buttonsRows: List<Int>? = null,
    var context: String
)

