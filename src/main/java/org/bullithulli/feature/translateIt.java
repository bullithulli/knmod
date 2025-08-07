package org.bullithulli.feature;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.bullithulli.utils.parserUtils;

import java.io.File;

public class translateIt {
    public void generateTranslatedScript(String vanillaFilePath, String tlFile, String destinationFile, boolean isTabIndented) throws Exception {
        parser vanillaParser = new parser();
        File vanillaFile = new File(vanillaFilePath);
        rootSymbol vRoot = (rootSymbol) vanillaParser.parseFrom(vanillaFile, isTabIndented, true, new File(tlFile));
        parserUtils.writeChainString(destinationFile, vRoot, 0, -1, true, true);
    }
}
