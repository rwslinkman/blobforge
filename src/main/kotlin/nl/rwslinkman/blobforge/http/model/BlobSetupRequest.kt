package nl.rwslinkman.blobforge.http.model

import kotlinx.serialization.Serializable

@Serializable
data class BlobSetupRequest(val apiKey: String, val cleanInit: Boolean = false)
