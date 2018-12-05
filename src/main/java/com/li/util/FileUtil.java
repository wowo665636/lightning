package com.li.util;

import java.io.*;

/**
 * 文件读取
 * @author peak
 * @Date 2017年2月10日 下午4:45:46
 */
public class FileUtil {
	
	/**
	 * 读文件
	 * @return
	 */
	public String ReadFile(InputStream inputStream){
		BufferedReader reader = null;
		String laststr = "";
		try{
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString = reader.readLine()) != null){
				laststr += tempString;
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}
	
	/**
	 * 读文件
	 * @param Path
	 * @return
	 */
	public String ReadFile(String Path){
		BufferedReader reader = null;
		String laststr = "";
		try{
			FileInputStream fileInputStream = new FileInputStream(Path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString = reader.readLine()) != null){
				laststr += tempString;
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}
}
