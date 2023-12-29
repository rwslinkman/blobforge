package nl.rwslinkman.blobforge.storage

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import nl.rwslinkman.blobforge.BlobException
import java.io.File

class JsonBlobStorage(private val basePath: File) {

    fun readStoredBlob(key: String, blobName: String): String {
        val keyDir = File(basePath, key)
        if (!keyDir.exists() || !keyDir.isDirectory) {
            throw BlobException("Unknown client")
        }

        if (!isValidBlobName(blobName)) throw BlobException("Blob name does not meet expectations")

        val blobFile = File(keyDir, "$blobName.json")
        if (!blobFile.exists()) throw BlobException("Blob name does not exist")
        return blobFile.readText()
    }

    private fun isValidBlobName(name: String): Boolean = blobNameValidationRegex.matches(name)

    fun isKeyKnown(key: String): Boolean {
        val keyDir = File(basePath, key)
        return keyDir.exists() && keyDir.isDirectory
    }

    fun createStorage(key: String) {
        val keyDir = File(basePath, key)
        if (!keyDir.exists()) {
            keyDir.mkdir()
        }
    }

    fun cleanUp(key: String) {
        val keyDir = File(basePath, key)
        if (keyDir.exists() && keyDir.isDirectory) {
            keyDir.listFiles()?.forEach { it.deleteRecursively() }
        }
    }

    fun listBlobs(key: String): List<String> {
        val keyDir = File(basePath, key)
        return keyDir.listFiles()?.map { it.nameWithoutExtension } ?: listOf()
    }

    fun storeBlob(key: String, blobName: String, blob: String) {
        val keyDir = File(basePath, key)
        if (!keyDir.exists() || !keyDir.isDirectory) {
            throw BlobException("Unknown client")
        }

        if (!isValidBlobName(blobName)) throw BlobException("Blob name does not meet expectations")

        if (!isValidJSON(blob)) throw BlobException("Unable to store invalid JSON")

        val blobFile = File(keyDir, "$blobName.json")
        if(!blobFile.exists()) {
            blobFile.createNewFile()
        }
        blobFile.writeText(blob)
    }

    private fun isValidJSON(candidate: String): Boolean {
        return try {
            // Attempt to decode the JSON string
            Json.decodeFromString<JsonElement>(candidate)
            true // Return true if parsing succeeds (valid JSON)
        } catch (e: SerializationException) {
            e.printStackTrace()
            false // Return false if parsing fails (invalid JSON)
        }
    }

    companion object {
        private val blobNameValidationRegex = Regex("[a-zA-Z0-9\\-]{5,255}")
    }
}