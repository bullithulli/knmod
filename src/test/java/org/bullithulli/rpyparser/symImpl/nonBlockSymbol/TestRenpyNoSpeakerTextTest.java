package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestRenpyNoSpeakerTextTest {
	@Test
	public void test1() {
		String line = "\"hello\"";
		renpyNoSpeakerText renpyNoSpeakerText = new renpyNoSpeakerText(RENPY_SYMBOL_TYPE.RENPY_SPEAKER_TEXT, line);
		assertEquals(renpyNoSpeakerText.getRENPY_TRIMMED_LINE(), "\"hello\"");
	}

	@Test
	public void test2() {
		String rpyCode = """
				label one:
					"Hello"
					label two:
						"Bellow"
						label three:
							"three"
							Anwar "again three"
					label four:
						"Four"
						Anwar "again three"
					label five:
						IAM_FIVE "five"
						"again 5 three"
					label six:
						IAM_SIX "SIX"
						"again three"
						label seven:
						VENIAMIN "SEVEN"
						Anwar "again three"
					SIAM "EIGHT"
				Anwar "again three"
				"SEVEN"
				Anwar "again three"
				""";
		parser renpyParser = new parser();
		assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1, true, true).trim(), rpyCode.trim());
	}
}
