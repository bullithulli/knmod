package org.bullithulli;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class F95TabelFormater {

    private static final Logger logger = LoggerFactory.getLogger(F95TabelFormater.class);
    private static final String INPUT_FILE_PATH = "/tmp/input.txt";
    private static final String OUTPUT_FILE_PATH = "/tmp/output.txt";
    public static int totalGames = 0;

    public static void main(String[] args) {
        processFiles(INPUT_FILE_PATH, OUTPUT_FILE_PATH);
    }

    private static void processFiles(String inputFilePath, String outputFilePath) {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            boolean captureCellData = false;
            int CurrentColumnNumber = 0;
            int currentGameCount = 0;
            String gameName = "", genre = "", version = "", review = "", stars = "", status = "", image = "", gameURL = "";
            StringBuilder cellData = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StringUtils.isNotEmpty(line)) {
//                    logger.info(line);
                    line = line.trim();

                    if (line.startsWith("[TABLE]") || line.startsWith("[/TABLE]")) {
                        writer.write(line);
                        writer.newLine();
                        continue;
                    }

                    if (line.startsWith("[TR]")) {
                        if (currentGameCount == 0) {
                            writer.write(line);
                            writer.newLine();
                            currentGameCount++;
                            continue;
                        } else {
                            if (currentGameCount == 4) {
                                writer.write("[/TR]\n[TR]");
                                writer.newLine();
                                currentGameCount = 1;
                            } else {
                                currentGameCount++;
                            }
                            continue;
                        }
                    }

                    if (line.startsWith("[/TR]")) {
                        continue;
                    }

                    if (line.startsWith("[TD]")) {
                        captureCellData = true;
                        cellData = new StringBuilder();
                    }

                    if (line.endsWith("[/TD]")) {
                        captureCellData = false;
                        String cellFullData = cellData.append(line).append(System.lineSeparator()).toString();
                        cellFullData = cellFullData.replaceAll("\\[TD]", "").replaceAll("\\[/TD]", "");
                        if (CurrentColumnNumber == 0) {
                            gameName = cellFullData.trim();
//                            gameURL=gameName.substring(gameName.indexOf("\\[URL]") + 7, gameName.indexOf("']")).trim();
//                            gameName=gameName.substring(gameName.indexOf("']") + 2, gameName.indexOf("[/URL]")).trim();

                            gameName = gameName.replaceAll("\n", " ");
                            gameURL = gameURL.replaceAll("\n", "-");
                            CurrentColumnNumber++;
                        } else if (CurrentColumnNumber == 1) {
                            genre = cellFullData.trim();
                            genre = genre.replaceAll("\n", " ");
                            CurrentColumnNumber++;
                        } else if (CurrentColumnNumber == 2) {
                            version = cellFullData.trim();
                            version = version.replaceAll("\n", " ");
                            CurrentColumnNumber++;
                        } else if (CurrentColumnNumber == 3) {
                            review = cellFullData.trim();
                            review = review.replaceAll("\\[SPOILER=\".*\"]", "").replaceAll("\\[/SPOILER]", "");
                            try {
                                image = review.substring(review.indexOf("[IMG]") + 5, review.indexOf("[/IMG]")).trim();
                            } catch (Exception e) {
                                image = null;
                            }
                            if (image == null) {
                                review = review;
                            } else {
                                review = review.substring(0, review.indexOf("[IMG]")).trim();
                            }

                            review = review.replaceAll("\n", " ");
                            if (image != null) {
                                image = image.replaceAll("\n", " ");
                            }
                            if (review.contains("SPOILER")) {
                                throw new Exception("SPOILER found in review: " + line);
                            }
                            CurrentColumnNumber++;
                        } else if (CurrentColumnNumber == 4) {
                            stars = cellFullData.trim();
                            stars = stars.replaceAll("\n", " ");
                            CurrentColumnNumber++;
                        } else if (CurrentColumnNumber == 5) {
                            status = cellFullData.trim();
                            CurrentColumnNumber = 0;
                            status = status.replaceAll("\n", " ");
                            writeToFile(writer, gameName, genre, version, review, stars, status, image, gameURL);
                        }
                    }

                    if (captureCellData) {
                        cellData.append(line).append(System.lineSeparator());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error processing files", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Total Games: {}", totalGames);
    }

    public static void writeToFile(BufferedWriter writer, String gameName, String genre, String version, String review, String stars, String status, String image, String gameURL) throws IOException {

        totalGames++;
        logger.info("{}. Nmae {}", totalGames, gameName);
        if (image != null) {
            writer.write("[TD][IMG]" + image + "[/IMG]");
        } else {
            writer.write("[TD]");
        }
        writer.newLine();
        writer.write("[RIGHT]" + stars);
        writer.newLine();
        writer.write(gameName);
        writer.newLine();
        writer.write(genre);
        writer.newLine();
        writer.write("v" + version + " [" + status + "][/RIGHT]");
        writer.newLine();
        writer.write("[CENTER][SPOILER=\"Review\"]" + review + "[/SPOILER][/CENTER][/TD]");
        writer.newLine();
    }
}