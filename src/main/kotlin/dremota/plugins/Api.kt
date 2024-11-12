package dremota.plugins

import dremota.api.Api
import dremota.api.PerplexityApi
import dremota.lib.env
import io.ktor.server.application.*

lateinit var api: Api;

fun Application.configureApi() {
    //    api = GptApi(env("PROXY_URL"), env("PROXY_TOKEN"))
        api = PerplexityApi("https://api.perplexity.ai/chat/completions",env("PERPLEXITY_TOKEN"))
//    api = MockApi()
}