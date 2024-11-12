package dremota.models

import dremota.Balance
import kotlinx.serialization.Serializable

@Serializable
data class BalanceDTO(
     val id: Long,
     val chatId: Long,
     val createdAt: String,
     val days: Long,
     val due: String?,
     val paymentId: Long?,
     var payment: PaymentDTO?,
     val rationale: String?,
)


fun Balance.toDTO() : BalanceDTO{
    return BalanceDTO(id,chat_id,created_at,days,due,payment,null,rationale)
}