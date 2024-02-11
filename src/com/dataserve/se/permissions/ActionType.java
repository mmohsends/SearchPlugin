package com.dataserve.se.permissions;

import java.util.Locale;

import com.dataserve.se.util.LclUtil;

public enum ActionType {
	CREATE(1, "CREATE"),
	EDIT(2, "EDIT"),
	DELETE(3, "DELETE"),
	VIEW(4, "VIEW"),
	IMPORT(5, "IMPORT"),
	PRINT_BARCODE(6, "PRINT_BARCODE"),
	REPRINT_BARCODE(7, "REPRINT_BARCODE"), 
	EXPORT(8, "EXPORT"),
	ALL_ACTIONS(9, "ALL_ACTIONS"),
	SEARCH(10, "SEARCH"),
	SCAN(11, "SCAN"),
	UPLOAD(12, "UPLOAD");
	
	private int id;
	private String nameAr;
	private String nameEn;
	
	private final Locale AR = new Locale("ar");
	
	ActionType(int id, String key) {
		this.id = id;
		this.nameAr = LclUtil.getText(key, Locale.ENGLISH);
		this.nameEn = LclUtil.getText(key, AR);
	}
	
	public int getId() {
		return id;
	}
	
	public String getNameAr() {
		return nameAr;
	}
	
	public String getNameEn() {
		return nameEn;
	}
	
	public static ActionType getTypeById(int id) {
		switch (id) {
			case 1: return CREATE;
			case 2: return EDIT;
			case 3: return DELETE;
			case 4: return VIEW;
			case 5: return IMPORT;
			case 6: return PRINT_BARCODE;
			case 7: return REPRINT_BARCODE;
			case 8: return EXPORT;
			case 9: return ALL_ACTIONS;
			case 10: return SEARCH;
			case 11: return SCAN;
			case 12: return UPLOAD;
			default: return null;
		}
	}
}
