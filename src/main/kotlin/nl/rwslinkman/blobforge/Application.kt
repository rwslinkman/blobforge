package nl.rwslinkman.blobforge

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import nl.rwslinkman.blobforge.http.ApiKey
import nl.rwslinkman.blobforge.http.model.BlobForgeApiError
import nl.rwslinkman.blobforge.http.model.BlobSetupRequest
import nl.rwslinkman.blobforge.http.model.BlobSetupResponse
import nl.rwslinkman.blobforge.storage.JsonBlobStorage
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // is used by Ktor
fun Application.module() {
    println("BlobForge v${Environment.version}")
    val baseDir = File(Environment.storageBaseDir)
    // ensure base directory exist, else create it
    if (!baseDir.exists()) {
        baseDir.mkdir()
    }
    val storage = JsonBlobStorage(baseDir)

    routing {
        route("/json/{blobName}") {
            put {
                val (apiKey, blobName) = readApiKeyAndBlobName()
                val blob: String = call.receiveText()

                storage.storeBlob(apiKey, blobName, blob)

                call.respond(HttpStatusCode.Accepted)
            }
            get {
                // Read json blob from storage
                val (apiKey, blobName) = readApiKeyAndBlobName()

                val storedJSON: String = storage.readStoredBlob(apiKey, blobName)
                call.response.header("Content-type", "application/json")
                call.respond(HttpStatusCode.OK, storedJSON)
            }
        }
        route("/init") {
            post {
                val setupRequest: BlobSetupRequest = call.receive<BlobSetupRequest>()

                val response = handleInit(setupRequest, storage)

                call.respond(HttpStatusCode.OK, response)
            }
        }
        route("/status/{blobName}") {
            get {
                // TODO: Return status of blob
            }
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            // Global exception handler
            val error = when (cause) {
                is BadRequestException -> {
                    BlobForgeApiError(HttpStatusCode.BadRequest, "Invalid request body submitted")
                }
                is ApiKeyException,
                is BlobException -> {
                    // Convert known exceptions to standard format
                    BlobForgeApiError(HttpStatusCode.Forbidden, cause.message.toString())
                }
                else -> {
                    // Catch-all clause
                    BlobForgeApiError(HttpStatusCode.InternalServerError, cause.message.toString())
                }
            }
            // Always response with error JSON
            call.respond(HttpStatusCode.fromValue(error.statusCode), error)
        }
    }
}

private fun handleInit(setupRequest: BlobSetupRequest, storage: JsonBlobStorage): BlobSetupResponse {
    if (!ApiKey.isValidApiKey(setupRequest.apiKey)) throw ApiKeyException("API Key does not meet requirements")

    var cleanupExecuted = false
    if (!storage.isKeyKnown(setupRequest.apiKey)) {
        storage.createStorage(setupRequest.apiKey)
    } else if (setupRequest.cleanInit) {
        storage.cleanUp(setupRequest.apiKey)
        cleanupExecuted = true
    }
    val currentBlobs = storage.listBlobs(setupRequest.apiKey)
    return BlobSetupResponse(setupRequest.apiKey, currentBlobs, cleanupExecuted)
}

private fun PipelineContext<Unit, ApplicationCall>.readApiKeyAndBlobName(): Pair<String, String> {
    val apiKey = call.request.headers[ApiKey.HEADER]
        ?: throw ApiKeyException("Did not find '${ApiKey.HEADER}' header in request")
    if (!ApiKey.isValidApiKey(apiKey)) throw ApiKeyException("Value of '${ApiKey.HEADER}' does not meet API Key requirements")
    val blobName = call.parameters["blobName"] ?: throw BlobException("Did not find 'blobName' parameter in request")
    return Pair(apiKey, blobName)
}
