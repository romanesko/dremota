package dremota.lib

import dremota.models.CommandDTO
import dremota.models.PriceDTO
import dremota.models.SettingsDTO
import dremota.plugins.tgClient
import dremota.service.CommandsService
import dremota.service.PriceService
import dremota.service.SettingsService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice

object Cache {

    private val logger = LoggerFactory.getLogger("cache")


    lateinit var commands: Map<String, CommandDTO>
    lateinit var priceLinks: MutableList<Pair<PriceDTO,String>>
    lateinit var settings: SettingsDTO


    init {
        EventManager.addListener(BotCommandsChangedEvent::class.java) {  refreshCommands() }
        EventManager.addListener(PriceChangedEvent::class.java) {  refreshPrice() }
        EventManager.addListener(SettingsChangedEvent::class.java) {  refreshSettings() }

        refreshSettings()
        refreshCommands()
        refreshPrice()
    }

    private fun refreshCommands() {
        commands = CommandsService.selectActiveCommands().associateBy { it.key }
    }

    private fun refreshPrice() {
        val price = PriceService.getEnabled()
        val links = mutableListOf<Pair<PriceDTO,String>>()
        for (p in price) {
            val payload = Json.encodeToString(p)
            val key= "${p.id}:${p.amount}:${p.currency}:${p.days}"
            Storage.set("price:$key", payload, 30)
            logger.info("Payload: $payload")
            val lnk = CreateInvoiceLink(p.title, p.title, key, p.currency, listOf(LabeledPrice(p.amount.toString(), p.amount.toInt())))
            val l = tgClient.telegramClient.execute(lnk)
            links.add(Pair(p,l))
        }
        priceLinks = links
    }
    private fun refreshSettings() {
        settings = SettingsService.getSettings()
    }


}