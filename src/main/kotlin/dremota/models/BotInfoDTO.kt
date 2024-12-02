package dremota.models

import kotlinx.serialization.Serializable

@Serializable
data class BotInfoDTO(
    val name: String,
    val image: String,
)