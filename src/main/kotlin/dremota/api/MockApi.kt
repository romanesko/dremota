package dremota.api

import dremota.api.models.ApiRequest
import dremota.api.models.ApiResponse

class MockApi(): IApi {
    override suspend fun request(req: ApiRequest): ApiResponse {
        return ApiResponse("MockApi")
    }

}