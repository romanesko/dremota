package dremota.api

import dremota.api.models.ApiRequest
import dremota.api.models.ApiResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class GptApi(apiUrl: String, apiToken: String) : Api(apiUrl, apiToken) {

    override fun generatePayload(req: ApiRequest): String {
        return Json.encodeToString(req)
    }

    override fun parseResponse(body: String): ApiResponse {
        return Json.decodeFromString<ApiResponse>(body)
    }

}