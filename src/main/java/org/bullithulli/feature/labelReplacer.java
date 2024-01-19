package org.bullithulli.feature;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.bullithulli.utils.parserUtils;

import java.io.File;

public class labelReplacer {
    public void replace(String vanillaFilePath, String patchFilePath, String destinationFile, String listOfLabels, boolean isTabIndented, int spaceSize) throws Exception {
        // TODO: 1/15/24 Write UnitTests 
        parser vanillaParser = new parser();
        File vanillaFile = new File(vanillaFilePath);
        rootSymbol vRoot = (rootSymbol) vanillaParser.parseFrom(vanillaFile, isTabIndented, spaceSize, false, null);

        File patchFile = new File(patchFilePath);
        parser patchParser = new parser();
        rootSymbol pRoot = (rootSymbol) patchParser.parseFrom(patchFile, isTabIndented, spaceSize, false, null);
        //parserUtils.writeChainString(destinationFile, vRoot, 0, -1, true, true);
        String[] labelList = listOfLabels.split(",");
        for (String labelMapping : labelList) {
            String[] labels = labelMapping.split("->");
            renpyLabel patchLabel = pRoot.getInnerLabelByNameSearchRecursivly(labels[1]);
            renpyLabel sourceLabel = vRoot.getInnerLabelByNameSearchRecursivly(labels[0]);
            if (patchLabel == null || sourceLabel == null) {
                if (patchLabel == null) {
                    System.err.printf("PatchLabel [%s] couldn't not be found in patchFile%n", labels[1]);
                }
                if (sourceLabel == null) {
                    System.err.printf("SourceLabel [%s] couldn't not be found in sourceFile%n", labels[0]);
                }
                System.err.println("failed to patch");
                System.exit(1);
            }
            parserUtils.replaceLabel(sourceLabel, vanillaParser, patchLabel, patchParser);
        }
        parserUtils.writeChainString(destinationFile, vRoot, 0, -1, true, true);
    }
}
