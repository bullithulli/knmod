package org.bullithulli;
/*
 * Author: BulliThulli
 * Usage: java modder2 --help
 */

import org.bullithulli.feature.knmod;
import org.bullithulli.feature.labelLookup;

import java.util.ArrayList;
import java.util.Arrays;

import static org.bullithulli.feature.labelLookup.*;


public class modder2 {
    public static final String version = "Jan-2024-alpha";
    public static final String eol = "\n";
    public static final String indent = "    ";
    public static String realArgs;
    public knmod knmod;
    boolean isKnMODFeatureRequested = false;
    boolean isLabelLookUpFeatureRequested = false;
    boolean removeFromSourceOnLabelHit = false;
    boolean stopLabelLookUpOnceNewLabelFound = true;
    boolean stopOnNextLabelJump = false;
    boolean followInnerJumps = false;
    boolean followInnerCalls = false;
    int skipLinesUpto = 0;
    String inputFileForRequestedFeature = null;
    String outputFileForRequestedFeature = null;
    String lookupKey = null;
    ArrayList<String> ListOfIgnoreLabels = new ArrayList<>();
    labelLookup lookupLabel;
    boolean followScreenCalls = false;

    public modder2() {
        knmod = new knmod();
        lookupLabel = new labelLookup();
    }


    public static void main(String[] args) throws Exception {
        realArgs = String.join(" ", args);
        if (args.length == 0) {
            System.out.println("No command-line arguments provided.");
            displayHelp();
            System.exit(2);
        }
        modder2 modder = new modder2();
        modder.verifyArgs(args);
        modder.executeArgs();
    }

    public static void displayHelp() {
        System.out.println("Usage: java -jar modder-2.jar [OPTIONS]");
        System.out.println("Options:");
        System.out.println("  -h, --help                Display help information");
        System.out.println("  -v, --version             Display version information");
        System.out.println("  --file=FILENAME           Specify a file");
        System.out.println("  --outfile=FILENAME        Destination output. Defaults to /tmp/out");
        System.out.println("  --feature=FEATURE_NAME    The Feature you want to use. Available, KNMOD,LABEL_LOOKUP");
        System.out.println("                            KNMOD:          mandatory fields: --file; Optional fields: --outfile --skipLinesUpto, --ignoreLines --ignoreLinesContaining --silenceKNMOD_for");
        System.out.println("                                            --skipLinesUpto=INT                     Don't process these lines in the requested feature. Defaults to 0");
        System.out.println("                                            --ignoreLines=startsWith1,startW2       Skips the line that starts with <list>. Default: BULLITULLI-MODDER2");
        System.out.println("                                            --ignoreLinesContaining=word1,word2     Skips the line that contains the words <list>. Default: []");
        System.out.println("                                            --silenceKNMOD_for=startWord1,..        If you dont want to knmod if lines, pass here. Default: []");
        System.out.println("                            LABEL_LOOKUP:   mandatory fields: --file --key; Optional fields: --removefromsource --stopOnNewlabel --stopOnNextLabelJump --followInnerJumps --followInnerCalls --ignoreLabels --followScreenCalls");
        System.out.println("                                            --key=STRING                        Label to Lookup");
        System.out.println("                                            --removefromsource=BOOLEAN          Erase label definition in source file if matches. Defaults to false");
        System.out.println("                                            --stopOnNewlabel=BOOLEAN            Stop lookup label once new label is found. Defaults to true");
        System.out.println("                                            --stopOnNextLabelJump=BOOLEAN       Stop lookup label once new jump is found within definition. Defaults to false");
        System.out.println("                                            --followInnerJumps=BOOLEAN          Continue lookups if innerJumps are found, and proceed same with innerJumps. Defaults to false");
        System.out.println("                                            --followInnerCalls=BOOLEAN          Continue lookups if innerCalls are found, and proceed same with innerCalls labels. Defaults to false");
        System.out.println("                                            --followScreenCalls=BOOLEAN          Continue lookups if innerCalls for screen are found, and proceed same with innerCalls labels. Defaults to false");
        System.out.println("                                            --ignoreLabels=label1,label2        List of Labels to ignore while processing. Defaults to []");
    }

    public void verifyAndExecuteLabelLookupFeature(String lookupKey, String inputFilePathForRequestedFeature, boolean removeFromSourceOnMatch, boolean stopAfterNextLabelFound, boolean stopOnNextLabelJump, boolean followInnerJumps, boolean followInnerCalls, boolean followScreenCalls) {
        boolean forceExit = false;
        if (lookupKey == null) {
            System.err.println("You must pass --key parameter with LABEL_LOOKUP feature");
            forceExit = true;
        }
        if (inputFilePathForRequestedFeature == null) {
            System.err.println("You must pass --file parameter with LABEL_LOOKUP feature");
            forceExit = true;
        }
        if (forceExit) {
            System.exit(2);
        }
        //System.out.println(inputFilePathForRequestedFeature+"  "+lookupKey+"   "+removeFromSourceOnMatch);
        FOUND_HIT = false;
        ROOT_SEARCH_DIR = inputFilePathForRequestedFeature;
        lookupLabel.lookupLabel(stopAfterNextLabelFound, inputFilePathForRequestedFeature, lookupKey.trim(), removeFromSourceOnMatch, stopOnNextLabelJump, followInnerJumps, followInnerCalls, false, followScreenCalls);
        if (!FOUND_HIT) {
            modderSay("LABEL_NOT_FOUND", lookupKey);
        }
    }

    public void verifyAndExecuteKNModFeature(String inputFilePathForRequestedFeature, String destinationFileOPath, int skipLines) throws Exception {
        boolean forceExit = false;
        if (inputFilePathForRequestedFeature == null) {
            System.err.println("You must pass --file parameter with KNMOD feature");
            forceExit = true;
        }
        if (forceExit) {
            System.exit(2);
        }

        if (destinationFileOPath == null) {
            System.err.println("using default destination location of /tmp/out. or pass --outfile");
            destinationFileOPath = "/tmp/out";
        }
        if (skipLines == 0) {
            System.err.println("--skiplines was not passed. defaulted to 0");
        }
        knmod.feature_knmodit(inputFilePathForRequestedFeature, skipLines, destinationFileOPath);
    }

    public void executeArgs() throws Exception {
        if (isKnMODFeatureRequested) {
            verifyAndExecuteKNModFeature(inputFileForRequestedFeature, outputFileForRequestedFeature, skipLinesUpto);
        } else if (isLabelLookUpFeatureRequested) {
            verifyAndExecuteLabelLookupFeature(lookupKey, inputFileForRequestedFeature, removeFromSourceOnLabelHit, stopLabelLookUpOnceNewLabelFound, stopOnNextLabelJump, followInnerJumps, followInnerCalls, followScreenCalls);
        } else {
            System.err.println("No action performed. No feature selected");
        }
    }

    public void verifyArgs(String[] args) {
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                displayHelp();
                System.exit(0);
            } else if (arg.equals("-v") || arg.equals("--version")) {
                System.out.println("KNMOD " + version);
                System.exit(0);
            } else if (arg.startsWith("--feature=")) {
                switch (arg.substring("--feature=".length())) {
                    case "KNMOD" -> isKnMODFeatureRequested = true;
                    case "LABEL_LOOKUP" -> isLabelLookUpFeatureRequested = true;
                    default -> System.err.println("Unknown Feature requested");
                }
            } else if (arg.startsWith("--file=")) {
                inputFileForRequestedFeature = arg.substring("--file=".length());
            } else if (arg.startsWith("--outfile=")) {
                outputFileForRequestedFeature = arg.substring("--outfile=".length());
            } else if (arg.startsWith("--skipLinesUpto=")) {
                String temp = arg.substring("--skipLinesUpto=".length());
                skipLinesUpto = Integer.parseInt(temp);
            } else if (arg.startsWith("--key=")) {
                lookupKey = arg.substring("--key=".length());
            } else if (arg.startsWith("--removefromsource=")) {
                removeFromSourceOnLabelHit = Boolean.parseBoolean(arg.substring("--removefromsource=".length()));
            } else if (arg.startsWith("--stopOnNewlabel=")) {
                stopLabelLookUpOnceNewLabelFound = Boolean.parseBoolean(arg.substring("--stopOnNewlabel=".length()));
            } else if (arg.startsWith("--stopOnNextLabelJump=")) {
                stopOnNextLabelJump = Boolean.parseBoolean(arg.substring("--stopOnNextLabelJump=".length()));
            } else if (arg.startsWith("--followInnerJumps=")) {
                followInnerJumps = Boolean.parseBoolean(arg.substring("--followInnerJumps=".length()));
            } else if (arg.startsWith("--followInnerCalls=")) {
                followInnerCalls = Boolean.parseBoolean(arg.substring("--followInnerCalls=".length()));
            } else if (arg.startsWith("--ignoreLabels=")) {
                ListOfIgnoreLabels.addAll(Arrays.asList(arg.substring("--ignoreLabels=".length()).split(",")));
            } else if (arg.startsWith("--ignoreLines=")) {
                knmod.skipLinesStartsWith_FOR_KNMOD.addAll(Arrays.asList(arg.substring("--ignoreLines=".length()).split(",")));
            } else if (arg.startsWith("--silenceKNMOD_for=")) {
                knmod.silenceKNMOD_for.addAll(Arrays.asList(arg.substring("--silenceKNMOD_for=".length()).split(",")));
            } else if (arg.startsWith("--ignoreLinesContaining=")) {
                knmod.skipLinesThatContainsWord_FOR_KNMOD.addAll(Arrays.asList(arg.substring("--ignoreLinesContaining=".length()).split(",")));
            } else if (arg.startsWith("--followScreenCalls=")) {
                followScreenCalls = Boolean.parseBoolean(arg.substring("--followScreenCalls=".length()));
            } else {
                System.err.println("Unknown parameter " + arg);
                displayHelp();
                System.exit(2);
            }
        }
    }
}
