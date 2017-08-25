package com.jh.mng.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.common.StrIntBag;
import com.jh.mng.util.Base64Util;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.HttpTookit;

public class Test {

//	private static final Logger logger = Logger.getLogger(Test.class);
	/**
	 * @param args
	 * @throws ParseException 
	 * 
	 * 
	 */
	
	public Test() {
		System.out.println(Test.this);
	}
	
	public static void testSet2Array() {  
        Set<String> set = new HashSet<String>();  
        set.add("AA");  
        set.add("BB");  
        set.add("CC");  
          
        String[] arr = new String[set.size()];    
        //Set-->数组    
        set.toArray(arr);   
        
        System.out.println(arr.toString());
    } 
	
	public static void main(String[] args) throws Exception {
//		testSet2Array();
//		Integer a = 1;
//		Integer bInteger = 1;
//		
//		System.out.println(a == bInteger);
		
//		System.out.println(3 * 0.1);
		
//		String str = "675";
		
//		System.out.println("67500".indexOf(str) + str.length() + 1);
		
//		System.out.println("67501".substring(str.length(), "67501".indexOf(str) + 4));
//		Long times = System.currentTimeMillis();
//		
//		System.out.println(times);
//		System.out.println(times / 1000);
//		
//		Thread.sleep(1000 * 3);
//		
//		Long endTimes = System.currentTimeMillis();
//		
//		System.out.println( (endTimes - times ) / 1000);
		
//		Long a = 0L;
//		Long b = 0L;
//		System.out.println(a == b);
//		Double d = 0.01;
//		Double result = d * 100;
//		System.out.println(result.intValue());
//		testSendCode();
//		testRegister();
//		getPwd();
//		updateMiGu();
//		System.out.println(b("sjhlhlsdoubledan", "123_456"));
//		System.out.println(Test.this);
//		Calendar calendar = Calendar.getInstance();
//		
//		int month = calendar.get(Calendar.HOUR_OF_DAY);
//		
//		System.out.println(month);
//		int i = 0;
//		
//		i = 100 /35;
//		
//		System.out.println(i);
////		int max=92050010;
//        int min=92050001;
//        Random random = new Random();
//
//        int s = random.nextInt(max)%(max-min+1) + min;
//        
//        System.out.println(s);
		
//		String payUrl = "http://wap.dm.10086.cn/apay/orderHandle.jsp?RequestID=2319308&AppID=400000000240&PayCode=800000003264&TimeStamp=1449126620&ChannelID=700001809&Signature=MEY0Mzg0MzU1NzRDRTBDNzMwRDREMjVGQjAwNDg2QzY=";
//		
//		String payStr = payUrl.substring(payUrl.indexOf("PayCode=") + 8);
//		
//		System.out.println(payStr);
//		
//		String payCode = payStr.substring(0, payStr.indexOf("&"));
//		
//		System.out.println(payCode);
//		System.out.println("SSX9ECFJGN924951".substring(10));

//		String startDay = "20141201";
//		String endDay = "20150801";
//		
//		DateFormat df = new SimpleDateFormat("yyyyMMdd");
//		int num = DateTool.compareDate(startDay, endDay, "yyyyMMdd");
//		
//		System.out.println(num);
//		Calendar c = Calendar.getInstance();
//		
//		for (int i = 0; i <= num; i++) {
//			c.setTime(df.parse(startDay));
//			c.add(Calendar.MONTH, i);
//			System.out.println(DateTool.DateToString(c.getTime(), "yyyyMM"));
//		}
//		
//		List<String> groupList = new ArrayList<String>();
//		groupList.add("providerId");
//		groupList.add("spId");
//		
//		System.out.println(groupList.toString().substring(1, groupList.toString().length() -1));
		
//        System.out.println(Thread.currentThread().getContextClassLoader().getResource("/") + "application.properties"); 
//		File cf = new File("/E:/workspace/mng_jh/WebRoot/WEB-INF/classes/application.properties");
//		if (!cf.exists()){
//            cf.getParentFile().mkdirs();
//            try {
//				cf.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        }
		//1065889923
//		System.out.println(Base64Util.encode("zhuan8578".getBytes()));
//		System.out.println(new String(Base64Util.decode("zDfnWg49ilZ5MUqTpCX8tnEwrQVuSwYmG1D5SEimjWifn70H+y8AKTVB3d4KZxf0u+Ws1o08jCRmhnQhgsd3beClONfajdmMeRM+anvT1QMwzy6kWwHmeU/RKjsjKD0lMTGtI6eTVKETWjzgansKPOP3uxFdtd0dkJF9pDUCLiQWfKp33u+CBacJo/MGO9HblwFNi+IT9ThUrQmhgu2OWANsnEoU", "UTF-8")));
//		testKd();
//		testKdVerfiyCode();
//		testKdReport();
//		testIyd();
//		iydSmsConfirm();
//		testiydReport();
//		testHhOrder();
//		testHHConfirm();
//		testHHreport();
//		testJyRdoOrder();
//		testJyConfirm();
//		testAndDjLogin();
//		testAndDjFee();
//		testLDLogin();
//		testLDFee();
//		testLdReport();
//		testJyDDoOrder();
//		testJyDDoConfirm();
//		testJyReport();
//		String httpArg = "13800455701";
//		String jsonResult = Methods.request(httpArg);
//		System.out.println(jsonResult);
//		testXborder();
//		testXbConifrm();
//		testXbreport();
//		testHYorder();
//		testHYConifrm();
//		testHyReport();
//		testHyDdoOrder();
//		testHyDDoConfirm();
//		testHyDDoReport();
//		testMiguSdk();
//		testMiguFee();
//		testMiguSession();
//		testMM();
//		testDyyOrder();
//		testMmOrder();
//		testPcOrder();
//		testPcConfirm();
//		testPcPwd();
//		testIGameOrder();
//		testMMreport();
//		testUpdateCity();
//		testDyyNotify();
//		testLdOrder();
//		testldConfirm();
//		testLdRpeort();
//		testUpdateState();
//		testMmpageOrder();
//		testMmpageConfirm();
//		testDmWapOrder();
//		testDmWapVerifyCode();
//		testGsMo();
//		testMiguGameOrder();
//		testYgRdoorder();
//		testYgRdoConifrm();
		testYydorder();
//		testYydConifrm();
//		testHyDDOMonthOrder();
//		testHyDDOMonthConfirm();
	}
	
	//科大讯飞RDO测试
	public static void testKd() {
		String feecode = "CS001001";
		String imei = "123456";
		String imsi = "112133";
		String caller = "18811144190";
		String cpparam = "0812";
		
		String requestStr = "feecode=" + feecode + "&imei=" + imei + "&imsi=" + imsi + "&caller=" + caller + "&cpparam=" + cpparam;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/kdSubmitOrder.do?" + requestStr));
	}
	
	public static void testKdVerfiyCode() {
		String url = "http://wap.cmread.com/rdo/order/ncp?sign=D0BF2262E78DAB7BC26E182AEF1832C0&amp;ln=1584_11256__1_&amp;t1=16664&amp;orderNo=020150817105120500001&amp;cm=M3710016&amp;feeCode=81000301&amp;hash=5sfWB2U64IG1PoXvTRuHLQ%3D%3D&amp;msisdn=18811144190&amp;reqTime=20150817105120&amp;mcpid=kedaxunfei&amp;layout=9";
		String verifyCode = "471795";
		
		System.out.println(HttpClientUtil.execute(url + "&verifyCode=" + verifyCode, null));
	}
	
	public static void testKdReport() {
		String feecode = "414";
		String imei = "123456";
		String imsi = "112133";
		String caller = "13811455759";
		
		String requestStr = "feeCode=" + feecode + "&imei=" + imei + "&imsi=" + imsi + "&caller=" + caller;
		
		System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/kdReport.do", requestStr));
		
	}

	public static void testIyd() {
		String market_id = "99";
		String pay_code = "2";
		String phone = "15810206528";
		String amount = "200";
		String imei = "123456";
		String imsi = "112133";
		String cpparam = "01";
		
		String requestStr = "market_id=" + market_id + "&pay_code=" + pay_code + "&phone=" + phone + "&amount=" + amount
							+ "&imei=" + imei + "&imsi=" + imsi +  "&cpparam=" + cpparam;
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/iydOrder.do?"+requestStr));
	}
	
	public static void iydSmsConfirm() {
		String orderid = "14891312";
		String verify_code = "941538";
		
		String reString = "orderid=" + orderid + "&verify_code=" + verify_code;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/iydSmsConfirm.do?"+reString));
	}
	
	public static void testiydReport() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("app_id", "53fd59be416911e58786c6a10b512583");
		map.put("app_orderid", "12838913");
		map.put("ch_type", "240");
		map.put("is_monthly", "0");
		map.put("merc_id", "2000079");
		map.put("orderid", "FCS15081421215186");
		map.put("pay_amount", "200");
		map.put("pay_time", "20150814151401");
		map.put("phone", "18811144190");
		map.put("rec_amount", "200");
		map.put("sign", "62b0ed245cfb2f8b59e2b9bdcca8b0a6");
		map.put("status", "1");
		map.put("time", "1439536441");
		
		System.out.println(HttpTookit.doPost("http://localhost:8080/mng_jh/ds/iydReport.do", map));
		
	}
	
	public static void testHhOrder() {
		String mcpid = "xiugewh";
		String feeCode = "66000204";
		String cm = "M30I0007";
		String channelid = "5001";
		String mobile = "15810206528";
		String cpparam = "13312covvlw";
		
		String requestStr = "mcpid=" + mcpid ;
		requestStr += "&feecode=" + feeCode;
		requestStr += "&cm=" + cm;
		requestStr += "&channelid=" + channelid;
		requestStr += "&mobile=" + mobile;
		requestStr += "&cpparam=" + cpparam;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hhOrder.do?" + requestStr));
//		System.out.println(HttpClientUtil.executeGet("http://182.92.222.250:8080/mng_jh/ds/hhOrder.do?" + requestStr));
	}
	
	public static void testHHConfirm() {
		String orderid = "17141790";
		String verifycode = "190250";
		
		String requestStr = "orderid=" + orderid ;
		requestStr += "&verifycode=" + verifycode;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hhConfirm.do?" + requestStr));
	}
	
	public static void testHHreport() {
		String str = "orderno=20150924100341305512&feecode=66000204&cm=M30I0007&mobile=18811144190&price=4&channelid=5001&outorderid=14023782&resultcode=200";
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hxRdoReport.do?" + str));
	}
	
	public static void testJyRdoOrder() {
		String str = "subchannel=12&feecode=414&partnerid=100040&mobile=15810206528&returl=wap.baidu.com&imei=865342016417487&cpparam=12AEE122606804";
		System.out.println(HttpClientUtil.executeGet("http://182.92.222.250:8080/mng_jh/ds/jyRdoOrder.do?" + str));
	}
	
	public static void testJyConfirm() {
		String orderid = "13669542";
		String verifyCode = "870615";
		
		String requestStr = "orderid=" + orderid + "&verifyCode=" + verifyCode;
		System.out.println(HttpClientUtil.executeGet("http://182.92.222.250:8080/mng_jh/ds/jyRdoConfirm.do?" + requestStr));
	}
	
	public static void testAndDjLogin() throws UnsupportedEncodingException {
		JSONObject jObject = new JSONObject();
		jObject.put("ssid", "SSAXYQYL6V_013");
		jObject.put("imei", "353921053341806");
		jObject.put("imsi", "460021682548757");
		jObject.put("brand", "samsung");
		jObject.put("model", "GT-I9300");
		jObject.put("osbuild", "16");
		
		StrIntBag resp = HttpClientUtil.executeJson("http://218.242.153.106:3000/cmgbv4t/init", jObject.toJSONString());
		
		System.out.println(resp);
		
		JSONObject respObject = JSONObject.parseObject(resp._str);
		
		String init_b64 = respObject.getString("init_b64");
		
		init_b64 = new String(Base64Util.decode(init_b64));
		
		System.out.println(init_b64);
	}
	
	public static void testAndDjFee() throws UnsupportedEncodingException {
		JSONObject jObject = new JSONObject();
		jObject.put("ssid", "SSAXYQYL6V_013");
		jObject.put("cpparam", "0000000000abcdef");
		jObject.put("imsi", "460021682548757");
		
		StrIntBag resp = HttpClientUtil.executeJson("http://218.242.153.106:3000/cmgbv4t/getb", jObject.toJSONString());
		
		System.out.println(resp);
		
		JSONObject respObject = JSONObject.parseObject(resp._str);
		
		String billing_b64 = respObject.getString("billing_b64");
		
		billing_b64 = new String(Base64Util.decode(billing_b64));
		
		System.out.println(billing_b64);
	}
	
	public static void testLDLogin() throws UnsupportedEncodingException {
		
		String consumecode = "006037853001";
		String imei = "353921053341806";
		String mobile = "";
		String imsi = "460021682548757";
		String brand = "samsung";
		String model = "GT-I9300";
		String osbuild = "16";
		String cpparam = "af0000";
		
		String requestStr = "consumecode=" + consumecode ;
		requestStr += "&imei=" + imei;
		requestStr += "&mobile=" + mobile;
		requestStr += "&imsi=" + imsi;
		requestStr += "&brand=" + brand;
		requestStr += "&model=" + model;
		requestStr += "&osbuild=" + osbuild;
		requestStr += "&cpparam=" + cpparam;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/ldLogin.do?" + requestStr));
//		System.out.println(HttpClientUtil.executeGet("http://182.92.222.250:8080/mng_jh/ds/ldLogin.do?" + requestStr));
//		
//		
//		
//		JSONObject jObject = new JSONObject();
//		jObject.put("ssid", "SSX9ECFJGN_001");
//		jObject.put("imei", "353921053341806");
//		jObject.put("imsi", "460021682548757");
//		jObject.put("brand", "samsung");
//		jObject.put("model", "GT-I9300");
//		jObject.put("osbuild", "16");
//		
//		StrIntBag resp = HttpClientUtil.executeJson("http://218.242.153.106:3000/cmgbv4t/init", jObject.toJSONString());
//		
//		System.out.println(resp);
//		
//		JSONObject respObject = JSONObject.parseObject(resp._str);
//		
//		String init_b64 = respObject.getString("init_b64");
//		
//		init_b64 = new String(Base64Util.decode(init_b64));
//		
//		System.out.println(init_b64);
		
		
	}
	
	public static void testLDFee() throws UnsupportedEncodingException {
		String orderid = "15455580";
		
		String requestStr = "orderid=" + orderid ;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/ldFee.do?" + requestStr));
//		JSONObject jObject = new JSONObject();
//		jObject.put("ssid", "SSX9ECFJGN_001");
//		jObject.put("cpparam", "0000000000abcdef");
//		jObject.put("imsi", "460021682548757");
//		
//		StrIntBag resp = HttpClientUtil.executeJson("http://218.242.153.106:3000/cmgbv4t/getb", jObject.toJSONString());
//		
//		System.out.println(resp);
//		
//		JSONObject respObject = JSONObject.parseObject(resp._str);
//		
//		String billing_b64 = respObject.getString("billing_b64");
//		
//		billing_b64 = new String(Base64Util.decode(billing_b64));
//		
//		System.out.println(billing_b64);
	}
	
	public static void testLdReport() {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><userId>1501270357</userId><contentId>633716014263</contentId><consumeCode>006037853001</consumeCode><cpid>741337</cpid><hRet>0</hRet><status>1800</status><versionId>20121</versionId><cpparam>SSX9ECFJGNYD0101</cpparam><packageID /></request>";
		
		System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/ldReport.do", str));
	}
	
	public static void testJyDDoOrder() {
		String str = "subchannel=12&feecode=438&partnerid=100062&mobile=15810206528&imei=865342016417487&cpparam=12AEE122606811&returl=www.baidu.com";
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/jyDdoOrder.do?" + str));
//		System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/jyDdoOrder.do?" + str));
	}
	
	public static void testJyDDoConfirm() {
		String orderid = "16815032";
		String verifyCode = "414326";
		
		String requestStr = "orderid=" + orderid + "&vcode=" + verifyCode;
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/jyDdoConfirm.do?" + requestStr));
	}
	
	public static void testJyReport() {
		String str = "frontOrderNo=15414631&returnCode=0";
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/jyReport.do?" + str));
	}
	
	public static void testXborder() {
		String feecode = "66020002";
		String cpparam = "10";
		String phone = "15810206528";
		
		String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&phone=" + phone;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/xbOrder.do?" + str));
	}
	
	public static void testXbConifrm() {
		String verifyCode = "176699";
		String orderId = "14855924";
		
		String str = "verifyCode=" + verifyCode + "&orderId=" + orderId;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/xbVerifyCode.do?" + str));
	}
	
	public static void testXbreport() {
		String str = "fee=1000&sign=0B42EDCD18791AF021E77AAAA49C122B&orderNo=hy14855099&cm=M30I0014&feeCode=66000310&msisdn=15911750061&reqTime=20150924000016&resultCode=1&mcpid=xiugewh&payTime=20150924000016";
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/xbReport.do?" + str));
	}
	
	public static void testHYorder() {
		String feecode = "92050008";
		String cpparam = "12";
		String phone = "15201942907";
		String cm = "M20U0010";
		String mcpid = "aiyuemw";
		String redirectUrl= "www.baidu.com";
		
		String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&phone=" + phone + "&cm=" + cm + "&mcpid=" + mcpid + "&redirecturl=" + redirectUrl;
		
//		logger.info(DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		
//		System.out.println(HttpClientUtil.executeGet("http://114.215.159.123:8080/mng_jh/ds/hyRdoOrder.do?" + str));
//		System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/hyRdoOrder.do?" + str));
//		HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyRdoOrder.do?" + str);
//		logger.info(DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss"));s
//		System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/hyRdo.do?" + str));
		System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/rdoUrl.do?" + str));
	}
	
	public static void testHYConifrm() {
		String verifyCode = "163798";
		String orderId = "24596872";
		
		String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/rdoVerifyCode.do?" + str));
	}
	
	public static void testHyReport() {
		String str = "sign=07D32BB78A32BB38E9E470710F976220&fee=400&orderNo=18382441&cm=M20U0010&feeCode=92050004&msisdn=13844278981&reqTime=20160127125745&resultCode=1&mcpid=aiyuemw&payTime=20160127125744";
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyRdoReport.do?" + str));
	}
	
	public static void testHyDdoOrder() {
		String feecode = "701";
//		String feecode = "438";
		
		String cpparam = "35";
		String mobile = "18250850045";
		String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&mobile=" + mobile;
		System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/hyDdoOrder.do?" + str));
//		System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/hyDdoOrder.do?" + str));
	}
	
	
	
	public static void testHYDDoPwd() {
		String verifyCode = "909404";
		String orderId = "21498928";
		
		String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyDdoPwd.do?" + str));
//		System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/hyDdoPwd.do?" + str));
	}
	
	public static void testHyDDoUpdate() {
		String verifyCode = "320652";
		String orderId = "18450041";
		
		String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyDdoUpdate.do?" + str));
	}
	
	public static void testHyDDoConfirm() {
		String verifyCode = "017772";
		String orderId = "23855828";
		
		String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
		
		System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/hyDdoVerifyCode.do?" + str));
//		System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/hyDdoVerifyCode.do?" + str));
	}
	
	public static void testHyDDoReport() {
		String a = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><MsgType>ChargedNotifyReq</MsgType><TradeId>16082314493453557300</TradeId><AppId>300000007672</AppId><ProgramId></ProgramId><ApData>22993698</ApData><ExData>22993698</ExData><ChannelId>700002982</ChannelId><SubsId>361131166749705</SubsId><Cost>600</Cost><ChargeTime>2016-08-23 14:49:44</ChargeTime><ChargeType>103</ChargeType></Request>";
		
		System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/hyDdoReport.do", a));
	}
	
	
	public static StrIntBag execute(String uri, String requestBody){
		HttpClient client = new HttpClient();
		PostMethod post = null;
		StrIntBag bag = new StrIntBag();
		try {
			post = new PostMethod(uri);
			post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			post.getParams().setParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
			
			post.addRequestHeader("platform","android");
			post.addRequestHeader("loginType", "wifi");
			post.addRequestHeader("isGzip", "1");
			post.addRequestHeader("msg", "13_D##F7C516E8B0D33CB2EEA7DF714E24660037607F4786A953F9E5C9BAEACA8C1EA0E631EF79A9B08361D3DE00032DC1BD08468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
			post.addRequestHeader("login_tag", "-1");
			post.addRequestHeader("version", "4.6.1258");
			post.addRequestHeader("simid", "A454853BEC3A35F0D1A4E5FA1972190D");
			post.addRequestHeader("isAnonymous", "true");
			post.addRequestHeader("province_id", "");
			post.addRequestHeader("deviceid", "C0F8F156EC2780581907733CCF3C0762");
			post.addRequestHeader("channel", "0000");
			
			if(requestBody != null){
				RequestEntity reqEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
				post.setRequestEntity(reqEntity);
			}
			int status = client.executeMethod(post);
			String resp = post.getResponseBodyAsString();
			bag._str = resp;
			bag._int = status;
			return bag;
		} catch (Exception e) {
			e.printStackTrace();
//			log.error(Strings.throwableToString(e));
		} finally{
			if(post != null){
				post.releaseConnection();
			}
		}
		return bag;
	}
	
	 public static void testSendCode() { 
         HttpClient client = new HttpClient(); 
			PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/new_send_message_to_get_vertification_code.hisun");
	    	NameValuePair[] data = {
	                new NameValuePair("mobile_num", "40948D108AD6842272A86583CE9B841C"), //13811455759
//	                new NameValuePair("mobile_num", "EF8849FE2ADEB304BE30AB434C381CD0"), 
	                
	                new NameValuePair("type", "2"), //注册TYPE：2 找回密码TYPE：3 8:升级账号
	                new NameValuePair("is_supportIDMP", "True"),
	                new NameValuePair("isRechargeEmail", "0"),
	        };
	    	phonePostMethod.addRequestHeader("platform","android");
//	    	phonePostMethod.addRequestHeader("loginType", "wifi");
	    	phonePostMethod.addRequestHeader("isGzip", "1");
//	    	phonePostMethod.addRequestHeader("msg", "12_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
//	    	phonePostMethod.addRequestHeader("msg", "13_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
	    	phonePostMethod.addRequestHeader("login_tag", "-1");
	    	phonePostMethod.addRequestHeader("version", "4.7.1259");
//	    	phonePostMethod.addRequestHeader("simid", "A454853BEC3A35F0D1A4E5FA1972190D"); //imsi
	    	phonePostMethod.addRequestHeader("province_id", "");
//	    	phonePostMethod.addRequestHeader("deviceid", "C0F8F156EC2780581907733CCF3C0762"); //时间 System
	    	phonePostMethod.addRequestHeader("channel", "0000");
			
	    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//	    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
	    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
	    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//	    	phonePostMethod.addRequestHeader("Host", "wap.dm.10086.cn");
	    	phonePostMethod.setRequestBody(data);
	    	
         try { 
        	 client.executeMethod(phonePostMethod);
        	 String resp = getResponseBodyAsString(phonePostMethod);
//        	 
        	 System.out.println("resp========:" + resp);
         } catch (Exception e) { 
         } finally { 
//        	 post.releaseConnection(); 
         } 
 } 
	 
	 public static void testRegister() {
		 HttpClient client = new HttpClient(); 
		 PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/register_user.hisun");
	    	NameValuePair[] data = {
	                new NameValuePair("new_password", "C74FF7B8A69DDABFCC90A452E79053E2"),
//	                new NameValuePair("mobile_num", "A00017AB46EA098AE0DBC06176FF6AA3"),
//	                new NameValuePair("is_encrypted", "true"),
	                new NameValuePair("mobile_num", "A00017AB46EA098AE0DBC06176FF6AA3"),
	                new NameValuePair("is_encrypted", "true"),
	                new NameValuePair("validate_code", "266071"),
	                new NameValuePair("is_supportIDMP", "True"),
	                new NameValuePair("sessionID", "I3u189B0wLWfdIC0xMxJYWeh4SvYvG7i"),
	                new NameValuePair("disable_migu", "0"),
	        };
	    	phonePostMethod.addRequestHeader("platform","android");
//	    	phonePostMethod.addRequestHeader("loginType", "wifi");
	    	phonePostMethod.addRequestHeader("isGzip", "1");
//	    	phonePostMethod.addRequestHeader("msg", "12_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
//	    	phonePostMethod.addRequestHeader("msg", "13_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
	    	phonePostMethod.addRequestHeader("login_tag", "-1");
	    	phonePostMethod.addRequestHeader("version", "4.7.1259");
//	    	phonePostMethod.addRequestHeader("simid", "A454853BEC3A35F0D1A4E5FA1972190D"); //imsi
	    	phonePostMethod.addRequestHeader("province_id", "");
//	    	phonePostMethod.addRequestHeader("deviceid", "C0F8F156EC2780581907733CCF3C0762"); //时间 System
	    	phonePostMethod.addRequestHeader("channel", "0000");
			
	    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//	    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
	    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
	    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//	    	phonePostMethod.addRequestHeader("Host", "wap.dm.10086.cn");
	    	phonePostMethod.setRequestBody(data);
	    	
      try { 
     	 client.executeMethod(phonePostMethod);
     	 String resp = getResponseBodyAsString(phonePostMethod);
//     	 
     	 System.out.println("resp========:" + resp);
      } catch (Exception e) { 
      } finally { 
      } 
	 }
	 
	 
	 public static void getPwd() {
		 HttpClient client = new HttpClient(); 
		 PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/forget_password.hisun");
	    	NameValuePair[] data = {
	                new NameValuePair("new_password", "C74FF7B8A69DDABFCC90A452E79053E2"),
	                new NameValuePair("mobile", "EF8849FE2ADEB304BE30AB434C381CD0"),
	                new NameValuePair("is_encrypted", "true"),
	                new NameValuePair("validate_code", "145392"),
	                new NameValuePair("is_supportIDMP", "True"),
	                new NameValuePair("sessionID", "w0wWvPTQkA8F5tJwmXrOlcuID82vdHvC"),
	                new NameValuePair("disable_migu", "0"),
	                new NameValuePair("type", "0"),
	        };
	    	phonePostMethod.addRequestHeader("platform","android");
//	    	phonePostMethod.addRequestHeader("loginType", "wifi");
	    	phonePostMethod.addRequestHeader("isGzip", "1");
//	    	phonePostMethod.addRequestHeader("msg", "12_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
//	    	phonePostMethod.addRequestHeader("msg", "13_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
	    	phonePostMethod.addRequestHeader("login_tag", "-1");
	    	phonePostMethod.addRequestHeader("version", "4.7.1259");
//	    	phonePostMethod.addRequestHeader("simid", "A454853BEC3A35F0D1A4E5FA1972190D"); //imsi
	    	phonePostMethod.addRequestHeader("province_id", "");
//	    	phonePostMethod.addRequestHeader("deviceid", "C0F8F156EC2780581907733CCF3C0762"); //时间 System
	    	phonePostMethod.addRequestHeader("channel", "0000");
			
	    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//	    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
	    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
	    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//	    	phonePostMethod.addRequestHeader("Host", "wap.dm.10086.cn");
	    	phonePostMethod.setRequestBody(data);
	    	
      try { 
     	 client.executeMethod(phonePostMethod);
     	 String resp = getResponseBodyAsString(phonePostMethod);
//     	 {"code":"2","msg":"咪咕动漫帐号已全面升级为咪咕帐号！为了您更好地体验咪咕业务，请马上升级。升级后，一个咪咕帐号便可畅享所有咪咕业务。"}
    //{"code":"0","msg":"新密码设置成功"} 	
     	 System.out.println("resp========:" + resp);
      } catch (Exception e) { 
      } finally { 
      } 
	 }
	 
	 
	 public static void updateMiGu() {
		 HttpClient client = new HttpClient(); 
		 PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/upgrade_to_migu.hisun");
	    	NameValuePair[] data = {
	                new NameValuePair("new_password", "C74FF7B8A69DDABFCC90A452E79053E2"),
	                new NameValuePair("mobile_num", "EF8849FE2ADEB304BE30AB434C381CD0"),
	                new NameValuePair("password", "0BC7696E46A9AA2AF7A66A01177AE7B9"),
	                new NameValuePair("email", ""),
	                new NameValuePair("validate_code", "367788"),
	                new NameValuePair("is_supportIDMP", "True"),
	                new NameValuePair("sessionID", "GwWnZSmCYRAEVqcw8dSaCNrT1iWt55VS"),
	                new NameValuePair("uSessionID", ""),
	        };
	    	phonePostMethod.addRequestHeader("platform","android");
//	    	phonePostMethod.addRequestHeader("loginType", "wifi");
	    	phonePostMethod.addRequestHeader("isGzip", "1");
//	    	phonePostMethod.addRequestHeader("msg", "12_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
//	    	phonePostMethod.addRequestHeader("msg", "13_7##72E5842657BB42C7E330B92B79431B4C96DEDAE000BC44F09E98246B22D50B84F0F94CBAEF925BFB11819ED92A216E5A468A14847F84E744F5BFFA00A3D6A805B2E9C7171B21ACC37BA8CB83929480E6");
	    	phonePostMethod.addRequestHeader("login_tag", "-1");
	    	phonePostMethod.addRequestHeader("version", "4.7.1259");
//	    	phonePostMethod.addRequestHeader("simid", "A454853BEC3A35F0D1A4E5FA1972190D"); //imsi
	    	phonePostMethod.addRequestHeader("province_id", "");
//	    	phonePostMethod.addRequestHeader("deviceid", "C0F8F156EC2780581907733CCF3C0762"); //时间 System
	    	phonePostMethod.addRequestHeader("channel", "0000");
			
	    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//	    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
	    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
	    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//	    	phonePostMethod.addRequestHeader("Host", "wap.dm.10086.cn");
	    	phonePostMethod.setRequestBody(data);
	    	
      try { 
     	 client.executeMethod(phonePostMethod);
     	 String resp = getResponseBodyAsString(phonePostMethod);
//     	 {"code":"2","msg":"咪咕动漫帐号已全面升级为咪咕帐号！为了您更好地体验咪咕业务，请马上升级。升级后，一个咪咕帐号便可畅享所有咪咕业务。"}
    //{"code":"0","msg":"新密码设置成功"} 	
     	 System.out.println("resp========:" + resp);
      } catch (Exception e) { 
      } finally { 
      } 
	 }
	 
	    private static String getResponseBodyAsString(HttpMethod postMethod) throws Exception {
	        if (postMethod.getResponseBody() != null) {
	            if(postMethod.getResponseHeader("Content-Encoding") != null
	                    && postMethod.getResponseHeader("Content-Encoding").getValue().toLowerCase().indexOf("gzip") > -1) {
//	                For GZip response
	                InputStream is = postMethod.getResponseBodyAsStream();
	                GZIPInputStream gzin = new GZIPInputStream(is);
	                
	                InputStreamReader isr = new InputStreamReader(gzin, "UTF-8");
	                java.io.BufferedReader br = new java.io.BufferedReader(isr);
	                StringBuffer sb = new StringBuffer();
	                String tempbf;
	                while ((tempbf = br.readLine()) != null) {
	                    sb.append(tempbf);
//	                    sb.append("/r/n");
	                }
	                isr.close();
	                gzin.close();
	                return sb.toString();
	            } else {
	                return postMethod.getResponseBodyAsString();
	            }
	        } else {
	            return null;
	        }
	        
	    }
	    
	    public static String b(String paramString1, String paramString2)
	    throws Exception
	  {
//	    SecretKeySpec localSecretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1", new PBKDF2WithHmacSHA1Provider()).generateSecret(new PBEKeySpec(paramString1.toCharArray(), paramString1.getBytes(), 128, 128)).getEncoded(), "AES/ECB/PKCS5Padding");
//	    Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//	    localCipher.init(1, localSecretKeySpec);
//	    if (paramString2 != null)
//	      return a(localCipher.doFinal(paramString2.getBytes()));
	    return null;
	  }
	    
	    public static String a(byte[] paramArrayOfByte)
	    {
	      if (paramArrayOfByte == null)
	        return "";
	      StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
	      for (int i = 0; i < paramArrayOfByte.length; i++)
	        a(localStringBuffer, paramArrayOfByte[i]);
	      return localStringBuffer.toString();
	    }
	    
	    private static void a(StringBuffer paramStringBuffer, byte paramByte)
	    {
	      paramStringBuffer.append("0123456789ABCDEF".charAt(0xF & paramByte >> 4)).append("0123456789ABCDEF".charAt(paramByte & 0xF));
	    }
	    
	    
	    public static void testMiguSdk() {
//	    	System.out.println("kO84OjbWyLhsbvJRICkEh4SeDSg=".length());
	    	HttpClient client = new HttpClient(); 
			 PostMethod phonePostMethod = new PostMethod("http://drm.cmgame.com:81/migusdk/verification/checkSdkUpdate");
		    	NameValuePair[] data = {
//		                new NameValuePair("en_billingreq", "zDfnWg49ilZ5MUqTpCX8tnEwrQVuSwYmG1D5SEimjWifn70H+y8AKTVB3d4KZxf0u+Ws1o08jCRmhnQhgsd3beClONfajdmMeRM+anvT1QMwzy6kWwHmeU/RKjsjKD0lMTGtI6eTVKETWjzgansKPOP3uxFdtd0dkJF9pDUCLiQWfKp33u+CBacJo/MGO9HblwFNi+IT9ThUrQmhgu2OWANsnEoU"),
//		                new NameValuePair("ctype", "4"),
//		                new NameValuePair("transId", "051b325950d34289a58582af9e19d5fa"),
//		                new NameValuePair("mobile_num", "A00017AB46EA098AE0DBC06176FF6AA3"),
//		                new NameValuePair("is_encrypted", "true"),
//		                new NameValuePair("validate_code", "266071"),
//		                new NameValuePair("is_supportIDMP", "True"),
		                new NameValuePair("versionCode", "12001000"),
		                new NameValuePair("type", "1"),
		        };
//		    	phonePostMethod.addRequestHeader("Header-Signature","kO84OjbWyLhsbvJRICkEh4SeDSg=");
//		    	phonePostMethod.addRequestHeader("Header-Key", "&apiVersion=1.0&SDKVersion=12001000");
		    	phonePostMethod.addRequestHeader("isGzip", "1");
		    	phonePostMethod.addRequestHeader("Accept", "application/xml");
		    	phonePostMethod.addRequestHeader("Response-Type", "xml");
		    	phonePostMethod.addRequestHeader("apiVersion", "1.0");
		    	phonePostMethod.addRequestHeader("platform", "Android");
		    	phonePostMethod.addRequestHeader("SDKVersion", "12001000"); //imsi
		    	phonePostMethod.addRequestHeader("imsi", "867247020554928");
		    	phonePostMethod.addRequestHeader("imei", "460000243158096"); //时间 System
		    	phonePostMethod.addRequestHeader("X-OF-Signature", "kO84OjbWyLhsbvJRICkEh4SeDSg=");
				
		    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//		    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
		    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
		    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		    	phonePostMethod.addRequestHeader("Host", "drm.cmgame.com:81");
		    	phonePostMethod.setRequestBody(data);
		    	
	      try { 
	     	 client.executeMethod(phonePostMethod);
	     	 String resp = getResponseBodyAsString(phonePostMethod);
//	     	 
	     	 System.out.println("resp========:" + new String(resp.getBytes("ISO-8859-1"), "UTF-8"));
	      } catch (Exception e) { 
	    	  e.printStackTrace();
	      } finally { 
	      } 
	    }
	    
	    public static void testMiguFee() {
	    	HttpClient client = new HttpClient(); 
			 PostMethod phonePostMethod = new PostMethod("http://drm.cmgame.com:81/migusdk/charge/doCharge");
		    	NameValuePair[] data = {
		                new NameValuePair("en_billingreq", "zDfnWg49ilZ5MUqTpCX8tnEwrQVuSwYmG1D5SEimjWifn70H+y8AKTVB3d4KZxf0u+Ws1o08jCRmhnQhgsd3beClONfajdmMeRM+anvT1QMwzy6kWwHmeU/RKjsjKD0lMTGtI6eTVKETWjzgansKPOP3uxFdtd0dkJF9pDUCLiQWfKp33u+CBacJo/MGO9HblwFNi+IT9ThUrQmhgu2OWANsnEoU"),
		                new NameValuePair("ctype", "4"),
		                new NameValuePair("transId", "051b325950d34289a58582af9e19d5fa"),
//		                new NameValuePair("mobile_num", "A00017AB46EA098AE0DBC06176FF6AA3"),
//		                new NameValuePair("is_encrypted", "true"),
//		                new NameValuePair("validate_code", "266071"),
//		                new NameValuePair("is_supportIDMP", "True"),
//		                new NameValuePair("versionCode", "12001000"),
//		                new NameValuePair("type", "1"),
		        };
//		    	phonePostMethod.addRequestHeader("Header-Signature","kO84OjbWyLhsbvJRICkEh4SeDSg=");
//		    	phonePostMethod.addRequestHeader("Header-Key", "&apiVersion=1.0&SDKVersion=12001000");
		    	phonePostMethod.addRequestHeader("isGzip", "1");
		    	phonePostMethod.addRequestHeader("Accept", "application/xml");
		    	phonePostMethod.addRequestHeader("Response-Type", "xml");
		    	phonePostMethod.addRequestHeader("apiVersion", "1.0");
		    	phonePostMethod.addRequestHeader("platform", "Android");
		    	phonePostMethod.addRequestHeader("SDKVersion", "12001000"); //imsi
		    	phonePostMethod.addRequestHeader("imsi", "867247020554928");
		    	phonePostMethod.addRequestHeader("imei", "460000243158096"); //时间 System
		    	phonePostMethod.addRequestHeader("X-OF-Signature", "iXbq9aDcowvOIyXdqyxlwe2l13w=");
				
		    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//		    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
		    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
		    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		    	phonePostMethod.addRequestHeader("Host", "drm.cmgame.com:81");
		    	phonePostMethod.setRequestBody(data);
		    	
	      try { 
	     	 client.executeMethod(phonePostMethod);
	     	 String resp = getResponseBodyAsString(phonePostMethod);
//	     	 
	     	 System.out.println("resp========:" + new String(resp.getBytes("ISO-8859-1"), "UTF-8"));
	      } catch (Exception e) { 
	    	  e.printStackTrace();
	      } finally { 
	      } 
	    }
	    
	    public static void testMiguSession() {
	    	HttpClient client = new HttpClient(); 
			 PostMethod phonePostMethod = new PostMethod("http://drm.cmgame.com:81/migusdk/charge/getSession");
		    	NameValuePair[] data = {
		                new NameValuePair("en_sessionreq", "zDfnWg49ilZ5MUqTpCX8tnEwrQVuSwYmG1D5SEimjWifn70H+y8AKTVB3d4KZxf0u+Ws1o08jCRmhnQhgsd3beClONfajdmMeRM+anvT1QMwzy6kWwHmeU/RKjsjKD0lMTGtI6eTVKETWjzgansKPOP3uxFdtd0dkJF9pDUCLiQWfKp33u+CBacJo/MGO9HblwFNi+IT9ThUrQmhgu2OWANsnEoU"),
//		                new NameValuePair("ctype", "4"),
//		                new NameValuePair("transId", "051b325950d34289a58582af9e19d5fa"),
//		                new NameValuePair("mobile_num", "A00017AB46EA098AE0DBC06176FF6AA3"),
//		                new NameValuePair("is_encrypted", "true"),
//		                new NameValuePair("validate_code", "266071"),
//		                new NameValuePair("is_supportIDMP", "True"),
//		                new NameValuePair("versionCode", "12001000"),
//		                new NameValuePair("type", "1"),
		        };
//		    	phonePostMethod.addRequestHeader("Header-Signature","kO84OjbWyLhsbvJRICkEh4SeDSg=");
//		    	phonePostMethod.addRequestHeader("Header-Key", "&apiVersion=1.0&SDKVersion=12001000");
		    	phonePostMethod.addRequestHeader("isGzip", "1");
		    	phonePostMethod.addRequestHeader("Accept", "application/xml");
		    	phonePostMethod.addRequestHeader("Response-Type", "xml");
		    	phonePostMethod.addRequestHeader("apiVersion", "1.0");
		    	phonePostMethod.addRequestHeader("platform", "Android");
		    	phonePostMethod.addRequestHeader("SDKVersion", "12001000"); //imsi
		    	phonePostMethod.addRequestHeader("imsi", "867247020554928");
		    	phonePostMethod.addRequestHeader("imei", "460000243158096"); //时间 System
		    	phonePostMethod.addRequestHeader("X-OF-Signature", "4kZUOS0ZxkhaiIvkidtlCH9JDwQ=");
				
		    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//		    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
		    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
		    	phonePostMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		    	phonePostMethod.addRequestHeader("Host", "drm.cmgame.com:81");
		    	phonePostMethod.setRequestBody(data);
		    	
	      try { 
	     	 client.executeMethod(phonePostMethod);
	     	 String resp = getResponseBodyAsString(phonePostMethod);
//	     	 
	     	 System.out.println("resp========:" + new String(resp.getBytes("ISO-8859-1"), "UTF-8"));
	      } catch (Exception e) { 
	    	  e.printStackTrace();
	      } finally { 
	      } 
	    }
	    
	    public static void testMM() {
	    	HttpClient client = new HttpClient(); 
			 PostMethod phonePostMethod = new PostMethod("http://da.mmarket.com/mmsdk/mmsdk?func=mmsdk:posteventlog&appkey=300009181706&channel=2200146258&code=106");
//		    	NameValuePair[] data = {
//		                new NameValuePair("en_sessionreq", "zDfnWg49ilZ5MUqTpCX8tnEwrQVuSwYmG1D5SEimjWifn70H+y8AKTVB3d4KZxf0u+Ws1o08jCRmhnQhgsd3beClONfajdmMeRM+anvT1QMwzy6kWwHmeU/RKjsjKD0lMTGtI6eTVKETWjzgansKPOP3uxFdtd0dkJF9pDUCLiQWfKp33u+CBacJo/MGO9HblwFNi+IT9ThUrQmhgu2OWANsnEoU"),
////		                new NameValuePair("ctype", "4"),
////		                new NameValuePair("transId", "051b325950d34289a58582af9e19d5fa"),
////		                new NameValuePair("mobile_num", "A00017AB46EA098AE0DBC06176FF6AA3"),
////		                new NameValuePair("is_encrypted", "true"),
////		                new NameValuePair("validate_code", "266071"),
////		                new NameValuePair("is_supportIDMP", "True"),
////		                new NameValuePair("versionCode", "12001000"),
////		                new NameValuePair("type", "1"),
//		        };
////		    	phonePostMethod.addRequestHeader("Header-Signature","kO84OjbWyLhsbvJRICkEh4SeDSg=");
////		    	phonePostMethod.addRequestHeader("Header-Key", "&apiVersion=1.0&SDKVersion=12001000");
//		    	phonePostMethod.addRequestHeader("isGzip", "1");
//		    	phonePostMethod.addRequestHeader("Accept", "application/xml");
//		    	phonePostMethod.addRequestHeader("Response-Type", "xml");
//		    	phonePostMethod.addRequestHeader("apiVersion", "1.0");
//		    	phonePostMethod.addRequestHeader("platform", "Android");
//		    	phonePostMethod.addRequestHeader("SDKVersion", "12001000"); //imsi
//		    	phonePostMethod.addRequestHeader("imsi", "867247020554928");
//		    	phonePostMethod.addRequestHeader("imei", "460000243158096"); //时间 System
//		    	phonePostMethod.addRequestHeader("X-OF-Signature", "4kZUOS0ZxkhaiIvkidtlCH9JDwQ=");
				
		    	phonePostMethod.addRequestHeader("connection", "keep-alive");
//		    	phonePostMethod.addRequestHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)");
		    	phonePostMethod.addRequestHeader("Accept-Encoding", "gzip");
		    	phonePostMethod.addRequestHeader("Content-Type", "application/octet-stream; charset=UTF-8");
//		    	phonePostMethod.addRequestHeader("Host", "drm.cmgame.com:81");
//		    	phonePostMethod.setRequestBody(data);
		    	
	      try { 
	     	 client.executeMethod(phonePostMethod);
	     	 String resp = getResponseBodyAsString(phonePostMethod);
//	     	 
	     	 System.out.println("resp:===========" + resp);
	     	 System.out.println("resp========:" + new String(resp.getBytes("ISO-8859-1"), "UTF-8"));
	      } catch (Exception e) { 
	    	  e.printStackTrace();
	      } finally { 
	      } 
	    }
	    
	    public static void testDyyOrder() {
	    	String imei = "867247020554928";
			String feecode = "420596601";
			String imsi = "460000243158096";
//			String channelid = "700002982";
			String phone = "";
			String cpparam = "13312covvlw";
//			String start_time = String.valueOf(System.currentTimeMillis());
			
			String requestStr = "imei=" + imei ;
			requestStr += "&feecode=" + feecode;
			requestStr += "&imsi=" + imsi;
//			requestStr += "&channelid=" + channelid;
			requestStr += "&mobile=" + phone;
			requestStr += "&cpparam=" + cpparam;
//			requestStr += "&start_time=" + start_time;
			
//			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyDDoDyyOrderBase64.do?" + requestStr));
//			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyDDoDyyOrder.do?" + requestStr));
			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/newDDoDyyOrder.do?" + requestStr));
	    }
	    
	    public static void testDyyNotify() {
	    	String str = "tel=18856577048&status=0&price=1000&cpparam=20954587&imsi=460078565542655&linkId=jjdm1000&transId=5bce903876e84986abb1d50727fabb55&chid=youyu";
	    	System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/hyDdoDyyNotity.do?" + str));
	    }
	    
	    public static void testMmOrder() {
	    	String imei = "860471030412359";
			String feecode = "30000923240903";
			String imsi = "460028084242716";
			String channelid = "704";
			String phone = "";
			String cpparam = "3899scZM9GQZTDLL";
			String ip = "117.177.26.105";
			String appid = "300009232409";
			
			String requestStr = "imei=" + imei ;
			requestStr += "&feecode=" + feecode;
			requestStr += "&imsi=" + imsi;
			requestStr += "&appid=" + appid;
			requestStr += "&mobile=" + phone;
			requestStr += "&cpparam=" + cpparam;
			requestStr += "&channelid=" + channelid;
			requestStr += "&ip=" + ip;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/mmOrder.do?" + requestStr));
	    }
	    
	    public static void testPcOrder() {
	    	String feecode = "006125612001";
			String cpparam = "3810S4UXRUU4";
			String mobile = "13811455759";
			String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&mobile=" + mobile;
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/pcOrder.do?" + str));
//			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/pcOrder.do?" + str));
	    }
	    
	    public static void testPcConfirm(){
	    	String verifyCode = "420037";
			String orderId = "24655862";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/pcConfirm.do?" + str));
	    }
	    
	    public static void testPcPwd() {
	    	String verifyCode = "984615";
			String orderId = "19832362";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/pcPwd.do?" + str));
	    }
	    
	    public static void testIGameOrder() {
			String feecode = "121100463022";
			String imsi = "460000243158096";
			String cpparam = "13312covvlw";
			
			String requestStr = "";
			
			requestStr += "feecode=" + feecode;
			requestStr += "&imsi=" + imsi;
			requestStr += "&cpparam=" + cpparam;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/aiyxOrder.do?" + requestStr));
	    }
	    
	    public static void testMMreport() {
	    	String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    		+"<SyncAppOrderReq xmlns=\"http://www.monternet.com/dsmp/schemas/\">"
+"<TransactionID>CSSP18631465</TransactionID>"
+"<MsgType>SyncAppOrderReq</MsgType>"
+"<Version>1.0.0</Version>"
+"<Send_Address>"
+"<DeviceType>200</DeviceType>"
+"<DeviceID>CSSP</DeviceID>"
+"</Send_Address>"
+"<Dest_Address>"
+"<DeviceType>1002</DeviceType>"
+"<DeviceID>f0_0</DeviceID>"
+"</Dest_Address>"
+"<OrderID>11160511162248791664</OrderID>"
+"<CheckID>0</CheckID>"
+"<ActionTime>20160511162248</ActionTime>"
+"<ActionID>1</ActionID>"
+"<MSISDN></MSISDN>"
+"<FeeMSISDN>4DB9656DB919BDA2</FeeMSISDN>"
+"<AppID>300009017961</AppID>"
+"<PayCode>30000901796120</PayCode>"
+"<TradeID>2D1B8F1706</TradeID>"
+"<Price>200</Price>"
+"<TotalPrice>200</TotalPrice>"
+"<SubsNumb>1</SubsNumb>"
+"<SubsSeq>1</SubsSeq>"
+"<ChannelID>2200192053</ChannelID>"
+"<ExData>A20160511900145</ExData>"
+"<OrderType>1</OrderType>"
+"<MD5Sign>19FBC24E25122D0CF242BAAE4F0C6A59</MD5Sign>"
+"<OrderPayment>1</OrderPayment>"
+"<ReturnStatus>0</ReturnStatus>"
+"<ProvinceID>28</ProvinceID>"
+"</SyncAppOrderReq>";
	    	
	    	System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/mmNotify.do", str));
	    }
	    
	    
	    public static void testUpdateCity() {
	    	String str = "month=1605&gameId=521";
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/getCityIncome.do?" + str));
	    }
	    
	    public static void testUpdateState() {
	    	String str = "day=2016-07-20&gameId=581&chnlId=46";
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/updateStateIncome.do?" + str));
	    }
	    
	    public static void testLdOrder() {
	    	String feecode = "001";
	    	String amount = "10";
			String cpparam = "13012covvlw";
			String mobile = "";
			String ip = "61.178.152.181";
			String imei = "865342016417487";
			
			String requestStr = "";
			
			requestStr += "feecode=" + feecode;
			requestStr += "&imei=" + imei;
			requestStr += "&cpparam=" + cpparam;
			requestStr += "&amount=" + amount;
			requestStr += "&mobile=" + mobile;
			requestStr += "&ip=" + ip;
			
			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/ldOrder.do?" + requestStr));
	    }
	    
	    public static void testldConfirm() {
	    	String verifyCode = "6910001";
			String orderId = "22263990";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			
			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/ldConfirmCode.do?" + str));
	    	
	    }
	    
	    public static void testLdRpeort() {
	    	String str = "merid=5171&goodsid=001&appid=&channelid=&orderid=21531076&orderdate=20160630&transdate=20160630&amount=10&amttype=02&banktype=3&mobileid=13811455759&transtype=0&settledate=20160630&merpriv=&retCode=0000&upversion=1.1&sign=f2f8ec01d0df22231529f0f451c5b624";
	    	System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/ldNotify.do?" + str));
	    }
	    

		public static void testMmpageOrder() {
			String feecode = "emddy";
			String cpparam = "13012covvlw";
//			String mobile = "13810878435";
//			String mobile = "13811455759";
			String mobile = "15810206528";
			
			String requestStr = "";
			
			requestStr += "feecode=" + feecode;
			requestStr += "&cpparam=" + cpparam;
			requestStr += "&mobile=" + mobile;
			
//			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/mmPageOrder.do?" + requestStr));
			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/newMmPageOrder.do?" + requestStr));
//			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/newMmPageOrder.do?" + requestStr));
//			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/mmPageOrder.do?" + requestStr));
		}
		
		public static void testMmpageConfirm() {
			String verifyCode = "167581";
			String orderId = "24246547";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
//			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/mmPageConfirm.do?" + str));
			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/newMmPageConfirm.do?" + str));
//			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/mmPageConfirm.do?" + str));
		}
		
		public static void testMmpageVerify() {
			String req = "<?xml version=\"1.0\" encoding=\"gbk\"?><VertifyUserState2APReq><APTransactionID>23128506</APTransactionID><APId>10959</APId><ServiceId>40614</ServiceId><ServiceType>31</ServiceType><ChannelId>10960</ChannelId><APContentId>hanjoys</APContentId><APUserId>MTM4MTE0NTU3NTk=</APUserId><OrderType>0</OrderType><Actiontime>2016-09-05 18:05:42</Actiontime><method /><signMethod>DSA</signMethod><sign>MCwCFF5g/+WJrsf25Syeu906nkTcHyxYAhR2moP8GXGoP6+rahAGDrwkYRo7eg==</sign><Msisdn>23012799460</Msisdn><Province>10</Province><Backup1 /><Backup2 /></VertifyUserState2APReq>";
			
			System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/mmPageVertify.do", req));
		}
		
		public static void testMmpageNotify() {
			String req = "<?xml version=\"1.0\" encoding=\"gbk\"?><ServiceWebTransfer2APReq><APTransactionID>23133204</APTransactionID><APId>10959</APId><ServiceId>40614</ServiceId><ServiceType>31</ServiceType><ChannelId>10960</ChannelId><APContentId>hanjoys</APContentId><APUserId>MTg4MjM0MDAxOTE=</APUserId><OrderType>0</OrderType><Actiontime>2016-09-06 10:30:50</Actiontime><ServiceAction>0</ServiceAction><method /><signMethod>DSA</signMethod><sign>MCwCFFVZ+ktHfu5S2f+R2bK+hmDooxm4AhRmPKkzkDCaSRwt2cZbl8O+fAF2kg==</sign><Msisdn>28024144492</Msisdn><Province>20</Province><Backup1>11160906102602022415</Backup1><Backup2 /></ServiceWebTransfer2APReq>";
			System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/mmPageNotify.do", req));
		}
		
		public static void testDmWapOrder() {
			String feecode = "166849";
			String cpparam = "35";
			String mobile = "13552010609";
			String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&mobile=" + mobile;
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/dmWapOrder.do?" + str));
//			System.out.println(HttpClientUtil.executeGet("http://101.201.148.52:13888/mng_jh/ds/hyDdoOrder.do?" + str));
		}
		
		public static void testDmWapVerifyCode() {
			String verifyCode = "744178";
			String orderId = "23189644";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/dmWapVerifyCode.do?" + str));
		}
		
		public static void testGsMo() {
			String req = "<?xml version=\"1.0\" encoding=\"gb2312\"?><MASP><DATA_TYPE>CO_SMS_MO</DATA_TYPE><TRANSACTIONID>U12161026145811620</TRANSACTIONID><SRC_TERMID>18587665123</SRC_TERMID><DST_SERVICECODE>106699677</DST_SERVICECODE><CONTENT>675A2</CONTENT><SERVICEID>SHBD</SERVICEID><FEE_CODE>200</FEE_CODE><MOSTATUS>0</MOSTATUS><MOSTATUSMEMO>正常上行MO</MOSTATUSMEMO><PROVINCE>广西</PROVINCE><CITY>南宁</CITY><SENDTIME>2016-10-26 14:58:32</SENDTIME></MASP>";
			System.out.println(HttpClientUtil.execute("http://localhost:8080/mng_jh/ds/gsMo.do", req));
		}
		
	    public static void testMiguGameOrder() {
			String feecode = "343562515";
			String imsi = "867247020554928";
			String imei = "460000243158096";
			String cpparam = "13312covvlw";
			String ip = "114.248.123.156";
			
			String requestStr = "";
			
			requestStr += "feecode=" + feecode;
			requestStr += "&imsi=" + imsi;
			requestStr += "&imei=" + imei;
			requestStr += "&cpparam=" + cpparam;
			requestStr += "&ip=" + ip;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/miguOrder.do?" + requestStr));
	    }
	    
		public static void testYgRdoorder() {
			String feecode = "66000202";
			String cpparam = "12";
			String phone = "18250850045";
			String cm = "M30I0007";
			String mcpid = "xiugewh";
			
			String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&phone=" + phone + "&cm=" + cm + "&mcpid=" + mcpid;
			
//			System.out.println(HttpClientUtil.executeGet("http://114.215.159.123:8080/mng_jh/ds/hyRdoOrder.do?" + str));
			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/sunRdoOrder.do?" + str));
		}
		
		public static void testYgRdoConifrm() {
			String verifyCode = "617862";
			String orderId = "24587440";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/sunRdoVerifyCode.do?" + str));
		}
		
		public static void testYydorder() {
			String feecode = "92390002";
			String cpparam = "12";
			String phone = "15199820870";
			String mcpid = "yd0058";
			String cm = "M23S0004";
			
			String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&phone=" + phone + "&mcpid=" + mcpid + "&cm=" + cm ;
			
//			System.out.println(HttpClientUtil.executeGet("http://114.215.159.123:8080/mng_jh/ds/hyRdoOrder.do?" + str));
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/yydOrder.do?" + str));
		}
		
		public static void testYydConifrm() {
			String verifyCode = "155465";
			String orderId = "24624536";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8888/mng_jh/ds/yydVerifyCode.do?" + str));
		}
		
		public static void testHyDDOMonthOrder() {
			String feecode = "401489901";
			String cpparam = "12";
			String phone = "18250850045";
			String chid = "12";
			
			String str = "feecode=" + feecode + "&cpparam=" + cpparam + "&phone=" + phone + "&chid=" + chid;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/ddoOrder.do?" + str));
		}
		
		public static void testHyDDOMonthConfirm() {
			String verifyCode = "950429";
			String orderId = "24637183";
			
			String str = "verifycode=" + verifyCode + "&orderid=" + orderId;
			
			System.out.println(HttpClientUtil.executeGet("http://localhost:8080/mng_jh/ds/ddoVerifyCode.do?" + str));
		}
}
