package org.bullithulli;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.bullithulli.Modder2.displayHelp;

public class TestModder {
    @Test
    public void fileLoadTest() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("dir1/a.rpy");
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Test
    public void testDisplayHelp() {
        displayHelp();
    }
}