package dremota.models

import kotlinx.serialization.Serializable


@Serializable
data class AuthCodeDTO (
    val code: String
)