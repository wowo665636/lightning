package com.li.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @ClassName: PropertiesUtil.java
 * @Description:
 * @author: wangd
 */
public class PropertyUtil {

	private static Properties props = new Properties();
	static {
		try {
			props.load(PropertyUtil.class.getResourceAsStream("storm-kafka.properties"));
			//登录验证,信息接口配置
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getProp(String name, Object[] obj) {
		String value = props.getProperty(name);
		return MessageFormat.format(value, obj);
	}

	public static String getProp(String name) {
		return props.getProperty(name);
	}
}
