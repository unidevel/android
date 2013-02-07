package com.unidevel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
	public static void extract(File srcFile, File destDir) throws IOException {
		FileInputStream in = new FileInputStream(srcFile);
		extract(in, destDir);
	}
	
	public static void extract(InputStream in, File destDir) throws IOException{
		ZipInputStream zipIn = new ZipInputStream(in);
		ZipEntry entry;
		while ( (entry = zipIn.getNextEntry()) != null ) {
			File file = new File(destDir, entry.getName());
			FileUtil.copy(zipIn, file);
			zipIn.closeEntry();
		}
	}
}
