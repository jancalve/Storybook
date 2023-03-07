package no.janco;

import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

public class FFmpegConnector {


    public static void createVideoPart(String imageFilePath, String audioFilePath, String outputFilePath) {
        System.out.println("Creating part video using " + imageFilePath + " and " + audioFilePath);

        String[] cmd = {"ffmpeg", "-y", "-loop", "1", "-i", imageFilePath, "-i", audioFilePath, "-c:v", "libx264", "-c:a", "aac", "-b:a", "192k", "-shortest", outputFilePath};
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("FFMPEG process exited with code " + exitCode);
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to process video part: " + ex.getMessage());
        }

    }

    public static void stitchVideos(ArrayList<String> videoParts, String outputFilePath) {
        System.out.println("Stitching together " + videoParts.size() + " videos..");

        String[] videoFileArgumentsTmp = new String[2 * videoParts.size() + 1];
        videoFileArgumentsTmp[0] = "-i";

        for (int i = 0; i < videoParts.size(); i++) {
            videoFileArgumentsTmp[2 * i + 1] = videoParts.get(i);
            videoFileArgumentsTmp[2 * i + 2] = "-i";
        }
        String[] videoFileArguments = new String[videoFileArgumentsTmp.length - 1];
        System.arraycopy(videoFileArgumentsTmp, 0, videoFileArguments, 0, videoFileArgumentsTmp.length - 1);


        String[] cmd = {"ffmpeg", "-y", "-filter_complex", "[0:v][0:a][1:v][1:a][2:v][2:a]concat=n=" + videoParts.size() + ":v=1:a=1", outputFilePath};
      //  String[] cmd = {"ffmpeg", "-y", "-filter_complex", "[0:v][1:v][2:v]concat=n=3:v=1:a=0[v];[v]vignette=0.3:0.8[vout]", "-map", "[vout]", "-map", "0:a", outputFilePath};
        int insertIndex = 2;
        String[] cmdWithFiles = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.subarray(cmd, 0, insertIndex), videoFileArguments), ArrayUtils.subarray(cmd, insertIndex, cmd.length));


        ProcessBuilder pb = new ProcessBuilder(cmdWithFiles);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("FFMPEG process exited with code " + exitCode);
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to process video part: " + ex.getMessage());
        }


    }
}