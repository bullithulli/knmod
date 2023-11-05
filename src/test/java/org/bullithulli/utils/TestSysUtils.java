package org.bullithulli.utils;

import java.io.*;

public class TestSysUtils {
    public static ByteArrayOutputStream byteArrayOS;
    public static PrintStream printStream;

    public static void disableSysOuts() {
        byteArrayOS = new ByteArrayOutputStream();
        printStream = new PrintStream(byteArrayOS);
        System.setOut(printStream);
    }

    public static void enableSysOuts() throws IOException {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        printStream.flush();
        byteArrayOS.flush();
    }

    public static String getStringFromSysOuts() {
        return byteArrayOS.toString();
    }
}
