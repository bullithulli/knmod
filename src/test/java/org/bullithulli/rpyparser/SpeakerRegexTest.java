package org.bullithulli.rpyparser;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpeakerRegexTest {

    @Test
    public void speakerPattern_examples() {
        parser p = new parser();
        Pattern pattern = p.pattern_for_speaker_text;

        // should match
        assertTrue(pattern.matcher("karan \"hello\"").find());
        assertTrue(pattern.matcher("karan sad \"hello\"").find());
        assertTrue(pattern.matcher("dsa car \"dsa\"   #ds asd d").find());

        // should NOT match
        assertFalse(pattern.matcher("karan \"dasdasd\" sdsda").find());
        assertFalse(pattern.matcher("karan \"dasdasd").find());
        assertFalse(pattern.matcher("karan dasdasd").find());
    }
}
