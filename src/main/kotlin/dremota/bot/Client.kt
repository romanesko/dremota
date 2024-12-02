package dremota.bot

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import dremota.bot.models.GeneralResponse
import dremota.lib.ImageDownloader.downloadImageAsBase64
import dremota.lib.dateFormat
import dremota.lib.dateFormatRu
import dremota.models.CommandDTO
import dremota.models.UserDTO
import dremota.service.BotService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ActionType
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.GetMe
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalDateTime
import org.telegram.telegrambots.meta.api.methods.send.SendMessage as sm

class Client(val telegramClient: TelegramClient,val botToken: String) {

    private val handlebars: Handlebars = Handlebars().with(EscapingStrategy.NOOP)

    private val logger: Logger = LoggerFactory.getLogger("tgClient")


    fun sendErrorMessage(user: UserDTO, error: String) {
        if (BotService.isAdmin(user.chatId)) {
            sendMessage(user, error)
        } else {
            sendMessage(user, "Ошибка при обработке запроса, мы уже разбираемся с этим")
        }
    }

    fun sendTyping(chatId: Long) {
        telegramClient.execute(SendChatAction(chatId.toString(), ActionType.TYPING.name));
    }

    private fun processTemplate(message: String, user: UserDTO): String {



        val template: Template = handlebars.compileInline(message, )

        val map = mutableMapOf<String, Any>()

        if (user.paidUntil != null) {
            try {
                map["paid_until"] = LocalDateTime.parse(user.paidUntil, dateFormat).format(dateFormatRu)
            } catch (e: Exception) {
                map["paid_until"] = user.paidUntil
            }
        }

        if (user.username != null) {
            map["username"] = "@" + user.username
        }

        if (user.firstName != null) {
            map["first_name"] = user.firstName
        }

        if (user.lastName != null) {
            map["last_name"] = user.lastName
        }

        map["referral"] = "<a href=\"https://t.me/$botUsername?start=${user.referral}\">$botUsername</a>"

        return template.apply(map).toString()
    }


    val botUsername : String by lazy  {
        telegramClient.execute(GetMe()).userName
    }



    fun sendMessage(user: UserDTO, message: String, markup: InlineKeyboardMarkup? = null): String {
        return processTemplate(message, user).also { msg ->
            sendMessageToChatId(user.chatId, msg, markup)
        }

    }

    fun sendMessageToChatId(chatId: Long, message: String, markup: InlineKeyboardMarkup? = null) {
        val msg = sm(chatId.toString(), message)

        if (markup != null) {
            msg.replyMarkup = markup
        }
        msg.parseMode = "HTML"
        msg.disableWebPagePreview = true
        telegramClient.execute(msg)
    }

    private fun sendMediaMessage(chatId: Long, urls: List<String>, captionString: String? = null) {
        val medias = urls.mapIndexed { index, url ->
            InputMediaPhoto(url).apply {
                if (index == 0) {
                    caption = captionString
                }
            }
        }
        val mediaGroup = SendMediaGroup(chatId.toString(), medias)
        telegramClient.execute(mediaGroup)
    }

    private fun createButtonRows(chatId: Long, generalResponse: GeneralResponse): List<InlineKeyboardRow> {

        val breakPositions = (generalResponse.buttonsRows ?: emptyList()).toSet()
        val cols = mutableListOf<InlineKeyboardButton>()
        val buttonRows = mutableListOf<InlineKeyboardRow>()

        (generalResponse.buttons ?: emptyList()).forEachIndexed { idx, btn ->
            val inlineKeyboardButton = InlineKeyboardButton(btn.label).apply {
                callbackData = BotService.setButtonAction(chatId, btn)
            }

            cols.add(inlineKeyboardButton)

            if (breakPositions.isEmpty() || breakPositions.contains(idx + 1)) {
                buttonRows.add(InlineKeyboardRow(cols.toList()))
                cols.clear()
            }
        }

        if (cols.isNotEmpty()) {
            buttonRows.add(InlineKeyboardRow(cols.toList()))
        }

        return buttonRows
    }

    @Deprecated("Не актуально в версии с управлением через интерфейс")
    fun sendGeneralResponse(chatId: Long, generalResponse: GeneralResponse) {
        BotService.setContext(chatId, generalResponse.context)

        for (msg in (generalResponse.telegramMessages ?: emptyList())) {

            if (msg.images.isNullOrEmpty()) {
                sendMessageToChatId(chatId, msg.text)
            } else {
                sendMediaMessage(chatId, msg.images, msg.text)
            }

        }

        val buttonRows = createButtonRows(chatId, generalResponse)

        if (buttonRows.isNotEmpty()) {
            val buttonsHeader = generalResponse.buttonsHeader ?: "Выберите действие:"
            sendMessageToChatId(chatId, buttonsHeader, InlineKeyboardMarkup(buttonRows))
        }
    }

    fun deleteMessage(chatId: Long, messageId: Int) {
        telegramClient.execute(DeleteMessage(chatId.toString(), messageId))

    }

    fun updateCommands(commands: List<CommandDTO>) {
        logger.info("Updating telegram commands")
        telegramClient.execute(DeleteMyCommands())
        val botCommands = ArrayList<BotCommand>()
        commands.forEach {
            botCommands.add(BotCommand(it.key, it.description))
        }
        telegramClient.execute(SetMyCommands(botCommands))

    }

    fun getUserProfilePhoto(id: Long): String {
        val gup = GetUserProfilePhotos(id)
        val upf =  telegramClient.execute(gup)

        if (upf.totalCount == 0) {
            return ""
        }

        val photo = upf.photos[0]
        val photoSize = photo.maxBy { it.width }

        val file = telegramClient.execute(GetFile(photoSize.fileId))
        return downloadImageAsBase64("https://api.telegram.org/file/bot${botToken}/${file.filePath}")?:""
    }
}