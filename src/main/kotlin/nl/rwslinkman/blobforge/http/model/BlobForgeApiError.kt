package nl.rwslinkman.blobforge.http.model

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class BlobForgeApiError(val statusCode: Int, val status: String, val message: String) {
    constructor(statusObject: HttpStatusCode, message: String): this(statusObject.value, statusObject.description, message)
}
