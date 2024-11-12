package dremota.api.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest(val context: String, val message: String, val model: String? =null)
