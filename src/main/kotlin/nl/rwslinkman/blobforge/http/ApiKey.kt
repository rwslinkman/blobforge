package nl.rwslinkman.blobforge.http

object ApiKey {

    const val HEADER = "X-API-Key"
    private val apiKeyValidationRegex = Regex("[a-zA-Z0-9]{32}")

    fun isValidApiKey(input: String): Boolean = apiKeyValidationRegex.matches(input)

    fun generateApiKey() : String {
        val options = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (0..32).map { options.random() }.joinToString()
    }
}