package nl.rwslinkman.blobforge

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
}