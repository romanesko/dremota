package dremota.service

import dremota.Price
import dremota.lib.EventManager
import dremota.lib.PriceChangedEvent
import dremota.lib.dbBool
import dremota.models.PriceDTO
import dremota.models.toDTO
import dremota.plugins.Db
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PriceService {
    private val logger: Logger = LoggerFactory.getLogger("PriceService")


    init {
        logger.info("PriceService init")
        createDefaultPrices()
    }

    private fun createDefaultPrices() {
        if (Db.priceQueries.count().executeAsOne() > 0) {
            return
        }
        Db.priceQueries.insert(title = "День за звезду", amount = 1, currency = "XTR", days = 1, enabled = 0)
    }

    fun getAll(): List<PriceDTO> {
        return Db.priceQueries.selectAll().executeAsList().map(Price::toDTO)
    }

    fun getEnabled(): List<PriceDTO> {
        return Db.priceQueries.selectEnabled().executeAsList().map(Price::toDTO)
    }

    fun upsert(receive: PriceDTO) {
        Db.priceQueries.upsert(
            if (receive.id < 1L) null else receive.id,
            receive.title,
            receive.amount,
            receive.currency,
            receive.days,
            dbBool( receive.enabled)
        )
        EventManager.notify(PriceChangedEvent())
    }

    fun delete(receive: PriceDTO) {
        Db.priceQueries.delete(receive.id)
        EventManager.notify(PriceChangedEvent())
    }

}