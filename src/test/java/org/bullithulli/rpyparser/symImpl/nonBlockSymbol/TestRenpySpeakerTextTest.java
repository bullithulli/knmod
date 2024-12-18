package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestRenpySpeakerTextTest {
	@Test
	public void test1() {
		String line = "anwar \"hello\"";
		renpySpeakerText renpySpeakerText = new renpySpeakerText(RENPY_SYMBOL_TYPE.RENPY_SPEAKER_TEXT, line);
		assertEquals(renpySpeakerText.getWho(), "anwar");
	}

	@Test
	public void test2() {
		String rpyCode = """
				label one:
					John "Hello"
					label two:
						DOe "Bellow"
						label three:
							Anwar "three"
							Anwar "again three"
					label four:
						Anwar "Four"
						Anwar "again three"
					label five:
						IAM_FIVE "five"
						Anwar "again 5 three"
					label six:
						IAM_SIX "SIX"
						Anwar "again three"
						label seven:
						VENIAMIN "SEVEN"
						Anwar "again three"
					SIAM "EIGHT"
				Anwar "again three"
				VENIAMIN "SEVEN"
				Anwar "again three"
				""";
		parser renpyParser = new parser();
		assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1, true, true).trim(), rpyCode.trim());
	}
}
