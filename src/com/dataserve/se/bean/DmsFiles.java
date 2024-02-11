package com.dataserve.se.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.ibm.json.java.JSONObject;

public class DmsFiles implements Serializable {
	private int fileId;
	private String documentId;
	private int folderId;
	private int savePeriod;
	private Timestamp createDate;
	private Timestamp modifiedDate;

	private int folerNo;
	private int boxNo;
	private ClassificationBean classifiction;
	private Department department;
	private User user;
	private DMSDestroyStatus destroyStatus ;
	private DMSTransferStatus transferStatus ;
	private String documentTitle;
	private int tempSavePeriodAllDeptSum;
	private String createYearHijri;
	private String createYear;

	private int noPages;
	private int permanentSavePeriodAllDeptSum;

	private String archiveCenterTransferExtensionReason;
	private int archiveCenterTransferExtensionMonths;
	
	public DmsFiles() {
	}

	
	public DmsFiles(int fileId) {
		super();
		this.fileId = fileId;
	}

	
	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	

	public ClassificationBean getClassifiction() {
		return classifiction;
	}


	public void setClassifiction(ClassificationBean classifiction) {
		this.classifiction = classifiction;
	}


	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public int getSavePeriod() {
		return savePeriod;
	}


	public void setSavePeriod(int savePeriod) {
		this.savePeriod = savePeriod;
	}


	public Timestamp getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}


	public Timestamp getModifiedDate() {
		return modifiedDate;
	}


	public void setModifiedDate(
			Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


	public DMSDestroyStatus getDestroyStatus() {
		return destroyStatus;
	}


	public void setDestroyStatus(DMSDestroyStatus destroyStatus) {
		this.destroyStatus = destroyStatus;
	}


	public int getFolerNo() {
		return folerNo;
	}


	public void setFolerNo(int folerNo) {
		this.folerNo = folerNo;
	}


	public int getBoxNo() {
		return boxNo;
	}


	public void setBoxNo(int boxNo) {
		this.boxNo = boxNo;
	}


	public DMSTransferStatus getTransferStatus() {
		return transferStatus;
	}


	public void setTransferStatus(DMSTransferStatus transferStatus) {
		this.transferStatus = transferStatus;
	}
	public String getDocumentTitle() {
		return documentTitle;
	}


	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}


	public int getTempSavePeriodAllDeptSum() {
		return tempSavePeriodAllDeptSum;
	}


	public void setTempSavePeriodAllDeptSum(int tempSavePeriodAllDeptSum) {
		this.tempSavePeriodAllDeptSum = tempSavePeriodAllDeptSum;
	}


	public void print() {
//		System.out.println("document Id : "+this.getDocumentId());
		
	}


	public String getCreateYearHijri() {
		return createYearHijri;
	}


	public void setCreateYearHijri(String createYearHijri) {
		this.createYearHijri = createYearHijri;
	}
	

	public int getNoPages() {
		return noPages;
	}


	public void setNoPages(int noPages) {
		this.noPages = noPages;
	}


	public int getPermanentSavePeriodAllDeptSum() {
		return permanentSavePeriodAllDeptSum;
	}


	public void setPermanentSavePeriodAllDeptSum(int permanentSavePeriodAllDeptSum) {
		this.permanentSavePeriodAllDeptSum = permanentSavePeriodAllDeptSum;
	}


	public String getCreateYear() {
		return createYear;
	}


	public void setCreateYear(
			String createYear) {
		this.createYear = createYear;
	}


	public String getArchiveCenterTransferExtensionReason() {
		return archiveCenterTransferExtensionReason;
	}


	public void setArchiveCenterTransferExtensionReason(
			String archiveCenterTransferExtensionReason) {
		this.archiveCenterTransferExtensionReason = archiveCenterTransferExtensionReason;
	}




	public int getArchiveCenterTransferExtensionMonths() {
		return archiveCenterTransferExtensionMonths;
	}


	public void setArchiveCenterTransferExtensionMonths(
			int archiveCenterTransferExtensionMonths) {
		this.archiveCenterTransferExtensionMonths = archiveCenterTransferExtensionMonths;
	}


	public JSONObject getAsJson(Locale locale) {
		JSONObject obj = new JSONObject();
		obj.put("id", this.getFileId());
		obj.put("savePeriod", this.getSavePeriod());
		obj.put("fileCreateDate", new SimpleDateFormat("yyyy-MM-dd").format(this.getCreateDate()));
		obj.put("fileModifiedDate", new SimpleDateFormat("yyyy-MM-dd").format(this.getModifiedDate()));

		obj.put("documentTitle", this.getDocumentTitle());
		obj.put("noPages", this.getNoPages());
		obj.put("createYearHijri", this.getCreateYearHijri());

		obj.put("boxNo", this.getBoxNo());
		obj.put("folderNo", this.getFolerNo());
		obj.put("tempSavePeriodAllDeptSum", this.getTempSavePeriodAllDeptSum());
		obj.put("permanentSavePeriodAllDeptSum", this.getPermanentSavePeriodAllDeptSum());
		
		obj.put("archiveCenterTransferExtensionMonths", this.getArchiveCenterTransferExtensionMonths());
		obj.put("archiveCenterTransferExtensionReason", this.getArchiveCenterTransferExtensionReason());

		
		if (Locale.ENGLISH.equals(locale)) {
			obj.put("createYear", this.getCreateYear());
		}else{
			obj.put("createYear", this.getCreateYearHijri());
		}

		if(this.getClassifiction() !=null){
			if (Locale.ENGLISH.equals(locale)) {
				obj.put("className", this.getClassifiction().getNameEn());
			}else{
				obj.put("className", this.getClassifiction().getNameAr());
			}
			obj.put("sympolicName", this.getClassifiction().getSymbolicName());
		}
		if(this.getDepartment() !=null){
			if (Locale.ENGLISH.equals(locale)) {
				obj.put("deptName", this.getDepartment().getDeptEnName());
			}else
				obj.put("deptName", this.getDepartment().getDeptArName());

		}
		
		if(this.getDestroyStatus()!=null){
			if (Locale.ENGLISH.equals(locale)) {
				obj.put("destroyStatus", this.getDestroyStatus().getDestroyStatusNameEn());
			}else
				obj.put("destroyStatus", this.getDestroyStatus().getDestroyStatusNameAr());
			
			obj.put("destroyStatusId", this.getDestroyStatus().getDestroyStatusId());
		}
		
		if(this.getTransferStatus()!=null){
			if (Locale.ENGLISH.equals(locale)) {
				obj.put("transferStatus", this.getTransferStatus().getTransferStatusNameEn());
			}else
				obj.put("transferStatus", this.getTransferStatus().getTransferStatusNameAr());
			
			obj.put("transferStatusId", this.getTransferStatus().getTransferStatusId());
		}
		return obj;
	}




}
