package org.bullithulli;
/*
 * Author: BulliThulli
 * Usage: java modder2 --help
 */

import lombok.extern.slf4j.Slf4j;
import org.bullithulli.feature.KNMod;
import org.bullithulli.feature.labelLookup;
import org.bullithulli.feature.labelReplacer;
import org.bullithulli.feature.translateIt;

import java.util.Arrays;

import static org.bullithulli.feature.labelLookup.FOUND_HIT;
import static org.bullithulli.feature.labelLookup.ROOT_SEARCH_DIR;
import static org.bullithulli.feature.labelLookup.modderSay;

@Slf4j
public class Modder2 {
	public static final String version = "Jan-2025-alpha0.1";
	public static final String eol = "\n";
	public static String realArgs;
	public KNMod KNmod;
	boolean isKnMODFeatureRequested = false;
	boolean isTranslateFeatureRequested = false;
	boolean isLabelLookUpFeatureRequested = false;
	boolean isLabelReplaceFeatureRequested = false;
	boolean removeFromSourceOnLabelHit = false;
	boolean stopLabelLookUpOnceNewLabelFound = true;
	boolean stopOnNextLabelJump = false;
	boolean followInnerJumps = false;
	boolean followInnerCalls = false;
	String startModFromSymbol = null;
	String inputFileForRequestedFeature = null;
	String outputFileForRequestedFeature = null;
	String lookupKey = null;
	String tlFile = null;
	boolean indentTypeTAB = false;
	int indentSize = 4;
	labelLookup lookupLabel;
	boolean followScreenCalls = false;
	String patchFrom = null;
	String replaceBy = null;

	public Modder2() {
		KNmod = new KNMod();
		lookupLabel = new labelLookup();
	}


	public static void main(String[] args) throws Exception {
		realArgs = Arrays.stream(args)
				.map(arg -> {
					if (arg.startsWith("--")) {
						// Split the argument into key and value
						String[] keyValue = arg.split("=", 2);
						if (keyValue.length == 2 && keyValue[1].contains(" ")) {
							// If value contains spaces, wrap it in double quotes
							return keyValue[0] + "=\"" + keyValue[1] + "\"";
						}
						return arg; // Return the original argument if no modification is needed
					}
					return arg; // Return non-`--` arguments as-is
				})
				.reduce((a, b) -> a + " " + b) // Join arguments with spaces
				.orElse("").trim();
		if (args.length == 0) {
			log.info("No command-line arguments provided.");
			displayHelp();
			System.exit(2);
		}

		Modder2 moder = new Modder2();
		moder.verifyArgs(args);
		moder.executeArgs();
	}

	public static void displayHelp() {
		log.info("Usage: java -jar modder-2.jar [OPTIONS]");
		log.info("Options:");
		log.info("  -h, --help                Display help information");
		log.info("  -v, --version             Display version information");
		log.info("  --file=FILENAME           Specify a file");
		log.info("  --outfile=FILENAME        Destination output. Defaults to /tmp/out");
		log.info("  --feature=FEATURE_NAME    The Feature you want to use. Available, KNMOD,LABEL_LOOKUP, LABEL_REPLACE");
		log.info("                            KNMOD:          mandatory fields: --file; Optional fields: --outfile --forceDontKNModForStartsWith --forceKNModForStartsWith");
		log.info("                                            --startModFromSymbol=String         Don't process the lines till it starts with this symbol. Defaults to null");
		log.info("                                            --forceDontKNModForStartsWith=String               Don't KNMOD the symbols. Defaults to null");
		log.info("                                            --forceKNModForStartsWith=String                   did you find new symbols that need to be KNMODed. Add them here");
		log.info("                            LABEL_LOOKUP:   mandatory fields: --file --key; Optional fields: --removefromsource --stopOnNewlabel --stopOnNextLabelJump --followInnerJumps --followInnerCalls  --followScreenCalls");
		log.info("                                            --key=STRING                        Label to Lookup");
		log.info("                                            --removefromsource=BOOLEAN          Erase label definition in source file if matches. Defaults to false");
		log.info("                                            --stopOnNewlabel=BOOLEAN            Stop lookup label once new label is found. Defaults to true");
		log.info("                                            --stopOnNextLabelJump=BOOLEAN       Stop lookup label once new jump is found within definition. Defaults to false");
		log.info("                                            --followInnerJumps=BOOLEAN          Continue lookups if innerJumps are found, and proceed same with innerJumps. Defaults to false");
		log.info("                                            --followInnerCalls=BOOLEAN          Continue lookups if innerCalls are found, and proceed same with innerCalls labels. Defaults to false");
		log.info("                                            --followScreenCalls=BOOLEAN         Continue lookups if innerCalls for screen are found, and proceed same with innerCalls labels. Defaults to false");
		log.info("                                            --ignoreLabels=label1,label2        List of Labels to ignore while processing. Defaults to []");
		log.info("                            LABEL_REPLACE:   mandatory fields: --file --patchFrom --replaceBy; Optional fields: --outfile --indentType --indentSize");
		log.info("                                             --patchFrom=/path/toFile           A patch rpy file where you want to patch the source file");
		log.info("                                             --replaceBy=LIST[STR->STR]         A list of labels you want to patch, eg. --replaceBy=labelA->labelPatchA,labelB->labelPatchB. Defaults to []");
		log.info("                                             --indentType=SPACE|TAB             Can be either Space or Tab. It informs the parser how the code is structured. Defaults to Space");
		log.info("                                             --indentSize=INT                   It says, how much spaces are there for single indent, supply this if you are passing --indentTyp=SPACE. Defaults to 4");
		log.info("                            TRANSLATE_RPY:   mandatory fields: --file --tlFile; Optional fields: --outfile --indentType --indentSize");
		log.info("                                             --tlFile=/path/toFile              A translation rpy file where it contains translations");
		log.info("                                             --indentType=SPACE|TAB             Can be either Space or Tab. It informs the parser how the code is structured. Defaults to Space");
		log.info("                                             --indentSize=INT                   It says, how much spaces are there for single indent, supply this if you are passing --indentTyp=SPACE. Defaults to 4");
	}

	public void verifyAndExecuteLabelLookupFeature(String lookupKey, String inputFilePathForRequestedFeature, boolean removeFromSourceOnMatch, boolean stopAfterNextLabelFound, boolean stopOnNextLabelJump, boolean followInnerJumps, boolean followInnerCalls, boolean followScreenCalls) {
		boolean forceExit = false;
		if (lookupKey == null) {
			log.error("You must pass --key parameter with LABEL_LOOKUP feature");
			forceExit = true;
		}
		if (inputFilePathForRequestedFeature == null) {
			log.error("You must pass --file parameter with LABEL_LOOKUP feature");
			forceExit = true;
		}
		if (forceExit) {
			System.exit(2);
		}
		//log.info();(inputFilePathForRequestedFeature+"  "+lookupKey+"   "+removeFromSourceOnMatch);
		FOUND_HIT = false;
		ROOT_SEARCH_DIR = inputFilePathForRequestedFeature;
		lookupLabel.lookupLabel(stopAfterNextLabelFound, inputFilePathForRequestedFeature, lookupKey.trim(), removeFromSourceOnMatch, stopOnNextLabelJump, followInnerJumps, followInnerCalls, false, followScreenCalls);
		if (!FOUND_HIT) {
			modderSay("LABEL_NOT_FOUND", lookupKey);
		}
	}

	public void verifyAndExecuteKNModFeature(String inputFilePathForRequestedFeature, String destinationFileOPath, String startModFromSymbol) throws Exception {
		boolean forceExit = false;
		if (inputFilePathForRequestedFeature == null) {
			log.error("You must pass --file parameter with KNMOD feature");
			forceExit = true;
		}
		if (forceExit) {
			System.exit(2);
		}

		if (destinationFileOPath == null) {
			log.error("using default destination location of /tmp/out. or pass --outfile");
			destinationFileOPath = "/tmp/out";
		}
		if (startModFromSymbol == null) {
			log.error("--startModFromSymbol was not passed. defaulted to null");
		}
		KNmod.convertRenPyToKineticNovel(inputFilePathForRequestedFeature, startModFromSymbol, destinationFileOPath);
	}

	public void verifyAndExecuteLabelReplaceFeature(String sourceFile, String patchFile, String destinationFile, String listOfLabels, boolean isTabIntended, int spaceSize) throws Exception {
		// TODO: 1/15/24 Write UnitTests
		boolean forceExit = false;
		if (sourceFile == null) {
			log.error("You must pass --file parameter with LABEL_REPLACE feature");
			forceExit = true;
		}
		if (patchFile == null) {
			log.error("You must pass --patchFile parameter with LABEL_REPLACE feature");
			forceExit = true;
		}
		if (listOfLabels == null) {
			log.error("You must pass --replaceBy parameter with LABEL_REPLACE feature");
			forceExit = true;
		}
		if (forceExit) {
			System.exit(2);
		}

		if (destinationFile == null) {
			log.error("using default destination location of /tmp/out. or pass --outfile");
			destinationFile = "/tmp/out";
		}
		new labelReplacer().replace(sourceFile, patchFile, destinationFile, listOfLabels, isTabIntended, spaceSize);
	}

	public void verifyAndExecuteTranslateFeature(String sourceFile, String tlFile, String destinationFile, boolean isTabIntended, int spaceSize) throws Exception {
		// TODO: 1/15/24 Write UnitTests
		boolean forceExit = false;
		if (sourceFile == null) {
			log.error("You must pass --file parameter with TRANSLATE_RPY feature");
			forceExit = true;
		}
		if (tlFile == null) {
			log.error("You must pass --tlFile parameter with TRANSLATE_RPY feature");
			forceExit = true;
		}
		if (forceExit) {
			System.exit(2);
		}

		if (destinationFile == null) {
			log.error("using default destination location of /tmp/out. or pass --outfile");
			destinationFile = "/tmp/out";
		}
		new translateIt().generateTranslatedScript(sourceFile, tlFile, destinationFile, isTabIntended, spaceSize);
	}

	public void executeArgs() throws Exception {
		if (isKnMODFeatureRequested) {
			verifyAndExecuteKNModFeature(inputFileForRequestedFeature, outputFileForRequestedFeature, startModFromSymbol);
		} else if (isLabelLookUpFeatureRequested) {
			verifyAndExecuteLabelLookupFeature(lookupKey, inputFileForRequestedFeature, removeFromSourceOnLabelHit, stopLabelLookUpOnceNewLabelFound, stopOnNextLabelJump, followInnerJumps, followInnerCalls, followScreenCalls);
		} else if (isLabelReplaceFeatureRequested) {
			verifyAndExecuteLabelReplaceFeature(inputFileForRequestedFeature, patchFrom, outputFileForRequestedFeature, replaceBy, indentTypeTAB, indentSize);
		} else if (isTranslateFeatureRequested) {
			verifyAndExecuteTranslateFeature(inputFileForRequestedFeature, tlFile, outputFileForRequestedFeature, indentTypeTAB, indentSize);
		} else {
			log.error("No action performed. No feature selected");
		}
	}

	public void verifyArgs(String[] args) {
		for (String arg : args) {
			if (arg.equals("-h") || arg.equals("--help")) {
				displayHelp();
				System.exit(0);
			} else if (arg.equals("-v") || arg.equals("--version")) {
				log.info("KNMOD " + version);
				System.exit(0);
			} else if (arg.startsWith("--feature=")) {
				switch (arg.substring("--feature=".length())) {
					case "KNMOD" -> isKnMODFeatureRequested = true;
					case "LABEL_LOOKUP" -> isLabelLookUpFeatureRequested = true;
					case "LABEL_REPLACE" -> isLabelReplaceFeatureRequested = true;
					case "TRANSLATE_RPY" -> isTranslateFeatureRequested = true;
					default -> log.error("Unknown Feature requested");
				}
			} else if (arg.startsWith("--file=")) {
				inputFileForRequestedFeature = arg.substring("--file=".length());
			} else if (arg.startsWith("--outfile=")) {
				outputFileForRequestedFeature = arg.substring("--outfile=".length());
			} else if (arg.startsWith("--startModFromSymbol=")) {
				startModFromSymbol = arg.substring("--startModFromSymbol=".length());
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
			} else if (arg.startsWith("--forceKNModForStartsWith=")) {
				KNMod.forceKNModForStartsWith.addAll(Arrays.asList(arg.substring("--forceKNModForStartsWith=".length()).split(",")));
			} else if (arg.startsWith("--forceDontKNModForStartsWith=")) {
				KNmod.forceDontKNModForStartsWith.addAll(Arrays.asList(arg.substring("--forceDontKNModForStartsWith=".length()).split(",")));
			} else if (arg.startsWith("--followScreenCalls=")) {
				followScreenCalls = Boolean.parseBoolean(arg.substring("--followScreenCalls=".length()));
			} else if (arg.startsWith("--indentSize=")) {
				indentSize = Integer.parseInt(arg.substring("--indentSize=".length()));
			} else if (arg.startsWith("--replaceBy=")) {
				replaceBy = arg.substring("--replaceBy=".length());
			} else if (arg.startsWith("--patchFrom=")) {
				patchFrom = arg.substring("--patchFrom=".length());
			} else if (arg.startsWith("--tlFile=")) {
				tlFile = arg.substring("--tlFile=".length());
			} else if (arg.startsWith("--indentType=")) {
				switch (arg.substring("--indentType=".length()).toUpperCase()) {
					case "TAB" -> indentTypeTAB = true;
					case "SPACE" -> indentTypeTAB = false;
					default -> {
						log.error("Unknown indent Type " + arg.substring("--indentType=".length()));
						System.exit(2);
					}
				}
			} else {
				log.error("Unknown parameter " + arg);
				displayHelp();
				System.exit(2);
			}
		}
	}
}
