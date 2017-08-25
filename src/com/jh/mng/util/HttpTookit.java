package com.jh.mng.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.URIException;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.params.HttpMethodParams;
//import org.apache.commons.httpclient.util.URIUtil;
//import org.apache.commons.lang.StringUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class HttpTookit {
	private static Logger log = Logger.getLogger(HttpTookit.class); 

    /** 
     * 执行一个HTTP GET请求，返回请求响应的HTML 
     * 
     * @param url                 请求的URL地址 
     * @param queryString 请求的查询参数,可以为null 
     * @param charset         字符集 
     * @param pretty            是否美化 
     * @return 返回请求响应的HTML 
     */ 
    public static String doGet(String url, String queryString, String charset, boolean pretty) { 
            StringBuffer response = new StringBuffer(); 
            HttpClient client = new HttpClient(); 
            HttpMethod method = new GetMethod(url); 
            try { 
                    if (StringUtils.isNotBlank(queryString)) 
                            //对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串 
                            method.setQueryString(URIUtil.encodeQuery(queryString)); 
                    client.executeMethod(method); 
                    if (method.getStatusCode() == HttpStatus.SC_OK) { 
                            BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset)); 
                            String line; 
                            while ((line = reader.readLine()) != null) { 
                                    if (pretty) 
                                            response.append(line).append(System.getProperty("line.separator")); 
                                    else 
                                            response.append(line); 
                            } 
                            reader.close(); 
                    } 
            } catch (URIException e) { 
                    log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e); 
            } catch (IOException e) { 
                    log.error("执行HTTP Get请求" + url + "时，发生异常！", e); 
            } finally { 
                    method.releaseConnection(); 
            } 
            return response.toString(); 
    } 

    /** 
     * 执行一个HTTP POST请求，返回请求响应的HTML 
     * 
     * @param url         请求的URL地址 
     * @param params    请求的查询参数,可以为null 
     * @param charset 字符集 
     * @param pretty    是否美化 
     * @return 返回请求响应的HTML 
     */ 
    public static String doPost(String url, Map<String, String> params, String charset, boolean pretty) { 
            StringBuffer response = new StringBuffer(); 
            HttpClient client = new HttpClient(); 
            HttpMethod method = new PostMethod(url); 
            //设置Http Post数据 
            if (params != null) { 
                    HttpMethodParams p = new HttpMethodParams(); 
                    for (Map.Entry<String, String> entry : params.entrySet()) { 
                            p.setParameter(entry.getKey(), entry.getValue()); 
                    } 
                    method.setParams(p); 
            } 
            try { 
                    client.executeMethod(method); 
                    if (method.getStatusCode() == HttpStatus.SC_OK) { 
                            BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset)); 
                            String line; 
                            while ((line = reader.readLine()) != null) { 
                                    if (pretty) 
                                            response.append(line).append(System.getProperty("line.separator")); 
                                    else 
                                            response.append(line); 
                            } 
                            reader.close(); 
                    } 
            } catch (IOException e) { 
                    log.error("执行HTTP Post请求" + url + "时，发生异常！", e); 
            } finally { 
                    method.releaseConnection(); 
            } 
            return response.toString(); 
    } 

    public static void main(String[] args) { 
            String y = doGet("http://video.sina.com.cn/life/tips.html", null, "GBK", true); 
            System.out.println(y); 
    } 
    
    public static String doPost(String url, Object data)  {

        CloseableHttpClient httpClient = null;
//        if (ssl){
////            httpClient = getSslHttpClient();
//        }else{
//            
//        }

        httpClient = HttpClients.createDefault();
        // 目标地址
        HttpPost httpPost = new HttpPost(url);

        try{
            //ByteArrayEntity postDataEntity = new ByteArrayEntity(data.getBytes());
            HttpEntity he = getHttpEntity(data);
            httpPost.setEntity(he);

            // 执行
            HttpResponse response = httpClient.execute(httpPost);   //targetHost

            HttpEntity entity = response.getEntity();

            // 显示结果
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
            return result;
        }catch(Exception e){
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	private static HttpEntity getHttpEntity(Object data) throws UnsupportedEncodingException {
		List<NameValuePair> paramPairs = getNameValuePairs((Map<String, String>) data);
		HttpEntity he = new UrlEncodedFormEntity(paramPairs, "UTF-8");
        return he;
    }

    private static List<NameValuePair> getNameValuePairs(Map<String, String> data) {
        Map<String, String> params = (Map<String, String>)data;
        List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
        if(params!=null && !params.isEmpty()){
            for(Map.Entry<String, String> entry : params.entrySet()){
                BasicNameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue());
                paramPairs.add(param);
            }
        }
        return paramPairs;
    }

    /**
     * 获取重定向地址
     * @param url
     * @param data
     * @return
     */
    public static String methodPost(String url,org.apache.commons.httpclient.NameValuePair[] data){  
    	String location = "";
//        String response= "";//要返回的response信息  
        HttpClient httpClient = new HttpClient();  
        PostMethod postMethod = new PostMethod(url);  
        // 将表单的值放入postMethod中  
        postMethod.setRequestBody(data);  
        // 执行postMethod  
        int statusCode = 0;  
        try {  
            statusCode = httpClient.executeMethod(postMethod);  
        } catch (HttpException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发  
        // 301或者302  
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY  
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
            // 从头中取出转向的地址  
            Header locationHeader = postMethod.getResponseHeader("location");  
//            String location = null;  
            if (locationHeader != null) {  
                location = locationHeader.getValue();  
//                log.info("The page was redirected to:" + location);  
//                response= methodPost(location,data);//用跳转后的页面重新请求。  
            } else {  
                log.error("Location field value is null.");  
            }  
        } else {  
        	log.info(postMethod.getStatusLine());  
  
//            try {  
////                response= postMethod.getResponseBodyAsString();  
//            } catch (IOException e) {  
//                e.printStackTrace();  
//            }  
            postMethod.releaseConnection();  
        }  
        return location;  
    } 
}
