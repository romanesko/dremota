package dremota.service


import dremota.User
import dremota.lib.dateFormat
import dremota.models.UserDTO
import dremota.models.UserStatDTO
import dremota.models.toDTO
import dremota.plugins.Db
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class UserInfoResponse(val user: UserDTO, val stat: UserStatDTO)


object UsersService {


    fun getAll(): List<UserDTO> {
        return Db.usersQueries.selectAll().executeAsList().map(User::toDTO)
    }

    fun updateLastVisit(chatId: Long) {
        val lastVisit = LocalDateTime.now().format(dateFormat)
        Db.usersQueries.updateLastVisit(lastVisit, chatId)
    }

    fun insertUser(user: UserDTO): UserDTO {
        return User(
            chat_id = user.chatId,
            username = user.username,
            first_name = user.firstName,
            last_name = user.lastName,
            photo_url = user.photoUrl,
            last_visit = LocalDateTime.now().format(dateFormat),
            paid_until = null,
            referral =  user.referral,
            created_at = LocalDateTime.now().format(dateFormat),
            invited_by = user.invitedBy
        ).let {
            Db.usersQueries.insert(it)
            it.toDTO()
        }
    }

    fun getPaidUntil(userId: Long): LocalDateTime? {
        val row = Db.usersQueries.selectPaidUntilForUser(userId).executeAsOne()
        return row.let { r ->
            r.paid_until?.let {
                LocalDateTime.parse(it, dateFormat)
            }
        }
    }

    fun updatePaidUntil(userId: Long, paidUntilStr: String?) {
        Db.usersQueries.updatePaidUntil(paidUntilStr, userId)

    }

    fun isAdmin(chatId: Long): Boolean {
        return AuthService.isAdmin(chatId)
    }

    fun setAdmin(chatId: Long) {
        AuthService.setAdmin(chatId)

    }

    fun getUser(id: Long): UserDTO? {
        return Db.usersQueries.selectById(id).executeAsOneOrNull()?.toDTO()
    }


    fun getUserInfo(id: Long): UserInfoResponse {
        val user = getUser(id) ?: throw NoSuchElementException("User not found")

        QueryService.countForUser(id).let { count ->

            PaymentsService.getTotalForUser(id).let { total ->
                val stat = UserStatDTO(count, total)
                return UserInfoResponse(user, stat)
            }



        }

    }

    fun findByReferralCode(referralCode: String): UserDTO? {
        return Db.usersQueries.selectByReferralCode(referralCode).executeAsOneOrNull()?.toDTO()
    }

    fun updatePhotoUrl(chatId: Long, photo: String) {
        Db.usersQueries.updatePhotoUrl(chat_id = chatId, photo_url = photo)

    }
}


