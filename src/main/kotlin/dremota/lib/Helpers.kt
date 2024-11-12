package dremota.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.format.DateTimeFormatter

val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
val dateFormatRu: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")


fun env(name: String): String {
    return System.getenv(name) ?: throw RuntimeException("$name environment variable missing")
}

fun cleanUpString(input: String): String {
    return input
        .replace(Regex("\\s+"), " ") // Reduce multiple spaces to a single space
        .replace(Regex("([\\{|\\[])(\\s)"), "$1") // Remove spaces around brackets
        .replace(Regex("(\\s)([\\}|\\]])"), "$1") // Remove spaces around brackets
        .trim() // Trim leading/trailing spaces
}


fun dbBool(value :Boolean?): Long{
    return if (value!= null && value) 1L else 0L
}

fun fromDbBool(value: Long): Boolean {
    return value == 1L
}

fun createTaskScope(): CoroutineScope {
    return CoroutineScope(Dispatchers.IO + SupervisorJob())
}

fun generateRandomString(length: Int = 12): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}