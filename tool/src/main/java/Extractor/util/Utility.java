package Extractor.util;


// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
public class Utility {

	
	public static String getJarName(String url) {
		url = url.replace('\\', '/');
		return url.substring(url.lastIndexOf('/') + 1);
    }
	

}
