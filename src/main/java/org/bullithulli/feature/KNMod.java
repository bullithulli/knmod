package org.bullithulli.feature;

import lombok.extern.slf4j.Slf4j;
import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpyGenericNonBlockSymbol;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpySpeakerText;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.utils.parserUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bullithulli.modder2.realArgs;
import static org.bullithulli.modder2.version;
import static org.bullithulli.utils.fileUtils.writeLineToDisk;
import static org.bullithulli.utils.modUtils.knmodSay;

@Slf4j
public class KNMod {
	protected static final List<String> retianBlock = new ArrayList<>(); //list of blocks where KNMOD doesnt apply to it
	public static final List<String> forceKNModForStartsWith = new ArrayList<>();//list of blocks where KNMOD forces it
	public final List<String> forceDontKNModForStartsWith = new ArrayList<>();//list of blocks where KNMOD forces it
	public final List<String> forceDontKNModFor = new ArrayList<>();//list of words where KNMOD forces it, eaxt words
	private static final char[] REMOVE_CHARACTERS = {'"', '\'', '[', ']', '{', '(', ')'}; // Characters to remove

	// Constructor to initialize default rules.
	public KNMod() {
		retianBlock.addAll(Arrays.asList("python", "define", "style", "screen", "image", "scene", "show", "init", "class", "transform")); //you dont want to KNMOD show block
		forceKNModForStartsWith.addAll(Arrays.asList("return ", "call", "menu", "renpy.quit", "renpy.call", "renpy.block_rollback", "if ", "else ", "elif ", "label", "show screen ", "(("));
		forceDontKNModForStartsWith.addAll(Arrays.asList("label start"));
		forceDontKNModFor.addAll(Arrays.asList("or", "and"));
	}

	/**
	 * Escapes special characters (quotes, brackets, etc.) in a string so that it can be safely embedded in Python code.
	 *
	 * @param input The input string to escape.
	 * @return A fully escaped string.
	 */
	private static String escapeStringForPython(String input) {
		if (input == null) {
			return "";
		}
		return replaceCharactersWithEmpty(input
				.replace("\\", "\\\\")  // Escape backslashes
				.replace("\n", "\\n")   // Escape newlines
				.replace("\t", "\\t")   // Escape tabs
				.replace("\r", "\\r"));
	}

	/**
	 * Replaces each character in the given array with an empty string in the provided input.
	 *
	 * @param input The string to process.
	 * @return The modified string with characters removed.
	 */
	private static String replaceCharactersWithEmpty(String input) {
		for (char c : KNMod.REMOVE_CHARACTERS) {
			input = input.replace(String.valueOf(c), "");
		}
		return input;
	}

	/**
	 * Writes the transformed Ren'Py script to the destination folder.
	 *
	 * @param sourceFilePath   Path of the source RenPy file.
	 * @param startModdingFrom renpy symbol from where modding has to happen
	 * @param destinationPath  Destination file path for the output.
	 * @throws Exception If file IO operations fail.
	 */
	public void convertRenPyToKineticNovel(String sourceFilePath, String startModdingFrom, String destinationPath) throws Exception {
		File sourceFile = new File(sourceFilePath);
		FileWriter fw = new FileWriter(destinationPath);
		// Write header lines to the destination file.
		writeLineToDisk("define KN_MOD = Character(\"KN_MOD\", color=\"#ff0000\")", fw);
		writeLineToDisk("# java -jar modder-2.jar " + realArgs, fw);
		writeLineToDisk("# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/", fw);
		writeLineToDisk("# modded by modder2" + version + " program. Created by BulliThulli", fw);

		parser rpyParser = new parser();
		log.info("parsing......");
		renpySymbol rootSymbol = rpyParser.parseFrom(sourceFile, false, 4, false, null); // Parsed tree starting from root.
		log.info("modding......");
//		log.info("Processing %s...%n", rootSymbol.getChainString(0, -1, true, true).trim());
		writeLineToDisk(processSymbolHierarchy(rootSymbol, startModdingFrom), fw);


		// Write footer lines at the end of the file.
		writeLineToDisk(knmodSay("ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"), fw);
		writeLineToDisk(knmodSay("modded by modder2 " + version + " program. Created by BulliThulli"), fw);
		writeLineToDisk("python:\n" +
				"    renpy.input(\"Ignore this box. It is just added by me to verify if you reached the end of the game\", length=32)", fw);
		fw.close();
		log.info("[completed]");
	}

	/**
	 * Recursively processes the symbol hierarchy and writes transformed content into the output file.
	 *
	 * @param symbol         Current `renpySymbol` being processed.
	 * @param skippableLines Number of lines to skip initially.
	 * @return The updated number of processed lines.
	 */
	private String processSymbolHierarchy(renpySymbol symbol, String skippableLines) {
		renpySymbol currentPointedsymbol = symbol;
		boolean doKnmod = false;
		boolean startModding = skippableLines == null;
		while (currentPointedsymbol.getCHAIN_CHILD_SYMBOL() != null) {
			currentPointedsymbol = currentPointedsymbol.getCHAIN_CHILD_SYMBOL();
			if (!startModding) {
				if (currentPointedsymbol.getRENPY_TRIMMED_LINE().startsWith(skippableLines)) {
					startModding = true;
				} else {
					continue;
				}
			}

			if (currentPointedsymbol.isBlockSymbol() && retianBlock.stream().anyMatch(currentPointedsymbol.getRENPY_TRIMMED_LINE()::startsWith)) {
				renpySymbol temp = currentPointedsymbol.getLastChainSymbol(currentPointedsymbol.getHIERARCHY_LEVEL(), false, false, false, false, currentPointedsymbol);
				ArrayList<renpySymbol> patchesChildChainSymbols = currentPointedsymbol.getChainSymbols(currentPointedsymbol.getHIERARCHY_LEVEL(), false, false, false, false, currentPointedsymbol);
				parserUtils.makeBlockHierarchyStartFromZero(currentPointedsymbol, patchesChildChainSymbols);
				doKnmod = false;
				currentPointedsymbol = temp;
				continue;
			}


			if ((doKnmod || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_GENERIC_BLOCK_SYMBOLS || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_NO_SPEAKER_TEXT || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_SPEAKER_TEXT || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_GENERIC_NON_BLOCK_SYMBOLS || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_JUMP || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_RETURN || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_CALL || forceKNModForStartsWith.stream().anyMatch(currentPointedsymbol.getRENPY_TRIMMED_LINE()::startsWith) || forceDontKNModFor.stream().anyMatch(currentPointedsymbol.getRENPY_TRIMMED_LINE()::equalsIgnoreCase))) {
				renpySymbol knmodSymbol = null;
				doKnmod = true;
				if (forceDontKNModForStartsWith.stream().noneMatch(currentPointedsymbol.getRENPY_TRIMMED_LINE()::startsWith) && (currentPointedsymbol.isBlockSymbol() || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_JUMP || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_GENERIC_BLOCK_SYMBOLS || currentPointedsymbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_RETURN || forceKNModForStartsWith.stream().anyMatch(currentPointedsymbol.getRENPY_TRIMMED_LINE()::startsWith) || forceDontKNModFor.stream().anyMatch(currentPointedsymbol.getRENPY_TRIMMED_LINE()::equalsIgnoreCase))) {
					knmodSymbol = createKNMODSymbol(currentPointedsymbol.getRENPY_TRIMMED_LINE());
				} else {
					knmodSymbol = new renpyGenericNonBlockSymbol(RENPY_SYMBOL_TYPE.RENPY_GENERIC_NON_BLOCK_SYMBOLS, currentPointedsymbol.getRENPY_TRIMMED_LINE());
				}
				renpySymbol parentChainSymbol = currentPointedsymbol.getCHAIN_PARENT_SYMBOL();
				renpySymbol parentHierarchicalSymbol = currentPointedsymbol.getHIERARCHY_PARENT_SYMBOL();
				renpySymbol childChainSymbol = currentPointedsymbol.getCHAIN_CHILD_SYMBOL();
				knmodSymbol.setCHAIN_PARENT_SYMBOL(parentChainSymbol);
				knmodSymbol.setHIERARCHY_PARENT_SYMBOL(symbol);
				knmodSymbol.setCHAIN_CHILD_SYMBOL(childChainSymbol);
				parentChainSymbol.setCHAIN_CHILD_SYMBOL(knmodSymbol);
				parentHierarchicalSymbol.removeHIERARCHY_CHILD_SYMBOL(currentPointedsymbol);
				parentHierarchicalSymbol.addHIERARCHY_CHILD_SYMBOL(knmodSymbol);
				if (childChainSymbol != null) {
					childChainSymbol.setCHAIN_PARENT_SYMBOL(knmodSymbol);
				}
			}
		}
		return symbol.getChainString(0, -1, true, true).trim().replace("\t", "    ");
	}

	renpySpeakerText createKNMODSymbol(String line) {
		renpySpeakerText renpySymbol = new renpySpeakerText(RENPY_SYMBOL_TYPE.RENPY_SPEAKER_TEXT, "KN_MOD \"" + escapeStringForPython(line) + "\"");
		renpySymbol.setHIERARCHY_LEVEL(0);
		return renpySymbol;
	}
}

/**
 * define sb_i_phone_nude_event = Event(
 * location="home_isabelroom",
 * category="technical",
 * label="sb_i_phone_switch_naked_clothes",
 * title=_(""),
 * main_condition=( "isa.ap_tier>=14 and 'sb_j_cooking.level10' in seen_sb and 'sb_i_phone.level7' in seen_sb", _("")),
 * type="auto"
 * )
 * //todo treat above as single block, even thogh block does not ends wiht :
 */