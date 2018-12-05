package com.li.util;

import org.apache.commons.lang.StringUtils;

/**
 * 比较对象值是否相等
 * @author peak
 * @Date 2017年2月10日 下午3:24:00
 */
public class CompareUtil {
	
	/**等于(default)*/
	public static String EQUAL = "==";
	/**不等于*/
	public static String NOTEQUAL = "!=";
	/**包含*/
	public static String CONTAIN= "contain";
	
	/**
	 * 比较 a 和 b 
	 * @param a 配置中的值
	 * @param b 实际参数中的值
	 * @param mode 计算模式 （ ==, !=） 
	 * @return
	 */
	public static boolean compare(Object a, Object b, String mode){
		boolean result = false;
		///默认使用  等于
		if(StringUtils.isBlank(mode)){
			mode = EQUAL;
		}
		
		if(EQUAL.equals(mode)){
			result = isEqual(a, b);
		}else if(NOTEQUAL.equals(mode)){
			result = !isEqual(a, b);
		}else if(CONTAIN.equals(mode)){
			result = contain(a, b);
		}
		return result;
	}

	
	/**
	 * 比较 a 和 b 是否相等
	 * @param a 配置中的值
	 * @param b 实际参数中的值
	 * @return
	 */
	public static boolean isEqual(Object a, Object b){
		
		if(null==a || b==null){
			//a 为 null 的特殊情况
			return a == b;
		}
		String aCanonicalName =  a.getClass().getCanonicalName();
		String bCanonicalName =  b.getClass().getCanonicalName();
		// a 为 *（任意值） 的特殊情况
		if((a instanceof String) && a.equals("*") ){
			return true;
		}
		//数据类型不一致 直接false返回
		if(!aCanonicalName.equals(bCanonicalName)){
			return false;
		}
		return a.equals(b);
	}
	/**
	 * 判断 b 中是否包含 a
	 * @param a 配置中的值
	 * @param b 实际参数中的值
	 * @return
	 */
	public static boolean contain(Object a, Object b){
		if(null==a || b==null){
			return false;
		}
		String aStr = String.valueOf(a);
		String bStr = String.valueOf(b);
		
		return bStr.contains(aStr);
	}
	
	
}
