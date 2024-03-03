package com.dataserve.se.db.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class FileNetDateConverter {
	public static String toFileNetDate(String strDate) {
		String dateString = strDate;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fromdDate = LocalDate.parse(dateString, formatter);
        String stFromDate = fromdDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        stFromDate += "T210000Z";
        return stFromDate;
	}
	
}
