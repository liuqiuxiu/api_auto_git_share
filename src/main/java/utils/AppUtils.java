package utils;

import java.net.URL;

public class AppUtils {
	
	static AppUtils appUtils = new AppUtils();

	public static String getRootPath() {
		URL base = appUtils.getClass().getResource(""); //先获得本类的所在位置，如/home/popeye/testjava/build/classes/net/  
		String path = base.getFile();
		path = path.substring(0, path.indexOf("cn/fatri"));
		return  path;
	}
	
	public static void main(String[] args) {
		System.out.println(getRootPath());
	}
	
}
