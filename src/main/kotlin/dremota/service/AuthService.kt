package dremota.service

import dremota.Auth

import dremota.controller.TgAuthRequest
import dremota.lib.env
import dremota.models.AuthDTO
import dremota.models.UserDTO
import dremota.plugins.Db
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object AuthService {
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
        Db.authQueries.insert(Auth(chat_id = user.chatId, token = token, role = "user"))
        return token
    }

    fun isAdmin(chatId: Long): Boolean {
        return Db.authQueries.selectByChatId(chatId).executeAsOneOrNull()?.role == "admin"
    }

    fun setAdmin(chatId: Long) {
        Db.authQueries.updateRole(chat_id = chatId, role = "admin")
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