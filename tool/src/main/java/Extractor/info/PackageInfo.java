package Extractor.info;

import Extractor.util.GitUtil;

import java.util.ArrayList;
// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
public class PackageInfo {
	private int id;
	private String name;
	private int jarId;
	private ArrayList<GitUtil.ClassInfo> classes;

	public PackageInfo(String packageName) {
		this.classes = new ArrayList<GitUtil.ClassInfo>();
		this.name = packageName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getJarId() {
		return jarId;
	}

	public ArrayList<GitUtil.ClassInfo> getClasses() {
		return classes;
	}
	
	public boolean addClass(GitUtil.ClassInfo classInfo){
		classes.add(classInfo);
		return true;
	}

	public boolean matchesImportStatement(String importedPackage) {
		return importedPackage.startsWith(name);
	}

	public String toString() {
		String packageString = "PACKAGE: " + name;
		for (GitUtil.ClassInfo classFile : classes) {
			packageString += "\n" + classFile.toString();
		}
		return packageString;
	}
}
