package org.bullithulli.feature;

import org.bullithulli.Modder2;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertEquals;
import static org.bullithulli.Modder2.version;

public class TestKNModFastTest {

    @Test
    public void startSymbolPreservesPrefixAndModdersFromLabel() throws Exception {
        Modder2.realArgs = null;
        File input = resourceFile("knmodfast/prefixBoundary.rpy");
        Path output = Files.createTempFile("knmodfast-positive-", ".rpy");

        new Modder2().verifyAndExecuteKNModFeature(input.getAbsolutePath(), output.toString(), "label start");

        assertEquals(String.format("""
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2%s program. Created by BulliThulli
                # prelude comment

                define e = Character("Eileen")
                jump prologue

                label start:
                KN_MOD "jump scene_1"
                "Keep this line"
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 %s program. Created by BulliThulli"
                python:
                    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
                """, version, version),
                Files.readString(output));
    }

    @Test
    public void missingStartSymbolKeepsScriptUnwrappedUntilFooter() throws Exception {
        Modder2.realArgs = null;
        File input = resourceFile("knmodfast/prefixBoundary.rpy");
        Path output = Files.createTempFile("knmodfast-negative-", ".rpy");

        new Modder2().verifyAndExecuteKNModFeature(input.getAbsolutePath(), output.toString(), "label missing");

        assertEquals(String.format("""
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2%s program. Created by BulliThulli
                # prelude comment

                define e = Character("Eileen")
                jump prologue

                label start:
                    jump scene_1
                    "Keep this line"
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 %s program. Created by BulliThulli"
                python:
                    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
                """, version, version),
                Files.readString(output));
    }

    private File resourceFile(String resourcePath) throws Exception {
        URL fileURL = getClass().getClassLoader().getResource(resourcePath);
        assert fileURL != null;
        return new File(fileURL.toURI());
    }
}
