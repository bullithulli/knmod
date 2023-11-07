package org.bullithulli.feature;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static org.bullithulli.modder2.*;
import static org.bullithulli.utils.fileUtils.writeLineToDisk;
import static org.bullithulli.utils.modUtils.knmodSay;
import static org.bullithulli.utils.textUtils.countLeadingWhitespaces;

public class knmod {

    public ArrayList<String> skipLinesStartsWith_FOR_KNMOD = new ArrayList<>();
    public ArrayList<String> skipLinesThatContainsWord_FOR_KNMOD = new ArrayList<>();

    public knmod() {
        skipLinesStartsWith_FOR_KNMOD.add("BULLITULLI-MODDER2:");
        skipLinesThatContainsWord_FOR_KNMOD.add("renpy.block_rollback()");
        skipLinesThatContainsWord_FOR_KNMOD.add("renpy.quit");
        skipLinesThatContainsWord_FOR_KNMOD.add("renpy.call");
    }

    /**
     * Detecting
     * case1:   label car:
     * case2:   label car: #some comments
     *
     * @param trimmedLine, pass the trimmedLine to verify
     * @return returns true or false
     */
    public boolean isBeginningOfFunctionBlock(String trimmedLine) {
        // label car:
        if (trimmedLine.endsWith(":")) {
            return true;
        }
        if (trimmedLine.contains(":") && trimmedLine.contains("#")) {
            //            else: ### ERROR: else
            //temp stores=else: ##
            String temp = trimmedLine.substring(0, trimmedLine.lastIndexOf("#")); //get string without comments
            // label car: #somecomments
            return temp.lastIndexOf(':') < trimmedLine.lastIndexOf('#');
        }
        return false;
    }

    /**
     * Writes into destination folder /tmp/out
     *
     * @param sourceFilePath  reads this file and silences any control statements (like if, else, jump etc.) and writes to destination
     * @param skippableLines  If User wants to skip or don't modify first n lines
     * @param destinationPath Destination file name, to dump the modified file.
     */
    public void feature_knmodit(String sourceFilePath, int skippableLines, String destinationPath) throws Exception {
        Scanner sc = new Scanner(new File(sourceFilePath));
        FileWriter fw = new FileWriter(destinationPath);
        String untrimmedline;
        String trimmedLine;
        boolean insideFunctionBlockThatShouldRun = false;
        int parentFunctionBlockWhiteSpaceSize = 0;
        int lineNumber = 1;
        boolean forceSkipSection = false;

        writeLineToDisk("define KN_MOD = Character(\"KN_MOD\", color=\"#ff0000\")", fw);
        writeLineToDisk("# java -jar modder-2.jar " + realArgs, fw);
        writeLineToDisk("# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/", fw);
        writeLineToDisk("# modded by modder2" + version + " program. Created by BulliThulli", fw);
        EXTERNAL_WHILE:
        while (sc.hasNextLine()) {
            untrimmedline = sc.nextLine();
            if (lineNumber <= skippableLines) {
                writeLineToDisk(untrimmedline, fw);
                lineNumber++;
                continue;
            }
            trimmedLine = untrimmedline.trim();
            //Handle some Non echo-ables
            if (trimmedLine.length() <= 0) {  // allow single characters, e.g. animation time variables
                continue;
                //image x:
                //5 <----------here
                //scene x2
            }
            for (String skippy : skipLinesStartsWith_FOR_KNMOD) {
                if (trimmedLine.startsWith(skippy)) {
                    continue EXTERNAL_WHILE;
                }
            }
            for (String skippy : skipLinesThatContainsWord_FOR_KNMOD) {
                if (trimmedLine.contains(skippy)) {
                    continue EXTERNAL_WHILE;
                }
            }
            //Detect some section that you don't want to mess up
            // example
            //init python:
            //      print("hello world")
            if (untrimmedline.startsWith("while ") || untrimmedline.startsWith("python:") || untrimmedline.startsWith("python :") || untrimmedline.startsWith("init python:") || untrimmedline.startsWith("screen ") || (untrimmedline.startsWith("init ") && untrimmedline.endsWith(":"))) {
                writeLineToDisk(untrimmedline, fw);
                parentFunctionBlockWhiteSpaceSize = 0;
                insideFunctionBlockThatShouldRun = true;
                forceSkipSection = true;
                continue;
            }


            if (trimmedLine.startsWith("return") || trimmedLine.startsWith("jump ") || trimmedLine.startsWith("call ") || trimmedLine.contains("renpy.quit")) {
                writeLineToDisk(knmodSay(trimmedLine), fw);
                continue;
            }

            // label car:
            // images car:
            // Handle functionBlock
            if (isBeginningOfFunctionBlock(trimmedLine) && !forceSkipSection) {
                //Non echoble
                if ((trimmedLine.startsWith("if ") || trimmedLine.startsWith("if:")) || (trimmedLine.startsWith("else ") || trimmedLine.startsWith("else:")) || (trimmedLine.startsWith("elif ") || trimmedLine.startsWith("elif:")) || trimmedLine.startsWith("label ") || (trimmedLine.startsWith("menu ") || trimmedLine.startsWith("menu(") || trimmedLine.startsWith("menu:")) || trimmedLine.startsWith("\"")) {
                    parentFunctionBlockWhiteSpaceSize = 0;
                    insideFunctionBlockThatShouldRun = false;
                    writeLineToDisk(knmodSay(trimmedLine), fw);
                    continue;
                }
                //Echoble
                //show car: <----------here
                //  xpos red
                parentFunctionBlockWhiteSpaceSize = countLeadingWhitespaces(untrimmedline);
                insideFunctionBlockThatShouldRun = true;
                writeLineToDisk(trimmedLine, fw);
                continue;
            } else {
                if (insideFunctionBlockThatShouldRun) {
                    /*
                     * Detecting like this
                     * car:
                     *      xpos 65 <----------here
                     */
                    int childFunctionBlockWhiteSpaceSize = countLeadingWhitespaces(untrimmedline);
                    if (childFunctionBlockWhiteSpaceSize > parentFunctionBlockWhiteSpaceSize) {
                        if (forceSkipSection) {
                            //Write true lines, don't indent anything,  cuz who knows what py code developer might have written
                            writeLineToDisk(untrimmedline, fw);
                        } else {
                            writeLineToDisk(indent + trimmedLine, fw);
                        }
                        continue;
                    } else {
                        /*
                         * car:
                         *      xpos 65
                         * Anwar "hello" <--------here
                         */
                        writeLineToDisk(trimmedLine, fw);
                        parentFunctionBlockWhiteSpaceSize = 0;
                        insideFunctionBlockThatShouldRun = false;
                        forceSkipSection = false;
                        continue;
                    }
                } else {
                    // Handling Casual lines
                    // Anwar "hi there! "
                    writeLineToDisk(trimmedLine, fw);
                }
            }
        }
        //A file is processed here, add some signatures
        writeLineToDisk(knmodSay("ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"), fw);
        writeLineToDisk(knmodSay("modded by modder2 " + version + " program. Created by BulliThulli"), fw);
        fw.close();
    }
}
