package Extractor;


import Extractor.info.JarInfo;
import java.io.File;

import java.util.jar.JarFile;

// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
public class Extractor {

	private String jarsPath;


	public Extractor() {
		String jarFileLocation = System.getProperty("user.home") + File.separator + "jars";
		File file = new File(jarFileLocation);
		file.mkdirs();
		jarsPath = file.getAbsolutePath();
		try {
			/*
			 * File db = new File("mydb.db"); if (!db.exists()) { File emptyDb =
			 * new File("empty.db"); Utility.copyFileUsingChannel(emptyDb, db);
			 * } manager = new JarManager();
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public JarInfo AnalyzeJar(JarFile jarFile, String groupId, String artifactId, String version) {
		if (jarFile == null)
			return null;
		JarInfo jarInfo = new JarInfo(jarFile, groupId, artifactId, version);
		// if(jarInfo != null && groupId != "" && artifactId != "" && version !=
		// "")
		// SaveToDb(jarInfo);
		return jarInfo;
	}




}
