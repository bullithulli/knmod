package org.bullithulli.feature;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class TestTranslateItTest {
    @Test
    public void translateTest1() throws Exception {
        URL fileURL = getClass().getClassLoader().getResource("parserTests/translate.rpy");
        assert fileURL != null;
        File tlFile = new File(fileURL.toURI());
        fileURL = getClass().getClassLoader().getResource("parserTests/translateTest.rpy");
        assert fileURL != null;
        File rpyFile = new File(fileURL.toURI());
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false);
        assertEquals("""
                        vall "I asked you to sharpen it."
                        label x:
                            vall "I asked you to sharpen it."
                            vall "{size=+8}You know I don't like these games."
                        """.trim(),
                new String(Files.readAllBytes(new File("/tmp/out").toPath())).trim());
    }

    @Test
    public void translateTest2() throws Exception {
        URL fileURL = getClass().getClassLoader().getResource("parserTests/translate2.rpy");
        assert fileURL != null;
        File tlFile = new File(fileURL.toURI());
        fileURL = getClass().getClassLoader().getResource("parserTests/translateTest2.rpy");
        assert fileURL != null;
        File rpyFile = new File(fileURL.toURI());
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false);
        assertEquals("""
                        menu:
                            "Listen to the backstory":
                                jump intro_lore
                            "Skip the backstory":
                                jump intro2
                        """.trim(),
                new String(Files.readAllBytes(new File("/tmp/out").toPath())).trim());
    }

    @Test
    public void translateTest3() throws Exception {
        URL fileURL = getClass().getClassLoader().getResource("parserTests/translate3.rpy");
        assert fileURL != null;
        File tlFile = new File(fileURL.toURI());
        fileURL = getClass().getClassLoader().getResource("parserTests/translateTest3.rpy");
        assert fileURL != null;
        File rpyFile = new File(fileURL.toURI());
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false);
        assertEquals("""
                        label caR:
                            inkognito_f "I've heard something about you..."
                        """.trim(),
                new String(Files.readAllBytes(new File("/tmp/out").toPath())).trim());
    }

    @Test
    public void translateTest4() throws Exception {
        URL fileURL = getClass().getClassLoader().getResource("parserTests/translate4.rpy");
        assert fileURL != null;
        File tlFile = new File(fileURL.toURI());
        fileURL = getClass().getClassLoader().getResource("parserTests/translateTest4.rpy");
        assert fileURL != null;
        File rpyFile = new File(fileURL.toURI());
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false);
        assertEquals("""
                        label tavernroad_cass_end:
                            $ tavernroad_end = 1
                            scene road_end1
                            "At night"
                            cass "..."
                        """.trim(),
                new String(Files.readAllBytes(new File("/tmp/out").toPath())).trim());
    }

    @Test
    public void translateTest5() throws Exception {
        URL fileURL = getClass().getClassLoader().getResource("parserTests/translate5.rpy");
        assert fileURL != null;
        File tlFile = new File(fileURL.toURI());
        fileURL = getClass().getClassLoader().getResource("parserTests/translateTest5.rpy");
        assert fileURL != null;
        File rpyFile = new File(fileURL.toURI());
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false);
        assertEquals("""
                        m norm "Hey sleepyhead, wake up! Are you sleeping until lunch again?"
                        n sad "{i}(*yawn*){/i} Yeah [NameMom!t], Just... Five more minutes..."
                        m smile "Get up, get up! Breakfast is almost ready! I hope you didn't forget that you're coming to work with me, today."
                        m smile "You thought I would let you sit back the whole summer?"
                        n sad "Oh, okay, okay. I'm up!"
                        """.trim(),
                new String(Files.readAllBytes(new File("/tmp/out").toPath())).trim());
    }
}
