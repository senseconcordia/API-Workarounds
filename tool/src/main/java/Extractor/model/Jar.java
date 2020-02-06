package Extractor.model;

import Extractor.info.JarInfo;

import javax.persistence.*;
// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
@Entity
@Table(name = "jar")
public class Jar {
	private int id;
	private String name;
	private String artifactId;
	private String version;
	private String groupId;
	
	public Jar() {
	}
	
	public Jar(JarInfo jarInfo) {
		this.name = jarInfo.getName();
		this.groupId = jarInfo.getGroupId();
		this.artifactId = jarInfo.getArtifactId();
		this.version = jarInfo.getVersion();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
