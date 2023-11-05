package org.bullithulli.feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.bullithulli.utils.textUtils.*;

public class labelLookup {
    public static boolean FOUND_HIT = false;
    public static String ROOT_SEARCH_DIR = null;
    public LinkedHashSet<String> processedLabels = new LinkedHashSet<>();
    public LinkedHashSet<String> ListOfIgnoreLabels = new LinkedHashSet<>();

    public static void modderSay(String key, String value) {
        System.out.printf("BULLITULLI-MODDER2: -----------------------------------      %s  [%s]   -----------------------------------%n", key, value);
    }

    /**
     * @param stopAfterNextLabel  if true: it tells, lookup should stop when it sees next label. It ignores inner return statements. Defaults true
     * @param sourceDirectory     From which directory searching should start
     * @param labelToFind         Target key to lookup
     * @param removeFromSource    if true, it deletes the label definitions in the source once found
     * @param stopOnNextLabelJump stop creating label definition, if the labels is about to end with "jump" calls. Defaults:  false
     */
    public void lookupLabel(boolean stopAfterNextLabel, String sourceDirectory, String labelToFind, boolean removeFromSource, boolean stopOnNextLabelJump, boolean followInnerJumps, boolean followInnerCalls, boolean isScreenLookup, boolean followInnerScreens) {
        if (processedLabels.contains(labelToFind)) {
            ///Already processed, Ignore revisit
            return;
        }

        if (FOUND_HIT && !(followInnerJumps || followInnerCalls)) {
            return;
        }
        File directory = new File(sourceDirectory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".rpy")) {
                    try {
                        List<String> lines = new ArrayList<>(); //this holds file contents, without its target label definition.
                        LinkedHashSet<String> innerJumpStatements = new LinkedHashSet<>(); //this holds if the target label definition contains any jump instructions
                        LinkedHashSet<String> innerLabelStatements = new LinkedHashSet<>();
                        LinkedHashSet<String> innerCallStatements = new LinkedHashSet<>();
                        LinkedHashSet<String> toVisitLabels = new LinkedHashSet<>();
                        boolean insideLabel = false;
                        boolean labelFound = false;
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String untrimmedline, trimmedLine;
                        //Business Logic to detect labels, calls, jumps and etc, file processing
                        while ((untrimmedline = reader.readLine()) != null) {
                            trimmedLine = untrimmedline.trim();
                            boolean isCurrentLineMatchesLabelKey;
                            if (!isScreenLookup) {
                                isCurrentLineMatchesLabelKey = (untrimmedline.startsWith("label " + labelToFind + ":") || untrimmedline.startsWith("label " + labelToFind + " :"));
                                if (isCurrentLineMatchesLabelKey) {
                                    ListOfIgnoreLabels.add(labelToFind);
                                    ListOfIgnoreLabels.add(labelToFind + "BULLISCREEN");
                                }
                            } else {
                                isCurrentLineMatchesLabelKey = (untrimmedline.startsWith("screen " + labelToFind + "()") || untrimmedline.startsWith("screen " + labelToFind + " ()"));
                                if (isCurrentLineMatchesLabelKey) {
                                    ListOfIgnoreLabels.add(labelToFind);
                                    ListOfIgnoreLabels.add(labelToFind + "BULLISCREEN");
                                }
                            }

                            if (isCurrentLineMatchesLabelKey) { //detect label car: or label car :
                                insideLabel = true;
                                labelFound = true;
                                processedLabels.add(labelToFind);
                                processedLabels.add(labelToFind + "BULLISCREEN");
                                FOUND_HIT = true;
                            } else if (stopAfterNextLabel && insideLabel) { // if it finds new label, stop if user has set stopping option
                                if (!isScreenLookup && untrimmedline.startsWith("label ")) insideLabel = false;
                                if (isScreenLookup && (trimmedLine.length() > 0 && !(untrimmedline.charAt(0) == ' ' || untrimmedline.charAt(0) == '\t') && !untrimmedline.contains(labelToFind) && !untrimmedline.startsWith("#")))
                                    insideLabel = false;
                            }

                            if (insideLabel) {
                                if (trimmedLine.startsWith("label ") && !containsExactWord(trimmedLine.replaceAll(":", ""), labelToFind)) { //detect innerLabels except itself
                                    innerLabelStatements.add(trimmedLine);
                                    ListOfIgnoreLabels.add(trimmedLine.split("\\s+")[1].replaceAll(":", ""));
                                    processedLabels.add(trimmedLine.split("\\s+")[1].replaceAll(":", ""));//add inner labels into processed label, so don't iterate inner labels again
                                } else if (trimmedLine.startsWith("screen ") && !containsExactWord(removeQuotesFromLine(removeBrackets(trimmedLine.replaceAll(":", ""))), labelToFind)) { //detect innerLabels except itself
                                    innerLabelStatements.add(trimmedLine);
                                    ListOfIgnoreLabels.add(removeQuotesFromLine(removeBrackets(trimmedLine.split("\\s+")[1].replaceAll(":", ""))));
                                    processedLabels.add(removeQuotesFromLine(removeBrackets(trimmedLine.split("\\s+")[1].replaceAll(":", ""))));//add inner labels into processed label, so don't iterate inner labels again
                                } else if (trimmedLine.startsWith("jump ") && !containsExactWord(trimmedLine, labelToFind)) {
                                    innerJumpStatements.add(trimmedLine);
                                    if (followInnerJumps) {
                                        toVisitLabels.add(trimmedLine.replaceAll("jump ", "").replaceAll("jump\t", "").trim());
                                    }
                                } else if (trimmedLine.startsWith("action Jump ") && !containsExactWord(trimmedLine, labelToFind)) {
                                    innerJumpStatements.add(trimmedLine);
                                    if (followInnerJumps) {
                                        toVisitLabels.add(removeQuotesFromLine(removeBrackets(trimmedLine.trim().split("\\s+")[2].replaceAll(":", ""))));
                                    }
                                } else if (trimmedLine.startsWith("call ") && !containsExactWord(trimmedLine, labelToFind)) {
                                    innerCallStatements.add(trimmedLine);
                                    if (followInnerCalls) {
                                        if (trimmedLine.startsWith("call screen ")) {
                                            toVisitLabels.add(trimmedLine.trim().split("\\s+")[2] + "BULLISCREEN");
                                        } else {
                                            toVisitLabels.add(trimmedLine.trim().split("\\s+")[1]);
                                        }
                                    }
                                }
                                //Exit Scenarios
                                if (!stopAfterNextLabel && untrimmedline.startsWith("return") || untrimmedline.startsWith("\treturn") || untrimmedline.startsWith("    return") || untrimmedline.startsWith("  return")) {
                                    /*
                                    Detects: label car:
                                                label z:
                                                    return <--not here
                                                return <----here
                                     */
                                    insideLabel = false;
                                } else if (stopOnNextLabelJump && untrimmedline.startsWith("jump") || untrimmedline.startsWith("\tjump") || untrimmedline.startsWith("    jump") || untrimmedline.startsWith("  jump")) {
                                    /*
                                    Detects: label car:
                                                label z:
                                                    jump z2 <--not here
                                                jump xcz <----here
                                     */
                                    insideLabel = false;
                                }
                                System.out.println(untrimmedline); // Print the label content
                            } else {
                                if (removeFromSource)
                                    lines.add(untrimmedline); // add the non label lines into arraylist, later dump that back
                            }
                        }
                        //At this point, A file is fully processed
                        reader.close();

                        if (labelFound) { //if the processed file contained target label
                            // Write the modified content back to the file
                            if (removeFromSource) {
                                Path filePath = file.toPath();
                                Files.write(filePath, lines);
                            }
                            if (innerJumpStatements.size() > 0) { //If the processed label definition has inner jump statements, display to user
                                for (String jumpStatement : innerJumpStatements) {
                                    modderSay("JUMPS_FOUND", jumpStatement);
                                }
                            }
                            if (innerLabelStatements.size() > 0) {
                                for (String innerLabels : innerLabelStatements) {
                                    modderSay("INNER_LABELS_FOUND", innerLabels);
                                }
                            }
                            if (innerCallStatements.size() > 0) {
                                for (String innerCalls : innerCallStatements) {
                                    modderSay("INNER_CALLS_FOUND", innerCalls);
                                }
                            }
                            if (followInnerCalls || followInnerJumps) {
                                for (String label : toVisitLabels) {
                                    if (!ListOfIgnoreLabels.contains(label)) {
                                        if (label.endsWith("BULLISCREEN") && followInnerScreens) {
                                            label = label.replaceAll("BULLISCREEN", "");
                                            lookupLabel(stopAfterNextLabel, ROOT_SEARCH_DIR, label.trim(), removeFromSource, stopOnNextLabelJump, followInnerJumps, followInnerCalls, true, followInnerScreens);
                                        }
                                        lookupLabel(stopAfterNextLabel, ROOT_SEARCH_DIR, label.trim(), removeFromSource, stopOnNextLabelJump, followInnerJumps, followInnerCalls, false, followInnerScreens);
                                    }
                                }
                            }
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    // Recursively search subdirectories
                    lookupLabel(stopAfterNextLabel, file.getAbsolutePath(), labelToFind.trim(), removeFromSource, stopOnNextLabelJump, followInnerJumps, followInnerCalls, isScreenLookup, followInnerScreens);
                }
            }
        }
    }
}
