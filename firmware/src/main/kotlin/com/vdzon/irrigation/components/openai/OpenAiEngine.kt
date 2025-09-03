package com.vdzon.irrigation.components.openai

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude


data class AIRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false,
)

//data class Message(val role: String, val content: String)

data class Message(
    val role: String,
    val content: List<ContentPart> // << Nu een lijst i.p.v. String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContentPart(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAIResponse(
    val choices: List<OpenAiChoice>,
    val usage: Usage,
    val model: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAiChoice(
    val message: OpenAiMessage,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAiMessage(
    val role: String,
    val content: String
)
class OpenAiEngine() {
    fun chat(systemPrompt: String, userPrompt: String, imageUrls: List<String> = emptyList(),model: String): String {
        val systemMessage = Message(
            "system",
            listOf(
                ContentPart(type = "text", text = systemPrompt)
            )
        )

        val userContentParts = mutableListOf<ContentPart>()
        userContentParts.add(ContentPart(type = "text", text = userPrompt))

        imageUrls.forEach { url ->
            userContentParts.add(
                ContentPart(
                    type = "image_url",
                    image_url = ImageUrl(url = url)
                )
            )
        }

        val userMessage = Message(
            "user",
            userContentParts
        )

        val request = AIRequest(
            model = model,
            messages = listOf(systemMessage, userMessage)
        )

        //        val request = AIRequest(
//            model = model,
//            messages = listOf(
//                Message("system", systemPrompt),
//                Message("user", userPrompt)
//            ),
//        )
        val apiKey = System.getenv("OPENAI_API_KEY")
        println(apiKey)
        val url = URL("https://api.openai.com/v1/chat/completions")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $apiKey")
        connection.doOutput = true
        val requesJson = jacksonObjectMapper().writeValueAsString(request)
        println("---")
        println("---")
        println(requesJson)
        println("---")
        println("---")
        connection.outputStream.use { it.write(requesJson.toByteArray()) }

        if (connection.responseCode>299){
            val error = connection.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: "Geen foutmelding beschikbaar"
            throw RuntimeException("Error during call to OpenAI, status:${connection.responseCode}, error: $error")
        }

        val responseJson = connection.inputStream.bufferedReader().use(BufferedReader::readText)
        val openAiResponse:OpenAIResponse = jacksonObjectMapper().readValue(responseJson, object : TypeReference<OpenAIResponse>() {})
        println("OpenAI: prompt tokens: ${openAiResponse.usage.prompt_tokens} completion tokens: ${openAiResponse.usage.completion_tokens} total tokens: ${openAiResponse.usage.total_tokens} model: ${openAiResponse.model}")
        val json = openAiResponse?.choices?.firstOrNull()?.message?.content ?: ""
        return json
    }
}