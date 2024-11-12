package dremota.models

import dremota.Payment
import kotlinx.serialization.Serializable

@Serializable
 data class PaymentDTO(
     val id: Long,
     val chatId: Long,
     val createdAt: String,
     val amount: Long,
     val currency: String,
)

fun Payment.toDTO(): PaymentDTO {
    return PaymentDTO(id, chat_id, created_at, amount, currency)
}