package org.bullithulli.utils;

import org.bullithulli.rpyparser.RENPY_SYMBOL_POSITION;
import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpyCall;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpyJump;
import org.bullithulli.rpyparser.symImpl.renpySymbol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.bullithulli.utils.textUtils.getTabbedString;

public class parserUtils {
	public static void deleteInnerLabel(renpyLabel label_to_be_deleted, parser sourceRenpyParser) {
		sourceRenpyParser.pathMatrix.remove(label_to_be_deleted.getLabelName());
		// TODO: 1/14/24 Not required.
		// REMEMBER, here i'm deleting entire row from pathmatrix of the label. But this leads to confusion among calls and jumps as they are still pointing to this row, but row is already deleted
		renpySymbol parentLabel = label_to_be_deleted.getHIERARCHY_PARENT_SYMBOL();
		parentLabel.removeHIERARCHY_CHILD_SYMBOL(label_to_be_deleted);

		renpySymbol parentSymbol_of_label_to_be_deleted = label_to_be_deleted.getCHAIN_PARENT_SYMBOL();
		renpySymbol endOfLabel_label_to_be_deleted = label_to_be_deleted.getLastChainSymbol(label_to_be_deleted.getHIERARCHY_LEVEL(), false, true, true, true, label_to_be_deleted);
		renpySymbol nextOfLabelEnd = endOfLabel_label_to_be_deleted.getCHAIN_CHILD_SYMBOL();

		parentSymbol_of_label_to_be_deleted.setCHAIN_CHILD_SYMBOL(nextOfLabelEnd);
		if (nextOfLabelEnd != null)
			nextOfLabelEnd.setCHAIN_PARENT_SYMBOL(parentSymbol_of_label_to_be_deleted);
		label_to_be_deleted = null;
		System.gc();
	}

	public static void addLabelAfter(renpySymbol sourceRenpySymbol, parser sourceParser, renpyLabel patchLabel, RENPY_SYMBOL_POSITION position, int leftShiftBy) throws Exception {
		int adjustHierarchyBy = 0;
		renpySymbol sourceChainChildSymbol = sourceRenpySymbol.getCHAIN_CHILD_SYMBOL();
		renpySymbol lastSymbolFromPatchLabel = patchLabel.getLastChainSymbol(patchLabel.getHIERARCHY_LEVEL(), false, true, true, true, patchLabel);

		int sourceHierarchyLevel = sourceRenpySymbol.getHIERARCHY_LEVEL();
		if (sourceRenpySymbol.isBlockSymbol()) {
			if (position == RENPY_SYMBOL_POSITION.CHAIN_RIGHT) {
				adjustHierarchyBy = 1;
			} else if (position == RENPY_SYMBOL_POSITION.CHAIN_DOWN) {
				adjustHierarchyBy = 0;
			} else {
				adjustHierarchyBy = -leftShiftBy;
			}
		} else {
			if (position == RENPY_SYMBOL_POSITION.CHAIN_LEFT) {
				adjustHierarchyBy = -leftShiftBy;
			}
		}

		if (adjustHierarchyBy <= -5) {
			adjustHierarchyBy = 0;
		}

		//remove the patch label from patchRPY, should be common for all 3 adds as it is  doing removal at patch side, no ops on source side
		renpySymbol patchChainParent = patchLabel.getCHAIN_PARENT_SYMBOL();
		patchChainParent.removeHIERARCHY_CHILD_SYMBOL(patchLabel);
		renpySymbol next_of_lastSymbolFromPatchLabel = lastSymbolFromPatchLabel.getCHAIN_CHILD_SYMBOL();
		patchChainParent.setCHAIN_CHILD_SYMBOL(next_of_lastSymbolFromPatchLabel);
		if (next_of_lastSymbolFromPatchLabel != null)
			next_of_lastSymbolFromPatchLabel.setCHAIN_PARENT_SYMBOL(patchChainParent);
		renpySymbol patchesHierarchyParent = patchLabel.getHIERARCHY_PARENT_SYMBOL();
		patchesHierarchyParent.removeHIERARCHY_CHILD_SYMBOL(patchLabel);

		//Operations on patch label's child symbols, like adjusting hierarchy, pathMatrix, for all 3 ops, same code if adjustHierarchyValue differs
		ArrayList<renpySymbol> patchesChildChainSymbols = patchLabel.getChainSymbols(patchLabel.getHIERARCHY_LEVEL(), false, true, true, true, patchLabel);
		patchesChildChainSymbols.remove(patchLabel);//remove label symbol itself from its child-chain symbol list, as we pulled using includeSameHierarchy
		makeBlockHierarchyStartFromZero(patchLabel, patchesChildChainSymbols);
		patchLabel.setHIERARCHY_LEVEL(sourceHierarchyLevel + adjustHierarchyBy);//5+1
		for (renpySymbol symbol : patchesChildChainSymbols) {
			symbol.setHIERARCHY_LEVEL(symbol.getHIERARCHY_LEVEL() + sourceHierarchyLevel + adjustHierarchyBy);//1+5+1
			if (symbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_CALL) {
				String labelName = ((renpyCall) symbol).getCallINTOLabelName();
				ArrayList<renpySymbol> list = sourceParser.pathMatrix.get(labelName);
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(symbol);
				sourceParser.pathMatrix.put(labelName, list);
			} else if (symbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_JUMP) {
				String labelName = ((renpyJump) symbol).getJumpINTOLabelName();
				ArrayList<renpySymbol> list = sourceParser.pathMatrix.get(labelName);
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(symbol);
				sourceParser.pathMatrix.put(labelName, list);
			}
		}

		//The real Magic, modifying sourceRpy
		sourceRenpySymbol.setCHAIN_CHILD_SYMBOL(patchLabel);
		patchLabel.setCHAIN_PARENT_SYMBOL(sourceRenpySymbol);
		lastSymbolFromPatchLabel.setCHAIN_CHILD_SYMBOL(sourceChainChildSymbol);
		if (sourceChainChildSymbol != null)
			sourceChainChildSymbol.setCHAIN_PARENT_SYMBOL(lastSymbolFromPatchLabel);

		//Adjusting hierarchy dependencies of patch label in newRPY
		if (position == RENPY_SYMBOL_POSITION.CHAIN_RIGHT) {
			if (sourceRenpySymbol.isBlockSymbol()) {
				//add our block inside sourceRenpySymbol
				patchLabel.setHIERARCHY_PARENT_SYMBOL(sourceRenpySymbol);
				sourceRenpySymbol.addHIERARCHY_CHILD_SYMBOL(patchLabel);
			} else {
				//add our block inside the parent of sourceRenpySymbol
				renpySymbol HirrarchyParentOfSourceRenpySymbol = sourceRenpySymbol.getHIERARCHY_PARENT_SYMBOL();
				patchLabel.setHIERARCHY_PARENT_SYMBOL(HirrarchyParentOfSourceRenpySymbol);
				HirrarchyParentOfSourceRenpySymbol.addHIERARCHY_CHILD_SYMBOL(patchLabel);
			}
		} else if (position == RENPY_SYMBOL_POSITION.CHAIN_DOWN) {
			//Irrespective of source symbol is block or non-block type, put it down, because it is down request
			renpySymbol HirrarchyParentOfSourceRenpySymbol = sourceRenpySymbol.getHIERARCHY_PARENT_SYMBOL();
			patchLabel.setHIERARCHY_PARENT_SYMBOL(HirrarchyParentOfSourceRenpySymbol);
			HirrarchyParentOfSourceRenpySymbol.addHIERARCHY_CHILD_SYMBOL(patchLabel);
		} else {//position==RENPY_SYMBOL_POSITION.LEFT
			renpySymbol recurrsiveDetectedHirrarcyparet = sourceRenpySymbol;
			for (int i = 0; i <= leftShiftBy; i++) {
				recurrsiveDetectedHirrarcyparet = recurrsiveDetectedHirrarcyparet.getHIERARCHY_PARENT_SYMBOL();
				if (recurrsiveDetectedHirrarcyparet == null) {
					throw new Exception("Oops somehow you went out of the rpy");
				}
			}
			patchLabel.setHIERARCHY_PARENT_SYMBOL(recurrsiveDetectedHirrarcyparet);
			recurrsiveDetectedHirrarcyparet.addHIERARCHY_CHILD_SYMBOL(patchLabel);
		}
	}

	public static void makeBlockHierarchyStartFromZero(renpySymbol label, ArrayList<renpySymbol> childChainSymbols) {
		int h = label.getHIERARCHY_LEVEL();
		for (renpySymbol symbol : childChainSymbols) {
			symbol.setHIERARCHY_LEVEL(symbol.getHIERARCHY_LEVEL() - h);
		}
		label.setHIERARCHY_LEVEL(0);
	}

	// TODO: 1/14/24 public static void addLabelAt(renpySymbol source,int position, parser sourceParser, renpyLabel patchLabel) {
	// TODO: 1/14/24 public static void addLabelAtLeft(renpySymbol source, parser sourceParser, renpyLabel patchLabel) {

	/*
		Similar:
		getLastChainSymbol
		getChainSymbols
		getChainString
		writeChainString

		output is written as space indented than tabs. Renpy doesn't work with tab
	 */
	public static void writeChainString(String filePath, renpySymbol start, int subtractIndents, int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy) throws IOException {
		renpySymbol current = start;
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
		while (current != null) {
			if (includeSameHierarchy) {
				if (current.getHIERARCHY_LEVEL() >= thresholdingLevel) {
					writer.write(getTabbedString(current.getHIERARCHY_LEVEL() - subtractIndents).concat(current.getRENPY_TRIMMED_LINE()).concat("\n").replaceAll("\t", "    "));
				}
			} else {
				if (current.getHIERARCHY_LEVEL() > thresholdingLevel) {
					writer.write(getTabbedString(current.getHIERARCHY_LEVEL() - subtractIndents).concat(current.getRENPY_TRIMMED_LINE()).concat("\n").replaceAll("\t", "    "));
				}
			}

			if (current.getCHAIN_CHILD_SYMBOL() != null) {
				if (followLowerHierarchy) {
					current = current.getCHAIN_CHILD_SYMBOL();
				} else {
					if (includeSameHierarchy && current.getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
						break;
					} else if (!includeSameHierarchy && current.getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
						break;
					} else {
						current = current.getCHAIN_CHILD_SYMBOL();
					}
				}
			} else {
				break;
			}
		}
		writer.close();
	}

	public static void replaceLabel(renpyLabel sourceLabel, parser sourceParser, renpyLabel patchLabel, parser patchParser) throws Exception {
		renpySymbol sourceChainParent = sourceLabel.getCHAIN_PARENT_SYMBOL();

		int sourceChainParentHierarchy = sourceChainParent.getHIERARCHY_LEVEL();
		int sourceHierarchy = sourceLabel.getHIERARCHY_LEVEL();
		parserUtils.deleteInnerLabel(sourceLabel, sourceParser);
		if (sourceHierarchy == sourceChainParentHierarchy) {
			parserUtils.addLabelAfter(sourceChainParent, sourceParser, patchLabel, RENPY_SYMBOL_POSITION.CHAIN_DOWN, 0);
		} else if (sourceHierarchy > sourceChainParentHierarchy) {
			parserUtils.addLabelAfter(sourceChainParent, sourceParser, patchLabel, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
		} else {
			//putToLeft sourceChainParentHierarchy-sourceHierarchy
			parserUtils.addLabelAfter(sourceChainParent, sourceParser, patchLabel, RENPY_SYMBOL_POSITION.CHAIN_LEFT, sourceChainParentHierarchy - sourceHierarchy);
		}
	}
}
