package org.bullithulli.rpyparser;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserRegexTest {

    @Test
    public void blockSymbolPattern_coversRealBlockSyntaxAndRejectsLookalikes() {
        assertMatches(parser.pattern_for_block_symbols, "label start:");
        assertMatches(parser.pattern_for_block_symbols, "    label start: # keep comment");
        assertMatches(parser.pattern_for_block_symbols, "if mc_cancel_apurna_date == 1 or");
        assertMatches(parser.pattern_for_block_symbols, "if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE and");
        assertMatches(parser.pattern_for_block_symbols, "if isa.ap_tier>=14 and 'sb_j_cooking.level10' in seen_sb and 'sb_i_phone.level7' in seen_sb:");
        assertMatches(parser.pattern_for_block_symbols, "label пролог:");
        assertMatches(parser.pattern_for_block_symbols, "screen окно_главное():");
        assertMatches(parser.pattern_for_block_symbols, "define sb_i_phone_nude_event = Event(");
        assertMatches(parser.pattern_for_block_symbols, "screen tabletUI():");
        assertMatches(parser.pattern_for_block_symbols, "python:");
        assertMatches(parser.pattern_for_block_symbols, "if score >= 10 and \"A&B\" in flags or");

        assertNotMatches(parser.pattern_for_block_symbols, "label start");
        assertNotMatches(parser.pattern_for_block_symbols, "jump chapter1");
        assertNotMatches(parser.pattern_for_block_symbols, "return");
        assertNotMatches(parser.pattern_for_block_symbols, "alice \"hello\"");
        assertNotMatches(parser.pattern_for_block_symbols, "# label start:");
        assertNotMatches(parser.pattern_for_block_symbols, "label 😀:");
        assertNotMatches(parser.pattern_for_block_symbols, "define sb_i_phone_nude_event = Event");
        assertNotMatches(parser.pattern_for_block_symbols, "if score >= 10 and \"A&B\" in flags");
        assertNotMatches(parser.pattern_for_block_symbols, "");
    }

    @Test
    public void speakerPattern_coversSpeakerFormsAndRejectsNonSpeakerForms() {
        assertMatches(parser.pattern_for_speaker_text, "karan \"hello\"");
        assertMatches(parser.pattern_for_speaker_text, "karan sad \"hello\"");
        assertMatches(parser.pattern_for_speaker_text, "A1 \"hello\"");
        assertMatches(parser.pattern_for_speaker_text, "dsa car \"dsa\"   #ds asd d");
        assertMatches(parser.pattern_for_speaker_text, "karan \"Привет — [name] {size=+8} ♥\"");
        assertMatches(parser.pattern_for_speaker_text, "karan sad \"Hello, world!\" # trailing comment");

        assertNotMatches(parser.pattern_for_speaker_text, "_karan \"hello\"");
        assertNotMatches(parser.pattern_for_speaker_text, "karan \"hello\" sdsda");
        assertNotMatches(parser.pattern_for_speaker_text, "karan \"\"");
        assertNotMatches(parser.pattern_for_speaker_text, "karan dasdasd");
        assertNotMatches(parser.pattern_for_speaker_text, "\"hello\"");
        assertNotMatches(parser.pattern_for_speaker_text, "alice bob charlie \"hi\"");
        assertNotMatches(parser.pattern_for_speaker_text, "Élan \"bonjour\"");
        assertNotMatches(parser.pattern_for_speaker_text, "karan sad extra \"hello\"");
    }

    @Test
    public void noSpeakerPattern_coversQuotedNarrationAndRejectsNonQuotedLines() {
        assertMatches(parser.pattern_for_no_speaker_texts, "\"hello\"");
        assertMatches(parser.pattern_for_no_speaker_texts, "   \"hello\"");
        assertMatches(parser.pattern_for_no_speaker_texts, "\t\"hello\"");
        assertMatches(parser.pattern_for_no_speaker_texts, "\"\"");
        assertMatches(parser.pattern_for_no_speaker_texts, "    \"hello\" # comment");
        assertMatches(parser.pattern_for_no_speaker_texts, "\"Привет — [name] {size=+8} ♥\"");

        assertNotMatches(parser.pattern_for_no_speaker_texts, "karan \"hello\"");
        assertNotMatches(parser.pattern_for_no_speaker_texts, "# \"hello\"");
        assertNotMatches(parser.pattern_for_no_speaker_texts, "return");
        assertNotMatches(parser.pattern_for_no_speaker_texts, "");
    }

    @Test
    public void createTranslationTable_usesRegexesAcrossCommentSpeakerOldAndNarrationForms() throws Exception {
        Path temp = Files.createTempFile("parser-regex-", ".rpy");
        Files.writeString(temp, """
                # game/intro.rpy:1
                # vall "Punctuated: [name] {size=+8} hello."
                vall "Translated one"

                # game/intro.rpy:2
                old "Self-voicing disabled."
                "Self-voicing enabled."

                # game/intro.rpy:3
                # mc "Starts with leading whitespace."
                    "Translated two"
                """);

        HashMap<String, String> dict = new parser().createTranslationTable(new File(temp.toString()));

        assertEquals(3, dict.size());
        assertEquals("\"Translated one\"", dict.get("\"Punctuated: [name] {size=+8} hello.\""));
        assertEquals("\"Self-voicing enabled.\"", dict.get("\"Self-voicing disabled.\""));
        assertEquals("\"Translated two\"", dict.get("\"Starts with leading whitespace.\""));
    }

    @Test
    public void createTranslationTable_handlesUnicodeAndSymbolHeavyPhrases() throws Exception {
        Path temp = Files.createTempFile("parser-regex-unicode-", ".rpy");
        Files.writeString(temp, """
                # game/scene.rpy:10
                # vall "Проклятье, нож совсем затупился."
                vall "Damn, the knife is completely blunted."

                # game/scene.rpy:11
                # vall "Я ведь попросила наточить его."
                vall "I asked you to sharpen it."

                # game/scene.rpy:12
                # vall "{size=+8}Ты же знаешь, я не люблю эти игры.{/size}"
                vall "{size=+8}You know I don't like these games."

                # game/scene.rpy:13
                old "Choice: A&B / C&D"
                "Choice: A&B / C&D translated"
                """);

        HashMap<String, String> dict = new parser().createTranslationTable(new File(temp.toString()));

        assertEquals(4, dict.size());
        assertEquals("\"Damn, the knife is completely blunted.\"", dict.get("\"Проклятье, нож совсем затупился.\""));
        assertEquals("\"I asked you to sharpen it.\"", dict.get("\"Я ведь попросила наточить его.\""));
        assertEquals("\"{size=+8}You know I don't like these games.\"", dict.get("\"{size=+8}Ты же знаешь, я не люблю эти игры.{/size}\""));
        assertEquals("\"Choice: A&B / C&D translated\"", dict.get("\"Choice: A&B / C&D\""));
    }

    private static void assertMatches(java.util.regex.Pattern pattern, String value) {
        assertTrue("Expected match for: " + value, pattern.matcher(value).find());
    }

    private static void assertNotMatches(java.util.regex.Pattern pattern, String value) {
        assertFalse("Expected no match for: " + value, pattern.matcher(value).find());
    }
}
