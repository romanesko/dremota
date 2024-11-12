package dremota.plugins

import dremota.bot.Client
import dremota.lib.env
import dremota.service.BotService
import io.ktor.server.application.*
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

lateinit var  tgClient : Client

fun Application.configureBot() {
    val botToken =env("BOT_TOKEN")
    val telegramClient = OkHttpTelegramClient(botToken)

    tgClient= Client(telegramClient, botToken)
    val tgBot = dremota.bot.Bot(tgClient, env("ADMIN_PASSWORD"))
    BotService.init(tgClient)

//    System.getProperties()["proxySet"] = "true";
//    System.getProperties()["socksProxyHost"] = "127.0.0.1";
//    System.getProperties()["socksProxyPort"] = "9150";

    TelegramBotsLongPollingApplication().registerBot(botToken, tgBot)

}