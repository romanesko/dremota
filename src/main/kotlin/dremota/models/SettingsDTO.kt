package dremota.models

import kotlinx.serialization.Serializable

@Serializable
data class SettingsDTO(
    val prefix: String,
    val context: String,
    val post: String,
    val emptyBalanceMessage: String
)