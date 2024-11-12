package dremota.api


import dremota.api.models.ApiRequest
import dremota.api.models.ApiResponse
import dremota.lib.cleanUpString
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Api(private val apiUrl: String, private val apiToken: String) : IApi {


    abstract fun generatePayload(req: ApiRequest): String
    abstract fun parseResponse(body: String): ApiResponse
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun request(req: ApiRequest): ApiResponse {
        val content = generatePayload(req)

        val client = HttpClient()

        var responseBody = ""
        try {

            val response: HttpResponse = client.post(apiUrl) {
                header("Authorization", "Bearer $apiToken")
                contentType(ContentType.Application.Json)
                setBody(content)
            }
            responseBody = response.bodyAsText()


            return parseResponse(responseBody)

        } catch (e: Exception) {
            logger.error("Request error: $responseBody\n${e.message}")

            val reqJson = Json.encodeToString(req)

            var msg = "Error while parsing response for\n${apiUrl}\n\n" +
                    "<b>Request:</b>\n<pre><code class=\"json\">${reqJson}</code></pre>\n"

            if (responseBody.isNotEmpty()) {
                msg += "<b>Response:</b>\n" +
                        "<pre><code class=\"json\">${cleanUpString(responseBody)}</code></pre>\n"

            }

            msg += "<b>Exception:</b><pre>${e.message}</pre>\n"

            throw RuntimeException(msg)

        }
    }


}