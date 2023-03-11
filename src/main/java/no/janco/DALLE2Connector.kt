package no.janco


import kotlinx.coroutines.runBlocking
import java.util.function.BiConsumer

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.client.OpenAI


class DALLE2Connector(private val openAIToken: String) {

    private var token: String = "Not Set";
    init {
        token = openAIToken
    }

    @OptIn(BetaOpenAI::class)
    fun generateImage(storyLine: String, index: Int, callback: BiConsumer<Int, List<String>>) {
        val openAI = OpenAI(token)

        runBlocking {
            val images = openAI.imageURL( // or openAI.imageJSON
                creation = ImageCreation(
                    prompt = "$storyLine | cartoon, 4k, detailed, trending in artstation",
                    n = 3,
                    size = ImageSize.is1024x1024
                )
            )
            val retVal = images.map { it.url }

            callback.accept(index, retVal)
        }
    }
}
