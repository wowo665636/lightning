package com.li.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);


	/**
	 * 发送post请求(新)
	 * 
	 * @param requestUrl
	 *            参数url
	 * @param fullParam
	 *            参数值
	 * @param paramKey
	 *            参数名称
	 * @return
	 * @throws Exception
	 */
	public static String reqPost1(String requestUrl, String fullParam, String paramKey) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(requestUrl);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair(fullParam, paramKey));

		// 将参数进行编码
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
		// 设置类型
		uefEntity.setContentType("application/x-www-form-urlencoded");

		// 设置请求的数据
		httppost.setEntity(uefEntity);
		// 执行
		HttpResponse response = httpclient.execute(httppost);

		HttpEntity entity = response.getEntity();
		log.info("----------------------------------------");

		log.info(response.getStatusLine().toString());
		if (entity != null)
		{
			log.info("Response content length: "
					+ entity.getContentLength());
		}
		// 显示结果
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent(), "utf-8"));
		StringBuffer buf = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null)
		{
			buf.append(line);
		}
		reader.close();
		httpclient.getConnectionManager().shutdown();
		log.info("buf" + buf);
		return buf.toString();
	}

	public static String reqPost(String requestUrl, String fullParam) throws Exception {
		//HttpClient httpclient =  HttpClients.createDefault();
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(requestUrl);
		StringEntity reqEntity = new StringEntity(fullParam, "UTF-8");
		// 设置类型
		reqEntity.setContentType("application/json");
		// 设置请求的数据
		httppost.setEntity(reqEntity);
		// 执行
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		log.info("----------------------------------------");

		log.info(response.getStatusLine().toString());
		if (entity != null)
		{
			log.info("Response content length: "
					+ entity.getContentLength());
		}
		// 显示结果
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent(), "utf-8"));
		StringBuffer buf = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null)
		{
			buf.append(line);
		}

		httpclient.close();
		reader.close();
		log.info("buf" + buf);
		return buf.toString();
	}

	public static String reqGet(String requestUrl) throws Exception
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(requestUrl);

		HttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		log.info("----------------------------------------");
		log.info(response.getStatusLine().toString());
		if (entity != null)
		{
			log.info("Response content length: "
					+ entity.getContentLength());
		}
		// 显示结果
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent(), "UTF-8"));
		StringBuffer str = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null)
		{
			str.append(line);
		}
		httpclient.getConnectionManager().shutdown();
		reader.close();
		log.info("backdata:" + str);
		return str.toString();
	}

	/**
	 * *************************************************************************
	 * ***********************************************
	 */

	/**
	 * 得到参数
	 * 
	 * @param map
	 * @return
	 */
	public static UrlEncodedFormEntity getparams(Map<String, String> map)
	{

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, String> enter = iter.next();
			params.add(new BasicNameValuePair(enter.getKey(), enter.getValue()));
		}

		// 将参数进行编码
		UrlEncodedFormEntity uefEntity = null;
		try
		{
			uefEntity = new UrlEncodedFormEntity(params, "UTF-8");
			uefEntity.setContentType("application/x-www-form-urlencoded");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return uefEntity;
	}

	public static String reqPostMsg(String requestUrl, Map<String, String> map)
			throws Exception
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(requestUrl);

		// 将参数打包
		UrlEncodedFormEntity uefEntity = getparams(map);
		
		
		// 设置请求的数据
		httppost.setEntity(uefEntity);
		
		// 执行
		HttpResponse response = httpclient.execute(httppost);
		
		HttpEntity entity = response.getEntity();
		log.info("----------------------------------------");

		log.info("status:"+response.getStatusLine());
		
		if (entity != null)
		{
			log.info("Response content length: "
					+ entity.getContentLength());
		}
		// 显示结果
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent(), "utf-8"));
		StringBuffer buf = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null)
		{
			buf.append(line);
		}
		reader.close();
		httpclient.getConnectionManager().shutdown();
		log.info("接口返回信息:" + buf);
		return buf.toString();
	}


    
	
}
