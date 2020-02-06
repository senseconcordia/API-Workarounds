package Extractor.info;

import Extractor.util.GitUtil;
import Extractor.util.Utility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
public class JarInfo {
	private String name;
	private String groupId;
	private String artifactId;
	private String version;

	private Map<String, PackageInfo> packages;

	public JarInfo(JarFile jarFile, String groupId, String artifactId,
                   String version) {
		this.artifactId = artifactId;
		this.groupId = groupId;
		this.version = version;
		this.name = Utility.getJarName(jarFile.getName());
		this.packages = new HashMap<String, PackageInfo>();

		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {

			JarEntry entry = entries.nextElement();

			String entryName = entry.getName();
			if (entryName.endsWith(".class") && !entryName.contains("module-info")) {
				ClassNode classNode = new ClassNode();
				InputStream classFileInputStream;
				try {
					classFileInputStream = jarFile.getInputStream(entry);
					try {
						ClassReader classReader = new ClassReader(
								classFileInputStream);
						classReader.accept(classNode, 0);
						GitUtil.ClassInfo newClass = new GitUtil.ClassInfo(classNode);
						String packageName = newClass.getQualifiedName().substring(0,
								newClass.getQualifiedName().lastIndexOf('.'));
						PackageInfo packageInfo = getPackageInfo(packageName);
						packageInfo.addClass(newClass);
					} catch(IllegalArgumentException e){
						System.out.println("IllegalArgumentException, could not read class file: " + entryName);
					} catch(Exception e) {
						System.out.println("Could not read class file");
						e.printStackTrace();
					} finally {
						classFileInputStream.close();
					}
				} catch (Exception e) {
					System.out.println("Could not read class file");
					e.printStackTrace();
				}
			}
		}

		for (GitUtil.ClassInfo classInfo : getClasses()) {
			if (!classInfo.getSuperClassName().equals("java.lang.Object")) {
				for (GitUtil.ClassInfo cls : getClasses()) {
					if (cls.getQualifiedName().equals(
							classInfo.getSuperClassName())) {
						classInfo.setSuperClassInfo(cls);
					}
				}
			}
			for (String superInterface : classInfo.getSuperInterfaceNames()) {
				for (GitUtil.ClassInfo cls : getClasses()) {
					if (cls.getQualifiedName().equals(
							superInterface)) {
						classInfo.putSuperInterfaceInfo(superInterface, cls);
					}
				}
			}
		}

	}

	private PackageInfo getPackageInfo(String packageName) {
		if (packages.containsKey(packageName)) {
			return packages.get(packageName);
		}
		PackageInfo packageInfo = new PackageInfo(packageName);
		packages.put(packageName, packageInfo);
		return packageInfo;
	}

	public String toString() {
		String jarString = name;
		for (PackageInfo packageInfo : packages.values()) {
			jarString += "\n\n" + packageInfo.toString();
		}
		return jarString;
	}

	public ArrayList<GitUtil.ClassInfo> getClasses() {
		ArrayList<GitUtil.ClassInfo> classes = new ArrayList<GitUtil.ClassInfo>();
		for (PackageInfo packageInfo : packages.values()) {
			classes.addAll(packageInfo.getClasses());
		}
		return classes;
	}

	public Collection<PackageInfo> getPackages() {
		return packages.values();
	}

	public String getName() {
		return name;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public ArrayList<GitUtil.ClassInfo> getClasses(String importedPackage) {
		ArrayList<GitUtil.ClassInfo> matchedClasses = new ArrayList<GitUtil.ClassInfo>();
		for (PackageInfo packageInfo : packages.values()) {
			if (packageInfo.matchesImportStatement(importedPackage)) {
				if (packageInfo.getName().equals(importedPackage)) {
					matchedClasses.addAll(packageInfo.getClasses());
				}
				else {
					for (GitUtil.ClassInfo classInfo : packageInfo.getClasses()) {
						if (classInfo.matchesImportStatement(importedPackage)) {
							matchedClasses.add(classInfo);
						}
					}
				}
			}
		}
		return matchedClasses;
	}
}
