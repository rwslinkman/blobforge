package nl.rwslinkman.blobforge.http.model

import kotlinx.serialization.Serializable

@Serializable
data class BlobSetupResponse(val apiKey: String, val blobList: List<String>, val cleanupExecuted: Boolean)
