package dremota.models

import dremota.Feedback
import dremota.lib.fromDbBool
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackDTO(
    val id: Long,
    val userId: Long,
    val message: String,
    val read: Boolean,
    val createdAt: String,
)

fun Feedback.toDTO(): FeedbackDTO {
    return FeedbackDTO(
        id,
        user_id,
        message,
        fromDbBool( read),
        created_at
    )
}