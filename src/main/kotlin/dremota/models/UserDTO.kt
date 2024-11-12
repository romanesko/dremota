package dremota.models

import dremota.User
import dremota.lib.dateFormat
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class UserDTO(
    val chatId: Long,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val lastVisit: String? = null,
    val paidUntil: String? = null,
    var activePayment: Boolean,
    var displayName: String,
    val createdAt: String,
    val invitedBy: Long? = null,
    val referral: String
) {
    init {
        displayName = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
            !firstName.isNullOrBlank() -> firstName
            !username.isNullOrBlank() -> username
            else -> chatId.toString()
        }
        if (paidUntil != null) {
            val paidUntilDate = LocalDateTime.parse(paidUntil, dateFormat)
            if (paidUntilDate.isAfter(LocalDateTime.now())) {
                activePayment = true
            }
        }
    }
}



fun User.toDTO() : UserDTO{
    return UserDTO(chat_id,username,first_name,last_name,photo_url,last_visit, paid_until,
        false, "", created_at, invited_by, referral)
}