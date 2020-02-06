package Extractor;


import Extractor.info.FieldInfo;
import Extractor.info.JarInfo;
import Extractor.util.GitUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
public class APIFinderImpl{

	private List<JarInfo> jarInfosFromPom;
	private List<JarInfo> jarInfosFromRepository;


	public APIFinderImpl(String projLocation, String jarFolderLocation){
		Extractor analyzer = new Extractor();
		jarInfosFromRepository = new ArrayList<JarInfo>();
		jarInfosFromPom = new ArrayList<JarInfo>();

		getJarsFromFolderLocation(jarFolderLocation, analyzer);
		setupJarsAfterInitialization();
	}

	private void getJarsFromFolderLocation(String folderLocation, Extractor analyzer){
			File jarFileLocation = new File(folderLocation);
		if (jarFileLocation.isDirectory()) {
			for (String jarPath : getAllJars(jarFileLocation.getAbsolutePath())) {
				JarFile jarFile;
				try {
					jarFile = new JarFile(new File(jarPath));
					jarInfosFromRepository.add(analyzer.AnalyzeJar(jarFile, "",
							"", ""));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setupJarsAfterInitialization(){
		ArrayList<JarInfo> allJars = new ArrayList<JarInfo>();
		allJars.addAll(jarInfosFromRepository);
		allJars.addAll(jarInfosFromPom);
		for (JarInfo jarInfo : allJars) {
			for (GitUtil.ClassInfo classInfo : jarInfo.getClasses()) {
				if (classInfo.getSuperClassInfo() == null &&
						!classInfo.getSuperClassName().equals("java.lang.Object")) {
					for (JarInfo jar : allJars) {
						for (GitUtil.ClassInfo cls : jar.getClasses()) {
							if (cls.getQualifiedName().equals( classInfo.getSuperClassName())) {
								classInfo.setSuperClassInfo(cls);
							}
						}
					}
				}
			}
		}
	}


	public Set<String> findMostRecentJarThatContainsMethod(List<String> imports,
													  String methodName, int numberOfParameters){

		Set<String> allJars = new HashSet<>();
		List<String> importStatements = new ArrayList<String>(imports);

		if (methodName.contains(".")) {
			importStatements.add(methodName);
		}

		for (String importedPackage : importStatements) {
			findMatchingMethodsInJars(jarInfosFromRepository, allJars,
					importedPackage, methodName, numberOfParameters);

		}
		return allJars;
	}


	private void findMatchingMethodsInJars(List<JarInfo> jarInfos,
									 Set<String> allJars, String importedPackage,
									 String methodName, int numberOfParameters) {
		for (JarInfo jarInfo : jarInfos) {
			if (jarInfo == null)
				continue;
			findPossibleJars(jarInfo, allJars, importedPackage,
					methodName, numberOfParameters);
		}
	}


	private void findPossibleJars(JarInfo jarInfo,
								  Set<String> allJars, String importedPackage,
								  String methodName, int numberOfParameters){

		for (GitUtil.ClassInfo classInfo: jarInfo.getClasses(importedPackage)){
			if (classInfo.getMethods(methodName,numberOfParameters).size() > 0){
				allJars.add(jarInfo.getName());
			}
		}
	}


	private Set<String> getAllJars(String projname) {
		Set<String> jars = new HashSet<String>();
		jars = getFiles(projname, "jar");

		return jars;
	}

	private static Set<String> getFiles(String directory, String type) {
		Set<String> jarFiles = new HashSet<String>();
		File dir = new File(directory);
		if (dir.listFiles() != null)
			for (File file : dir.listFiles()) {
				if (file.isDirectory() && !file.getName().equals("bin")) {
					jarFiles.addAll(getFiles(file.getAbsolutePath(), type));
				} else if (file.getAbsolutePath().toLowerCase()
						.endsWith((type.toLowerCase()))) {
					jarFiles.add(file.getAbsolutePath());
				}
			}
		return jarFiles;
	}




	public Set<FieldInfo> findAllFields(List<String> imports, String fieldName) {
		Set<FieldInfo> matchedFields = new LinkedHashSet<FieldInfo>();
		
		List<String> importStatements = new ArrayList<String>(imports);

		if (fieldName.contains(".")) {
			importStatements.add(fieldName);
		}
		
		for (String importedPackage : importStatements) {
			findMatchingFields(jarInfosFromRepository, matchedFields,
					importedPackage, fieldName);

			findMatchingFields(jarInfosFromPom, matchedFields, importedPackage,
					fieldName);
		}
		return matchedFields;
	}

	private void findMatchingFields(List<JarInfo> jarInfos,
                                    Set<FieldInfo> matchedFields, String importedPackage,
                                    String fieldName) {
		for (JarInfo jarInfo : jarInfos) {
			if (jarInfo == null)
				continue;
			findMatchingField(jarInfo, matchedFields, importedPackage,
					fieldName);
		}
	}

	private void findMatchingField(JarInfo jarInfo,
                                   Set<FieldInfo> matchedFields, String importedPackage,
                                   String fieldName) {
		for (GitUtil.ClassInfo classInfo : jarInfo.getClasses(importedPackage)) {
			matchedFields.addAll(classInfo.getFieldsByName(fieldName));
		}
	}
}
