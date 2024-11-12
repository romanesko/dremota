package dremota.api


import dremota.api.models.ApiRequest
import dremota.api.models.ApiResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PerplexityApi(apiUrl: String, apiToken: String, private val model: String = "llama-3.1-8b-instruct") : Api(apiUrl, apiToken) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun generatePayload(req: ApiRequest): String {
        return Json.encodeToString(
            PerplexityApiRequest(
                model = req.model ?: model,
                messages = listOf(
                    Message(role = "system", content = req.context),
                    Message(role = "user", content = req.message)
                )
            )
        )

    }

    override fun parseResponse(body: String): ApiResponse {
        val ppxResponse = json.decodeFromString<PerplexityApiResponse>(body)
        return ApiResponse(ppxResponse.choices[0].message.content)
    }

}


@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class PerplexityApiRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class PerplexityApiResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)


