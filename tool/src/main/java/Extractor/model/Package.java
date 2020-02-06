package Extractor.model;

import Extractor.info.PackageInfo;

import javax.persistence.*;
// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
@Entity
@Table(name = "package")
public class Package {
	private int id;
	private String name;
	private int jarId;
	
	public Package(){
		
	}
	
	public Package(PackageInfo packageInfo) {
		this.name = packageInfo.getName();
		this.jarId = packageInfo.getJarId();
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

	public int getJarId() {
		return jarId;
	}

	public void setJarId(int jarId) {
		this.jarId = jarId;
	}
}
