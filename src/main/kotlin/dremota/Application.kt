package dremota

import dremota.lib.Cache
import dremota.plugins.*
import dremota.service.BotService
import dremota.service.CommandsService
import dremota.service.PriceService
import dremota.service.SettingsService
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}



fun Application.module() {
    configureDatabase()
    configureApi()
    configureSecurity()
    configureHTTP()
    configureRouting()
    configureBot()


    SettingsService // create defaults
    Cache // register listeners
    BotService // register listeners
    CommandsService // rising init fill the commands
    PriceService // create defaults

    scheduleHourlyTask()
}
