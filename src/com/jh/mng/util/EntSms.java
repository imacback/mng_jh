package com.jh.mng.util;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EntSms {
	private static final Logger logger = Logger.getLogger(EntSms.class);
	final static String DEST_URL = "这里防止企信通的接口地址";

  public EntSms() {
  }

  public static boolean SendEmail(String emaillist,String subject,String body)
  {
	  boolean ret = false;
	  
	  CMail mail = new CMail("smtp.126.com","wang_kjava","kjavawang","wang_kjava@126.com");
	  
	  mail.setSubject(subject);
	  mail.setContentText(body);
	  mail.setTo(emaillist);
	  int r = mail.send();
	  
	  if ( r == 1 )
	  {
		  ret = true;
	  }
	  return ret;
  }
  public static String SendSms(String phones,String smstext)
  {
    String ret = "";

    int p1 = 0;
    int p2 = 0;

    HttpClient client = new HttpClient();
    GetMethod getMethod = null;

    try
    {
      long time1 = System.currentTimeMillis();

      logger.info(DEST_URL+"&mobile="+phones+"&content="+java.net.URLEncoder.encode(smstext,"UTF-8"));
      
      getMethod = new GetMethod(DEST_URL+"&mobile="+phones+"&content="+java.net.URLEncoder.encode(smstext,"UTF-8"));

      int statusCode = client.executeMethod(getMethod);
      long time2 = System.currentTimeMillis();
      if ( ((time2-time1)/1000) > 5 )
      {
    	  logger.info("too slow: " + (time2 - time1) / 1000);
      }

      String response = new String(getMethod.getResponseBody());
      response = new String(getMethod.getResponseBody(),"UTF-8");

      p1 = response.indexOf("<returnstatus>");
      p2 = response.indexOf("</returnstatus>",p1);
      if ( p1 > 0 && p2 > 0 )
      {
        ret = response.substring(p1+14,p2);
      }

      p1 = response.indexOf("<taskID>");
      p2 = response.indexOf("</taskID>",p1);
      if ( p1 > 0 && p2 > 0 )
      {
        ret = ret + "\t"+response.substring(p1+8,p2);
      }

      System.out.println(new java.util.Date()+":response="+statusCode+","+response);
    }catch(Exception e)
    {
      e.printStackTrace();
    }

    getMethod.releaseConnection();
    getMethod = null;

    return ret;
  }

  public static void main(String args[])
  {
    
  }
}
