package dremota.service

import dremota.Message
import dremota.models.MessageDTO
import dremota.models.toDTO
import dremota.plugins.Db
import dremota.plugins.tgClient

object MessagesService {


    fun getAllForUser(receiverId: Long): List<MessageDTO> {
        return Db.messageQueries.selectAllByReceiverId(receiverId).executeAsList().map(Message::toDTO)

    }

    fun add(receive: MessageDTO) {
        UsersService.getUser(receive.receiverId)?.let {
            tgClient.sendMessage(it, receive.message).let { msg ->
                return Db.messageQueries.insert(receive.senderId, receive.receiverId, msg)
            }

        } ?: throw NoSuchElementException("User not found")
    }
}