package com.dataserve.se.permissions;


public enum Module {
	CLASSIFICATION(1, "Classification", "التصنيفات"),
	DEPARTMENT(2, "Department", "الادارات"),
	GROUP(3, "Group", "المجموعات"),
	PERMISSION(4, "Permission", "الصلاحيات"),
	STORAGE(5, "Storage", "الأرشيف"),
	USER(6, "Users", "المستخدمين"),
	REPORT(7, "Reports", "التقارير"),
	CLASSMANAGERFILENET(8, "Classification Manager FileNet", "ادارة تصنيفات الفايل نت"),
	WORKFLOW_ADMIN(9, "Workflow Administartaion", "ادارة دورات العمل"),
	FILE_TRANSFER_DESTRUCTION_ADMIN(10, "File destruction and transfer manager", "ادارة اتلاف وترحيل الوثائق"),
	DEFINITION(11, "Definition", "التعريفات"),
	ELECTRONIC_ARCHIVE(12, "Electronic Archive", "أرشفة الكترونية"),
	CONFIGURATION(13, "Configuration", "خصائص النظام");
	
	private int id;
	private String nameAr;
	private String nameEn;
	
	Module(int id, String nameAr, String nameEn) {
		this.id = id;
		this.nameAr = nameAr;
		this.nameEn = nameEn;
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
	
	public static Module getModuleById(int id) {
		switch (id) {
			case 1: return CLASSIFICATION;
			case 2: return DEPARTMENT;
			case 3: return GROUP;
			case 4: return PERMISSION;
			case 5: return STORAGE;
			case 6: return USER;
			case 7: return REPORT;
			case 8: return CLASSMANAGERFILENET;
			case 9: return WORKFLOW_ADMIN;
			case 10: return FILE_TRANSFER_DESTRUCTION_ADMIN;
			case 11: return DEFINITION;
			case 12: return ELECTRONIC_ARCHIVE;
			case 13: return CONFIGURATION;
			default: return null;
		}
	}
}
