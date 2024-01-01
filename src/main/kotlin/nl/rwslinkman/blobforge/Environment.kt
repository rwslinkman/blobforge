package nl.rwslinkman.blobforge

import java.util.*

object Environment {

    val storageBaseDir: String
        get(): String {
            var volumePath = System.getenv("BLOBFORGE_VOLUME")
            if(volumePath.isNullOrBlank()) {
                // For local development (not in Docker container)
                volumePath = "${System.getProperty("user.dir")}/blobforge-storage"
            }
            return volumePath
        }

    val adminApiKey = System.getenv("BLOBFORGE_ADMIN_KEY")

    private val versionProps by lazy {
        Properties().also {
            it.load(this.javaClass.getResourceAsStream("/version.properties"))
        }
    }

    val version by lazy {
        versionProps.getProperty("version") ?: "no version"
    }
}