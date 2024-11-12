package dremota.plugins

import dremota.lib.Storage
import dremota.lib.createTaskScope
import io.ktor.server.application.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.Duration

fun Application.scheduleHourlyTask() {

    val logger = LoggerFactory.getLogger("scheduleHourlyTask")


    val taskScope = createTaskScope()


    taskScope.launch {
        while (isActive) {
            try {
                logger.info("Deleting expired storage keys")
                Storage.deleteExpired()
                delay(Duration.ofHours(1).toMillis())
            } catch (e: Exception) {
                // Handle any exceptions that occur during the task
                e.printStackTrace()
            }
        }
    }
}