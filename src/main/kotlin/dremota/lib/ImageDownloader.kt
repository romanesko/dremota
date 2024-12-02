package dremota.lib


import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

const val IMAGES_DIR = "static/images"

object ImageDownloader {

    fun downloadImageAsBase64(imageUrl: String): String? {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val inputStream: InputStream = connection.inputStream
        val imageBytes = inputStream.readBytes()

        val filename = UUID.randomUUID().toString() + ".png"

        val directory = File(IMAGES_DIR)
        if (!directory.exists()) directory.mkdirs()

        val fileToSave = File("$IMAGES_DIR/$filename")
        fileToSave.writeBytes(imageBytes)

        return filename
    }


}