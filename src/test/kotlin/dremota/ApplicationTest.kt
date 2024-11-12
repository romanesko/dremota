package dremota

import dremota.api.models.ApiRequest
import dremota.plugins.*
import dremota.service.BotService
import dremota.service.UsersService
import io.ktor.server.testing.*
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureDatabase(true)
            configureApi()
            configureSecurity()
            configureHTTP()
            configureRouting()
            configureBot()
            UsersService.getAll().forEach {
                if (!it.photoUrl.isNullOrEmpty()) return@forEach
                val photo = BotService.getUserPhoto(it.chatId)
                UsersService.updatePhotoUrl(it.chatId, photo)
            }

//            val res = api.request(ApiRequest("Ты профессиональный толкователь снов","Какое расстояние от луны до земли?"))
//            println("\n!!!!!")
//            println(res)



        }

    }

}
