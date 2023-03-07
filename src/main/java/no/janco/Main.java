package no.janco;

import com.amazonaws.regions.Regions;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.amazonaws.regions.Region;
import com.amazonaws.services.polly.model.OutputFormat;
import org.apache.commons.lang3.StringUtils;


import static no.janco.Locales.*;

public class Main {

    private static final ChatGPTConnector chatGPTConnector = new ChatGPTConnector();

    public static void main(String[] args) {
        //String plot = "a green turtle, a brown fox and a yellow duck who go on a quest and become best friends.";
        String plot = "a super hacker named Frank and his master JC. Frank is on a quest to track down russian super nerd hackers with his sidekick Lester. They travel all around the world while being mentored by their super guru friend JC. All characters are male cacausians in the 30s and all have short, dark hair. The story should have a lot of dark and morbid humor in it and a lot of action.";

        chatGPTConnector.getStory(plot, new Consumer<String>() {
            @Override
            public void accept(String originalStory) {
                System.out.println("The original story is: " + originalStory);
                ArrayList<String> storyLines = extractStoryLines(originalStory);
                ArrayList<String> pictureLines = extractPictureLines(originalStory);
                System.out.println("The story is: " + String.join("\n", storyLines));
                System.out.println("The story pictures is: " + String.join("\n", pictureLines));
                if (storyLines.size() != pictureLines.size()) {
                    throw new RuntimeException("Wrong amount of picture and story lines!");
                }


                generateStoryImages(pictureLines);
                writeStoryToFiles(storyLines, LOCALE_ENGLISH);
                generateVoiceForStory(storyLines, LOCALE_ENGLISH);
                generateVideoForStory(storyLines.size(), LOCALE_ENGLISH);
                translateStoryToOtherLanguages(originalStory, List.of(LOCALE_NORWEGIAN));
                //translateStoryToOtherLanguages(originalStory, ALL);
            }
        });
    }

    private static void translateStoryToOtherLanguages(String originalStory, List<Locale> locales) {

        for (Locale currentLocale : locales) {

            chatGPTConnector.translateStoryTo(currentLocale, originalStory, new Consumer<String>() {
                @Override
                public void accept(String messageContent) {
                    System.out.println("The translated story is: " + messageContent);
                    ArrayList<String> storyLines = extractStoryLines(messageContent);

                    writeStoryToFiles(storyLines, currentLocale);
                    generateVoiceForStory(storyLines, currentLocale);
                    generateVideoForStory(storyLines.size(), currentLocale);
                }
            });
        }

    }

    private static ArrayList<String> extractStoryLines(String messageContent) {
        ArrayList<String> storyLines = new ArrayList<>();
        String rawLines[] = messageContent.split("\\n");
        for(String line: rawLines) {
            if (!line.trim().isEmpty() && !line.startsWith("Picture:")) {
                storyLines.add(StringUtils.replace(line, "Picture", ""));
            }
        }
        return storyLines;
    }

    private static ArrayList<String> extractPictureLines(String messageContent) {
        ArrayList<String> pictureLines = new ArrayList<>();
        String rawLines[] = messageContent.split("\\n");
        for(String line: rawLines) {
            if (!line.trim().isEmpty() && line.startsWith("Picture:")) {
                pictureLines.add(StringUtils.replace(line, "Picture", ""));
            }
        }
        return pictureLines;
    }

    private static void generateVideoForStory(int size, Locale videoLocale) {
        ArrayList<String> videoParts = new ArrayList<>();
        for (int i = 0; i < size; i = i + 1) {
            FFmpegConnector.createVideoPart("./generated/" + i + ".png",
                    "./generated/" + i + "_" + videoLocale.getCountry() + ".mp3",
                    "./generated/" + i + "_" + videoLocale.getCountry() + ".mp4");

            videoParts.add("./generated/" + i + "_" + videoLocale.getCountry() + ".mp4");
        }
        FFmpegConnector.stitchVideos(videoParts, "./generated/output_" + videoLocale.getCountry() + ".mp4");

    }

    private static void generateVoiceForStory(ArrayList<String> storyLines, Locale voiceLocale) {
        PollyConnector pollyConnector = new PollyConnector(Region.getRegion(Regions.DEFAULT_REGION));
        for (int i = 0; i < storyLines.size(); i = i + 1) {
            try {
                InputStream generatedVoice = pollyConnector.synthesize(storyLines.get(i), OutputFormat.Mp3, voiceLocale);
                System.out.println("Generated voice for line " + i);
                saveInputStreamToFile(generatedVoice, "./generated/" + i + "_" +  voiceLocale.getCountry() + ".mp3");

            } catch (IOException ioe) {
                System.out.println("Something went wrong.." + ioe.getMessage());
            }
        }
    }

    private static void generateStoryImages(ArrayList<String> storyLines) {
        DALLE2Connector dalle2Connector = new DALLE2Connector();

        for (int i = 0; i < storyLines.size(); i = i + 1) {
            dalle2Connector.generateImage(storyLines.get(i), i, new BiConsumer<>() {
                @Override
                public void accept(Integer index, List<String> imageURIs) {
                    try {
                        String imageUrl = showImages(imageURIs);

                        URL url = new URL(imageUrl);
                        String filename = "./generated/" + index + ".png";
                        Path path = Paths.get(filename);
                        Files.copy(url.openStream(), path, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Generated image for story line " + index);
                    } catch (IOException e) {
                        System.err.println("Failed to download or save image for index " + index);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static String showImages(List<String> imageURIs) {
        Scanner scanner = new Scanner(System.in);

        String joinedString = String.join(" - ", imageURIs);
        System.out.println("Which image should be used from these images? " + joinedString);
        String input = scanner.nextLine();
        int chosenImageIndex = Integer.parseInt(input);
        if (chosenImageIndex < 0 || chosenImageIndex > imageURIs.size()) {
            System.out.println("Invalid index! Using first");
            chosenImageIndex = 0;
        }

        return imageURIs.get(chosenImageIndex);
    }


    private static void writeStoryToFiles(ArrayList<String> storyLines, Locale locale) {
        for (int i = 0; i < storyLines.size(); i++) {
            String filename = "./generated/" + i + "_" + locale.getCountry() + ".txt";
            try {
                FileWriter writer = new FileWriter(filename);
                writer.write(storyLines.get(i));
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to write to file " + filename);
                e.printStackTrace();
            }
        }
    }

    public static void saveInputStreamToFile(InputStream inputStream, String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
    }


}