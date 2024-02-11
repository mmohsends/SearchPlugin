package com.dataserve.se.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class LclUtil {
//	private static ResourceBundle localizationAr;
	private static ResourceBundle localizationEn;
	private static Properties properties;
	
	static {
		InputStream fileInput = null;
		try {
			java.util.ListResourceBundle.clearCache();
//			localizationAr = ResourceBundle.getBundle("localization", new Locale("ar"));
			localizationEn = ResourceBundle.getBundle("localization", Locale.ENGLISH);
			
			properties = new Properties();
			fileInput = LclUtil.class.getClassLoader().getResourceAsStream("localization.properties");
			properties.load(new InputStreamReader(fileInput, Charset.forName("UTF-8")));
		} catch (Exception e) {
			System.err.println("Failed to load localization files due to the following error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (fileInput != null) {
					fileInput.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String getText(String key, Locale loc) {
		if (Locale.ENGLISH.equals(loc)) {
			return localizationEn.getString(key);
		} else {
//			return localizationAr.getString(key);
			try {
				return new String(properties.getProperty(key));
//				return new String(properties.getProperty(key).getBytes("UTF-8"));
			} catch (Exception e) {
				return properties.getProperty(key);
			}
		}
	}
	
}
