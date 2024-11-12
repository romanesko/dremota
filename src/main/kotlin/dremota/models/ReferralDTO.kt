package dremota.models

import kotlinx.serialization.Serializable


@Serializable
data class ReferralDTO(
    val enabled: Boolean,
    val holderReceives: Long,
    val holderMessage: String,
    val referrerReceives: Long,
    val referrerMessage: String
)