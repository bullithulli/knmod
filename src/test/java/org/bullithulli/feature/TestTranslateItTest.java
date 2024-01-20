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
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false, 4);
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
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false, 4);
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
        new translateIt().generateTranslatedScript(rpyFile.getAbsolutePath(), tlFile.getAbsolutePath(), "/tmp/out", false, 4);
        assertEquals("""
                        label caR:
                            inkognito_f "I've heard something about you..."
                        """.trim(),
                new String(Files.readAllBytes(new File("/tmp/out").toPath())).trim());
    }
}
