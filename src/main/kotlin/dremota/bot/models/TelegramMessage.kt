package dremota.bot.models

data class TelegramMessage(
    val text: String,
    val images: List<String>? = null
)