package org.bullithulli.utils;

import java.io.FileWriter;

import static org.bullithulli.modder2.eol;

public class fileUtils {
	public static void writeLineToDisk(String str, FileWriter fw) throws Exception {
		fw.write(str);
		fw.write(eol);
	}
}
