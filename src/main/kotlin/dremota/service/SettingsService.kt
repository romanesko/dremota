package dremota.service

import dremota.Settings
import dremota.lib.EventManager
import dremota.lib.SettingsChangedEvent
import dremota.models.SettingsDTO
import dremota.plugins.Db

val keys = listOf(
    Pair("context","Ты профессиональный толкователь снов"),
    Pair("prefix",""),
    Pair("post","Вы можете оставить отзыв через команду /feedback"),
    Pair("empty_balance_message","Пополните ваш баланс, чтобы продолжить: /pay"),
)


object SettingsService {

    init {
        keys.forEach {
            val (key, default) = it
            Db.settingsQueries.insertIfNotExists(key, default)
        }
    }

    fun getSettings(): SettingsDTO {
        val records: List<Settings> = Db.settingsQueries.selectWhereKeyIn(keys.map { it.first }).executeAsList()
        return SettingsDTO(
            records.find { it.key == "prefix" }?.val_txt ?: "",
            records.find { it.key == "context" }?.val_txt ?: "",
            records.find { it.key == "post" }?.val_txt ?: "",
            records.find { it.key == "empty_balance_message" }?.val_txt ?: "",
        )
    }

    fun updateSettings(settings: SettingsDTO) {
        Db.settingsQueries.upsert("prefix", settings.prefix)
        Db.settingsQueries.upsert("context", settings.context)
        Db.settingsQueries.upsert("post", settings.post)
        EventManager.notify(SettingsChangedEvent())
    }

    fun setByKey(key: String, value: String) {
        Db.settingsQueries.upsert(key, value)
    }

    fun getSettingsByKey(key: String): String? {
        return Db.settingsQueries.selectValueByKey(key).executeAsOneOrNull()
    }


}