package dremota.service

import dremota.Auth
import dremota.controller.AuthState

import dremota.controller.TgAuthRequest
import dremota.lib.env
import dremota.models.AuthCodeDTO
import dremota.models.AuthDTO
import dremota.models.UserDTO
import dremota.plugins.Db
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.concurrent.schedule
import kotlin.random.Random


object AuthService {
    private val logger: Logger = LoggerFactory.getLogger("AuthService")

    private val LoginStorage = mutableMapOf<String, String?>()

    @Deprecated("")
    fun validateHash(req: TgAuthRequest): Boolean {
        val sb = StringBuilder()
            .append("auth_date=${req.authDate}")

        if (req.firstName != null) {
            sb.append("\nfirst_name=${req.firstName}")
        }

        sb.append("\nid=${req.id}")

        if (req.lastName != null) {
            sb.append("\nlast_name=${req.lastName}")
        }

        if (req.photoUrl != null) {
            sb.append("\nphoto_url=${req.photoUrl}")
        }

        if (req.username != null) {
            sb.append("\nusername=${req.username}")
        }


        val myBotToken = env("BOT_TOKEN")
        val key = sha256(myBotToken)
        val validateHash = hmacSha256(key, sb.toString())

        return (req.hash == validateHash)

    }

    fun updateToken(user: UserDTO): String {
        val token = UUID.randomUUID().toString()
        Db.authQueries.updateToken(chat_id = user.chatId, token = token)
        return token
    }

    fun isAdmin(chatId: Long): Boolean {
        return Db.authQueries.selectByChatId(chatId).executeAsOneOrNull()?.role == "admin"
    }

    fun setAdmin(user: UserDTO) {
        Db.authQueries.insert(Auth(chat_id = user.chatId, token = UUID.randomUUID().toString(), role = "admin"))
    }

    fun getUserByToken(token: String): AuthDTO? {
        return Db.authQueries.selectByToken(token).executeAsOneOrNull()?.let { auth ->
            val user = UsersService.getUser(auth.chat_id) ?: return null
            AuthDTO(
                user = user,
                role = auth.role

            )
        }
    }

    fun telegramLogin(user: UserDTO, token: String): Boolean {
        if (!LoginStorage.containsKey(token)) {
            return false
        }
        logger.info("telegramLogin $token")

        val newToken = updateToken(user)

        LoginStorage[token] = newToken
        return true
    }

    fun generateCode(): Any {
        val code = generateRandomNumberString()
        LoginStorage[code] = null

        Timer().schedule(120_000) {
            LoginStorage.remove(code)
        }

        return AuthCodeDTO(code)

    }

    private fun generateRandomNumberString(): String {
        fun randomThreeDigits() = Random.nextInt(100, 1000).toString()
        return listOf(randomThreeDigits(), randomThreeDigits(), randomThreeDigits()).joinToString("-")
    }

    fun checkUserLoggedIn(receive: AuthCodeDTO): AuthState {
        val token = LoginStorage[receive.code]

        if (token !== null) {
            LoginStorage.remove(receive.code)
        }

        return AuthState(token)
    }

}


private fun sha256(input: String): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(input.toByteArray(Charsets.UTF_8))
}

private fun hmacSha256(key: ByteArray, data: String): String {
    val mac = Mac.getInstance("HmacSHA256")
    val secretKeySpec = SecretKeySpec(key, "HmacSHA256")
    mac.init(secretKeySpec)
    val hmac = mac.doFinal(data.toByteArray(Charsets.UTF_8))
    return hmac.joinToString("") { String.format("%02x", it) }
}