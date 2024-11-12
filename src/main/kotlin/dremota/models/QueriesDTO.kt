package dremota.models

import dremota.Queries
import kotlinx.serialization.Serializable

@Serializable
 data class QueriesDTO(
     val id: Long,
     val chatId: Long,
     val request: String,
     val context: String,
     val prefix: String,
     val response: String?,
     val createdAt: String,
     val updatedAt: String?,
)


fun Queries.toDTO() : QueriesDTO{
    return QueriesDTO(id,chat_id,request,context,prefix,response,created_at,updated_at)
}