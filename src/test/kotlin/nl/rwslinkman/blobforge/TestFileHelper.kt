package nl.rwslinkman.blobforge

import java.nio.file.Files
import java.nio.file.Path

object TestFileHelper {

    internal fun createTempDirBlob(tempDir: Path, apiKey: String, blobName: String, fileExt: String = "json"): Path {
        val tenantDir = tempDir.resolve(apiKey)
        Files.createDirectory(tenantDir)
        val testFilePath = tempDir.resolve("$apiKey/$blobName.$fileExt")
        return Files.createFile(testFilePath)
    }

    internal fun createTempKeyDir(tempDir: Path, apiKey: String) {
        val tenantDir = tempDir.resolve(apiKey)
        Files.createDirectory(tenantDir)
    }
}