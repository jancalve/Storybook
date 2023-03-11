package no.janco

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import java.util.function.Consumer

class ChatGPTConnector(private val openAIToken: String) {

    private var token: String = "Not Set";
    init {
        token = openAIToken
    }

    @OptIn(BetaOpenAI::class)
    fun getStory(plot: String, callback: Consumer<String>) {
        val openAI = OpenAI(token)
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = "Write me an epic story about $plot. For each paragraph in the story I want you to write a single paragraph beneath it with a single line containing keywords describing the color and species of all characters in the previous paragraph as well as key items. Do not mention their names. This paragraph should always start with Picture:"
//                    content = "Pretend that I am a 5 year old. Write me a good night story about $plot. For each paragraph in the story I want you to write a single paragraph beneath it with a single line containing keywords describing the color and species of all characters in the previous paragraph as well as key items. Do not mention their names. This paragraph should always start with Picture:"
                )
            )
        )
        runBlocking {
            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            println("Response is $completion.")
            val messageContent = completion.choices[0].message?.content ?: "Error"
            callback.accept(messageContent.toString())
        }
    }

    @OptIn(BetaOpenAI::class)
    fun translateStoryTo(locale: Locale, originalStory: String, callback: Consumer<String>) {
        val openAI = OpenAI(token)
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = "I am going to ask you to translate a story. I want the story to have the same amount of line breaks as the original and you should only write the translated content. Translate the following story to ${locale.chatIdentifier} : $originalStory ."
                )
            )
        )
        runBlocking {
            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            println("Response is $completion.")
            val messageContent = completion.choices[0].message?.content ?: "Error"
            callback.accept(messageContent.toString())
        }
    }

}