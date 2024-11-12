package dremota.models

import kotlinx.serialization.Serializable

@Serializable
data class IncomeDTO(val amount: Long, val currency: String)

@Serializable
data class UserStatDTO(
    val totalRequests: Long,
    val totalIncome: List<IncomeDTO>
)