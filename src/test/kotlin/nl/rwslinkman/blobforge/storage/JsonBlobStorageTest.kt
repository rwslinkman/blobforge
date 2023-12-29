package nl.rwslinkman.blobforge.storage

import nl.rwslinkman.blobforge.BlobException
import nl.rwslinkman.blobforge.TestFileHelper
import nl.rwslinkman.blobforge.http.ApiKey
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class JsonBlobStorageTest {

    @TempDir
    lateinit var testBaseDir: Path

    private lateinit var subject: JsonBlobStorage

    @BeforeEach
    fun init() {
        subject = JsonBlobStorage(testBaseDir.toFile())
    }

    @Test
    fun `readStoredBlob - The blob content is returned as String for a valid key and blobName combination`() {
        // given
        val testApiKey = ApiKey.generateApiKey()
        val testContent = "{\"test\":\"content\"}"
        val testBlobName = "my-data-storage-blob"
        val testFile = TestFileHelper.createTempDirBlob(testBaseDir, testApiKey, testBlobName)
        Files.writeString(testFile, testContent)

        val result = subject.readStoredBlob(testApiKey, testBlobName)

        assertEquals(testContent, result)
    }

    @Test
    fun `readStoredBlob - A BlobException is thrown when the blob cannot be found in the user directory`() {
        val testApiKey = ApiKey.generateApiKey()
        TestFileHelper.createTempKeyDir(testBaseDir, testApiKey)

        val result = assertThrows(BlobException::class.java) {
            subject.readStoredBlob(testApiKey, "some-non-existing-blob")
        }

        assertNotNull(result)
        assertEquals("Blob name does not exist", result.message)
    }
}