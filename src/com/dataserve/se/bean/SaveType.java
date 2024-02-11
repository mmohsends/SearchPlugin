package com.dataserve.se.bean;

import java.util.Locale;

import com.dataserve.se.util.LclUtil;

public enum SaveType {
	ALL(3, "ALL_STORAGE_TYPES"),
	TEMPORAL(2, "TEMPORAL"),
	PERMANENT(1, "PERMANENT");
	
	int id;
	private String typeAr;
	private String typeEn;
	
	private final Locale AR = new Locale("ar");
	
	SaveType(int id, String key) {
		this.id = id;
		this.typeAr = LclUtil.getText(key, AR);
		this.typeEn = LclUtil.getText(key, Locale.ENGLISH);
	}
	
	public int getId() {
		return id;
	}
	
	public String getTypeAr() {
		return typeAr;
	}
	
	public String getTypeEn() {
		return typeEn;
	}
	
	public static SaveType getSaveTypeById(int id) {
		switch (id) {
			case 1: return PERMANENT;
			case 2: return TEMPORAL;
			case 3: return ALL;
			default: return null;
		}
	}
}
