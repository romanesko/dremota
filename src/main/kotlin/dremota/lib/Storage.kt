package dremota.lib

import dremota.plugins.Db
import java.time.LocalDateTime

object Storage {
    fun get(key: String): String? {
        return Db.keyValueQueries.selectByKey(key).executeAsOneOrNull()?.value_
    }



    fun set(key: String, value: String, expireDays: Long) {
        LocalDateTime.now().plusDays(expireDays).let {
            Db.keyValueQueries.upsert(key, value, it.format(dateFormat))
        }
    }

    fun delete(key: String) {

    }

    fun deleteExpired() {
        Db.keyValueQueries.deleteExpired()
    }
}