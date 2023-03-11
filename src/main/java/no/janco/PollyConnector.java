package no.janco;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;

import java.io.IOException;
import java.io.InputStream;


public class PollyConnector {

    private final AmazonPollyClient polly;

    public PollyConnector(Region region) {
        // TODO -- externalize credentials
        polly = new AmazonPollyClient(new DefaultAWSCredentialsProviderChain(),
                new ClientConfiguration());
        polly.setRegion(region);
    }

    public InputStream synthesize(String text, OutputFormat format, Locale locale) throws IOException {

        SynthesizeSpeechRequest synthReq =
                new SynthesizeSpeechRequest().withText(text).withVoiceId(locale.getVoiceId())
                        .withEngine("neural").withOutputFormat(format);
        SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

        return synthRes.getAudioStream();
    }


}
