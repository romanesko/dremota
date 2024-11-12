package dremota.api

import dremota.api.models.ApiRequest
import dremota.api.models.ApiResponse
import dremota.lib.cleanUpString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class _Api(private val apiUrl: String, private val apiToken: String) : IApi {

    private val client: OkHttpClient = OkHttpClient()

    abstract fun generatePayload(req: ApiRequest): String
    abstract fun parseResponse(body: String): ApiResponse
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)


    override suspend fun request(req: ApiRequest): ApiResponse {

        val content = generatePayload(req)
        val requestBody: RequestBody = content.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .header("Authorization", "Bearer $apiToken")
            .url(apiUrl)
            .post(requestBody)
            .build()
        var responseBody = ""
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Unsuccessful request for $request\n<code>${response}</code>")
                }
                responseBody = response.body?.string() ?: "{}"

                return parseResponse(responseBody)

            }
        } catch (e: Exception) {
            logger.error("Request error: $responseBody\n${e.message}")

            val reqJson = Json.encodeToString(req)

            var msg = "Error while parsing response for\n${request.url}\n\n" +
                    "<b>Request:</b>\n<pre><code class=\"json\">${reqJson}</code></pre>\n"

            logger.info(responseBody)

            if (responseBody.isNotEmpty()) {
                msg += "<b>Response:</b>\n" +
                        "<pre><code class=\"json\">${cleanUpString(responseBody)}</code></pre>\n"

            }

            msg += "<b>Exception:</b><pre>${e.message}</pre>\n"

            throw RuntimeException(msg)

        }
    }


}