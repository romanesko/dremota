package dremota.lib

import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


object ImageDownloader {

    fun downloadImageAsBase64(imageUrl: String): String? {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val inputStream: InputStream = connection.inputStream
        val imageBytes = inputStream.readBytes()

        val filename = UUID.randomUUID().toString() + ".png"

        val fileToSave = File("src/main/resources/images/$filename")
        fileToSave.writeBytes(imageBytes)

        return filename
    }


}