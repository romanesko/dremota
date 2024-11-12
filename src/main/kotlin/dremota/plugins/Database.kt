package dremota.plugins

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dremota.Database
import io.ktor.server.application.*
import org.slf4j.LoggerFactory
import java.io.File


lateinit var Db: Database
private const val DB_DIR = "database"

fun Application.configureDatabase(skipMigration: Boolean = false): Database {


    val logger = LoggerFactory.getLogger("database")

    val directory = File(DB_DIR)
    if (!directory.exists()) directory.mkdirs()

    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${DB_DIR}/main.db", schema = Database.Schema)
    val database = Database(driver)

    if (!skipMigration) {

        for (i in 1..Database.Schema.version) {
            try {
                logger.info("Migrating to version $i")
                Database.Schema.migrate(driver, oldVersion = i - 1, newVersion = i)
            } catch (e: Exception) {
                logger.error("Migration failed for version $i ${e.message}")
            }
        }
    }

    Db = database
    return database


}