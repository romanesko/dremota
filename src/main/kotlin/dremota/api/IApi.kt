package dremota.api

import dremota.api.models.ApiRequest
import dremota.api.models.ApiResponse

interface IApi  {
    suspend fun request(req: ApiRequest): ApiResponse
}