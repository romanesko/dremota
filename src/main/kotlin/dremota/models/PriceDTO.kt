package dremota.models

import dremota.Price
import dremota.lib.fromDbBool
import kotlinx.serialization.Serializable

@Serializable
data class PriceDTO(
    val id: Long,
    val title: String,
    val amount: Long,
    val currency: String,
    val days: Long,
    val enabled: Boolean,
    val createdAt: String?,
)

fun Price.toDTO(): PriceDTO {
    return PriceDTO(
        id,
        title,
        amount,
        currency,
        days,
        fromDbBool(enabled),
        created_at
    )
}