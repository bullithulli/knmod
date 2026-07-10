package org.bullithulli.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class TestFileUtilsTest {

    @Test
    public void writeLineToDiskAppendsTheProjectsEolEveryTime() throws Exception {
        Path temp = Files.createTempFile("file-utils-", ".txt");
        try (FileWriter fw = new FileWriter(temp.toFile())) {
            fileUtils.writeLineToDisk("alpha", fw);
            fileUtils.writeLineToDisk("beta", fw);
        }

        assertEquals("alpha\nbeta\n", Files.readString(temp));
    }

    @Test
    public void writeLineToDiskPreservesEmptyLinesAsRealNewlines() throws Exception {
        File temp = File.createTempFile("file-utils-empty-", ".txt");
        try (FileWriter fw = new FileWriter(temp)) {
            fileUtils.writeLineToDisk("", fw);
            fileUtils.writeLineToDisk("tail", fw);
        }

        assertEquals("\ntail\n", Files.readString(temp.toPath()));
    }
}
