package Extractor.model;

import Extractor.util.GitUtil;

import javax.persistence.*;
// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
@Entity
@Table(name = "class")
public class Class {
	private int id;
	private String name;
	private int packageId;
	
	public Class(){
		
	}
	
	public Class(GitUtil.ClassInfo classInfo) {
		this.name = classInfo.getSignature();
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

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
}
