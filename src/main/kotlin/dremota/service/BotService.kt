package dremota.service

import dremota.bot.Client
import dremota.bot.models.Button
import dremota.lib.*
import dremota.models.PriceDTO
import dremota.models.UserDTO
import dremota.service.ReferralService.getUserIdByReferralCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private val logger: org.slf4j.Logger = LoggerFactory.getLogger("BotService")


val tmpBtnStorage = mutableMapOf<String, Button>() // TODO: Remove
val tmpContextStorage = mutableMapOf<Long, String?>() // TODO: Remove

data class PaymentInfo(
    val userId: Long,
    val currency: String,
    val totalAmount: Int,
    val invoicePayload: String,
    val providerPaymentChargeId: String,
    val telegramPaymentChargeId: String,
    var date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
)

object BotService {

    private val logger: Logger = LoggerFactory.getLogger("BotService")

    private val taskScope = createTaskScope()


    private lateinit var client: Client

    fun init(client: Client) {
        this.client = client

        EventManager.addListener(BotCommandsChangedEvent::class.java) {
            logger.info("BotCommandsChangedEvent received")
            val commands = CommandsService.selectActiveVisibleCommands()
            client.updateCommands(commands)
        }

        EventManager.addListener(ReferralAcceptedEvent::class.java) { event ->
            logger.info("ReferralAcceptedEvent received")

            taskScope.launch {
                delay(2000)
                client.sendMessageToChatId(event.userId, event.message)
            }

        }


    }


    fun setButtonAction(chatId: Long, btn: Button): String {
        val key = UUID.randomUUID().toString()
        tmpBtnStorage[key] = btn
        return key
    }

    fun getButtonAction(chatId: Long, data: String?): Button? {
        return tmpBtnStorage[data]
    }


    fun setContext(chatId: Long, context: String?) {
        logger.info("setContext $chatId: $context")
        tmpContextStorage[chatId] = context
    }

    fun getContext(chatId: Long): String? {
        return tmpContextStorage[chatId]
    }

    fun isAdmin(chatId: Long): Boolean {
        return UsersService.isAdmin(chatId)
    }



    fun getSettings(key: String): String? {
        return SettingsService.getSettingsByKey(key)
    }

    fun saveRequest(chatId: Long, context: String, prefix: String, message: String): Long {
        return QueryService.insertRequest(chatId, context, prefix, message)

    }

    fun saveResponse(reqId: Long, output: String) {
        QueryService.setResponse(output, reqId)
    }

    fun setPaymentInfo(paymentInfo: PaymentInfo): Long {
        return PaymentsService.insert(
            paymentInfo.userId,
            paymentInfo.totalAmount,
            paymentInfo.currency,
            paymentInfo.invoicePayload,
            paymentInfo.providerPaymentChargeId,
            paymentInfo.telegramPaymentChargeId
        )
    }

    fun checkUser(message: Message): UserDTO {
        val tgUser = message.from;
        var user = UsersService.getUser(tgUser.id)
        if (user == null) {
            if (!message.hasText() || !message.text.startsWith("/start")) {
                client.sendMessageToChatId(message.from.id, "Начните с команды /start")
                throw Exception("User not found")
            }
            user = processStartCommand(message)
        }

        UsersService.updateLastVisit(user.chatId)
        return user

    }

    fun addBalance(user: UserDTO, price: PriceDTO, paymentId: Long) {
        BalanceService.create(user.chatId, price.days, "Покупка «${price.title}»", paymentId)
    }


    private fun processStartCommand(message: Message): UserDTO {

        val referralCode = message.text.split(" ").let { args ->
            if (args.size > 1) { // есть реферрал
                args[1]
            } else null
        }

        return UsersService.insertUser(
            UserDTO(
                chatId = message.from.id,
                username = message.from.userName,
                firstName = message.from.firstName,
                lastName = message.from.lastName,
                photoUrl = getUserPhoto(message.from.id),
                activePayment = false,
                displayName = "",
                createdAt = LocalDateTime.now().format(dateFormat),
                invitedBy = getUserIdByReferralCode(referralCode),
                referral = generateRandomString(16)
            )
        ).also {
            ReferralService.processReferral(it, referralCode)
        }


    }

    fun getUserPhoto(chatId: Long): String {
        return client.getUserProfilePhoto(chatId)
    }





}