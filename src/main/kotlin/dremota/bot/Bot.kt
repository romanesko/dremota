package dremota.bot

import dremota.api.models.ApiRequest
import dremota.bot.models.GeneralResponse
import dremota.lib.Cache
import dremota.lib.Storage
import dremota.lib.createTaskScope
import dremota.models.CommandDTO
import dremota.models.CommandType
import dremota.models.PriceDTO
import dremota.models.UserDTO
import dremota.plugins.api
import dremota.service.BotService
import dremota.service.FeedbackService
import dremota.service.PaymentInfo
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment
import dremota.bot.models.TelegramMessage as ResponseMessage

val settingsMap = mapOf(
    "welcome_message" to "приветственное сообщение",
    "context" to "контекст",
    "prefix" to "префикс",
    "post" to "пост-сообщение",
)


class Bot(val client: Client, private val adminPassword: String) : LongPollingSingleThreadUpdateConsumer {
    private val logger: Logger = LoggerFactory.getLogger("bot")


    private suspend fun asyncConsume(update: Update) {
        when {
            update.hasMessage() -> {
                val user = BotService.checkUser(update.message)
                when {
                    update.message.hasSuccessfulPayment() -> {

                        processSuccessfulPayment(
                            user,
                            update.message.successfulPayment

                        )
                    }

                    update.message.text.startsWith("/") -> processCommand(user, update.message)
                    else -> {
                        processText(user, update.message)
                    }
                }
            }

            update.hasCallbackQuery() -> onButtonClick(update.callbackQuery)
            update.hasPreCheckoutQuery() -> onPreCheckoutQuery(update.preCheckoutQuery)
            else -> {
                logger.error("No message nad no callback query")
                client.sendMessage(update.message.chatId, "No message nad no callback query")
            }
        }
    }


    val taskScope = createTaskScope()

    override fun consume(update: Update?) {
        if (update == null) {
            logger.error("update is null")
            return
        }
        taskScope.launch {
            asyncConsume(update)
        }
    }

    private fun processCommand(user: UserDTO, message: Message) {
        BotService.setContext(user.chatId, null)

        val cmdKey = message.text.substringAfter("/").substringBefore(" ").trim().trim()

        Cache.commands[cmdKey]?.let {

            if (it.message.isNotEmpty()) {
                client.sendMessage(user, it.message)
            }

            when (it.type) {
                CommandType.FEEDBACK -> processCommandFeedback(user, message)
                CommandType.PAY -> processCommandPay(user, message)
                else -> {}
            }
            return
        }

        client.sendMessage(user.chatId, "Неизвестная команда")
    }

    fun processCommandStart(user: UserDTO, command: CommandDTO) {
        if (command.message.isNotEmpty()) {
            client.sendMessage(user.chatId, command.message)
        }
    }

    private fun processCommandFeedback(user: UserDTO, message: Message) {
        BotService.setContext(user.chatId, "feedback")
    }


    private fun onButtonClick(callbackQuery: CallbackQuery) {
        val chatId = callbackQuery.message.chat.id;
        val messageId = callbackQuery.message.messageId;

        val context = BotService.getContext(chatId)

        if (context == null) {
            client.sendMessage(chatId, "Не пониманию о чём вы (")
            return
        }

        val button = BotService.getButtonAction(chatId, callbackQuery.data)
        if (button == null) {
            logger.error("No data for callback query")
            client.sendMessage(chatId, "No data for callback query")
            return
        }

        client.deleteMessage(chatId, messageId)

        when (context) {
            "settings:set" -> {
                if (button.data == "cancel") {
                    BotService.setContext(chatId, null)
                    client.sendMessage(chatId, "Вы вернулись в режим чата")
                    return
                } else {
                    client.sendGeneralResponse(
                        chatId,
                        GeneralResponse(
                            telegramMessages = listOf(ResponseMessage("Задайте ${settingsMap[button.data]}")),
                            context = "settings:set:${button.data}"
                        )
                    )
                }
            }

//            "history" -> {
//                downloadHistory(chatId, button.data)
//            }

            else -> {
                client.sendMessage(chatId, "Неизвестный контекст")
            }
        }

    }


    private fun onPreCheckoutQuery(preCheckoutQuery: PreCheckoutQuery) {
        client.telegramClient.execute(AnswerPreCheckoutQuery(preCheckoutQuery.id, true))
    }

    private fun processSuccessfulPayment(user: UserDTO, paymentInfo: SuccessfulPayment) {
        try {
            val paymentId = BotService.setPaymentInfo(
                PaymentInfo(
                    user.chatId,
                    paymentInfo.currency,
                    paymentInfo.totalAmount,
                    paymentInfo.invoicePayload,
                    paymentInfo.providerPaymentChargeId,
                    paymentInfo.telegramPaymentChargeId
                )
            )

            paymentInfo.invoicePayload?.let { payload ->

                val priceDTO: PriceDTO = Storage.get("price:$payload")?.let { storedPayload ->
                    Json.decodeFromString(storedPayload)
                } ?: run {
                    val (id, amount, currency, days) = payload.split(":")
                    PriceDTO(
                        id = id.toLong(),
                        title = "???",
                        amount = amount.toLong(),
                        currency = currency,
                        days = days.toLong(),
                        enabled = true,
                        createdAt = null
                    )
                }

                BotService.addBalance(user, priceDTO, paymentId)
            }

            Cache.commands["payment"]?.let { cmd ->
                if (cmd.postMessage.isNotEmpty()) {
                    client.sendMessage(user, cmd.postMessage)
                }
            }

        } catch (e: Exception) {
            logger.error("Error while processing successful payment", e)
            client.sendErrorMessage(user.chatId, "Ошибка при обработке платежа")
        }
    }

    private suspend fun processText(user: UserDTO, message: Message) {
        val context = BotService.getContext(user.chatId)

        if (context == null) {
            if (BotService.getSettings("context") == null) {
                client.sendMessage(user.chatId, "Бот не настроен, дождитесь настройки администратором")
                return
            }
            if (!user.activePayment) {
                BotService.getSettings("empty_balance_message")?.let { client.sendMessage(user.chatId, it) }
                return
            }
            processAiRequest(user.chatId, message.text)
            return
        }

        if (context == "feedback") {
            FeedbackService.saveFeedback(user.chatId, message.text)

            Cache.commands["feedback"]?.let { cmd ->
                if (cmd.postMessage.isNotEmpty()) {
                    client.sendMessage(user.chatId, cmd.postMessage)
                }
            }
            BotService.setContext(user.chatId, null)
            return
        }

//        if (context.startsWith("settings:set")) {
//            val key = context.substringAfter("settings:set:").trim()
//            if (key == "settings:set") {
//                bot.sendMessage(chatId, "Не указано название настройки")
//                return
//            }
//            var text = message.text.trim()
//            if (text == "-") {
//                storage.setSettings(key, null)
//                bot.sendMessage(chatId, "Настройка удалена")
//            } else {
//                storage.setSettings(key, text)
//                bot.sendMessage(chatId, "Настройка сохранена")
//            }
//            storage.setContext(chatId, null)
//            processCommandSettings(chatId)
//            return
//        }
        client.sendMessage(user.chatId, "Неизвестная команда")


    }

    private suspend fun processAiRequest(chatId: Long, message: String) {
        client.sendTyping(chatId)
        val context = BotService.getSettings("context")

        if (context == null) {
            client.sendMessage(chatId, "Бот не настроен, дождитесь настройки администратором")
            return
        }

        val prefix = BotService.getSettings("prefix") ?: ""



        try {
            val reqId = BotService.saveRequest(chatId, context, prefix, message)
            val res = api.request(ApiRequest(context, "$prefix $message"))

            logger.info("→ $message")
            logger.info("← ${res.response}\n")

            val postfix = BotService.getSettings("post")?.let { "\n\n$it" } ?: ""

            val output = "${res.response}$postfix"
            client.sendMessage(chatId, output)

            BotService.saveResponse(reqId, output)


        } catch (e: Exception) {
            logger.error("Error while processing request", e)
            client.sendErrorMessage(chatId, e.message ?: "Ошибка при обработке запроса");
        }

    }


    fun processCommandPay(user: UserDTO, message: Message) {
        val output = StringBuilder().apply {
            Cache.priceLinks.forEach { lnk ->
                val (price, link) = lnk
                appendLine("<a href='${link}'>${price.title}</a>")
            }
        }

        client.sendMessage(user, output.toString())
    }

//    fun processCommandPaymentsList(chatId: Long, message: Message) {
//
//        if (!BotService.isAdmin(chatId)) {
//            return
//        }
//
//        val payments = storage.getPayments()
////        storage.setPaymentInfo(payments.last())
//        if (payments.isEmpty()) {
//            bot.sendMessage(chatId, "Платежи пусты")
//            return
//        }
//
//        val output = StringBuilder()
//        payments.forEachIndexed { index, payment ->
//            output.appendLine(
//                "${index + 1} | ${payment.userId} | ${payment.totalAmount} ${payment.currency} |  ${
//                    payment.date.format(
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                    )
//                }"
//            )
//        }
//
//        bot.sendMessage(chatId, output.toString())
//
//
//    }


//    fun downloadHistory(chatId: Long, button: String) {
//
//
//        val history = when (button) {
//            "download_only" -> storage.getHistory()
//            "download_clear" -> storage.getHistoryAndClear()
//            else -> {
//                bot.sendMessage(chatId, "Неизвестный контекст")
//                return
//            }
//        }
//
//
//
//        if (history.isEmpty()) {
//            bot.sendMessage(chatId, "История пуста")
//            return
//        }
//
//        val tempFile = File.createTempFile("history_", ".tsv").apply { deleteOnExit() }
//
//        // Write to the temporary file
//        tempFile.printWriter().use { out ->
//            out.println(
//                "${escapeCsvField("date")}\t${escapeCsvField("context")}\t${escapeCsvField("prefix")}\t" +
//                        "${escapeCsvField("request")}\t${escapeCsvField("response")}"
//            )
//            history.forEach { row ->
//                out.println(
//                    "${escapeCsvField(row.date)}\t${escapeCsvField(row.context)}\t${escapeCsvField(row.prefix)}\t" +
//                            "${escapeCsvField(row.message)}\t${escapeCsvField(row.output)}"
//                )
//            }
//        }
//
//
//        val outputFilename = "history_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm"))}.tsv"
//
//        val doc = SendDocument(chatId.toString(), InputFile(tempFile, outputFilename))
//        bot.telegramClient.execute(doc)
//
//    }

}

fun escapeCsvField(field: String): String {
    val escapedField = field.replace("\"", "\"\"") // Escape double quotes
    return if (escapedField.contains(Regex("[\t\n\"]"))) {
        "\"$escapedField\"" // Enclose in quotes if it contains special characters
    } else {
        escapedField
    }
}