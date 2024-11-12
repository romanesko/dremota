package dremota.models

import dremota.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
     val id: Long? = null,
     val senderId: Long,
     val receiverId: Long,
     val message: String,
     val createdAt: String,
)


fun Message.toDTO(): MessageDTO {
    return MessageDTO(
        id,
        sender_id,
        receiver_id,
        message,
        created_at
    )
}