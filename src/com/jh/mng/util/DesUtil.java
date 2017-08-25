package com.jh.mng.util;

import javax.crypto.*;  
import javax.crypto.spec.DESKeySpec;  
 
import java.security.SecureRandom;  
 
/**  
* 通过DES加密解密实现一个String字符串的加密和解密.  
*   
* @author badpeas  
*   
*/ 
public class DesUtil {
	
	private final static String DES = "DES";
	
	public static void main(String[] args) {

	    String encryptString= encrypt("is米食米客","wozhidao米食米客对方蜂蜜@qqcomc大幅度的deed");
	    System.out.println(encryptString);
	    
	    String desencryptString = decrypt("XK0z8AVIwnQ=","E1027BFD1B3D497869974FB4AD7CE185");
        System.out.println(desencryptString);
	}
	
	/**
    *
    * @param src 数据源
    * @param key 密钥，长度必须是8的倍数
    * @return
    * @throws Exception
    */
   public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
       // DES算法要求有一个可信任的随机数源
       SecureRandom sr = new SecureRandom();
       // 从原始密匙数据创建一个DESKeySpec对象
       DESKeySpec dks = new DESKeySpec(key);
       // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成一个SecretKey对象
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
       SecretKey securekey = keyFactory.generateSecret(dks);
       // Cipher对象实际完成解密操作
       Cipher cipher = Cipher.getInstance(DES);
       // 用密匙初始化Cipher对象
       cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

       // 正式执行解密操作
       return cipher.doFinal(src);
   }

   public final static String decrypt(String data, String key) {
       try {
           return new String(decrypt(String2byte(data.getBytes()), key.getBytes()));
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }

   public static byte[] String2byte(byte[] b) {
       if ((b.length % 2) != 0)
           throw new IllegalArgumentException("长度不是偶数");
       byte[] b2 = new byte[b.length / 2];
       for (int n = 0; n < b.length; n += 2) {
           String item = new String(b, n, 2);
           b2[n / 2] = (byte) Integer.parseInt(item, 16);
       }
       return b2;
   }
	
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
	    // DES算法要求有一个可信任的随机数源
	    SecureRandom sr = new SecureRandom();

	    // 从原始密匙数据创建DESKeySpec对象
	    DESKeySpec dks = new DESKeySpec(key);

	    // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
	    SecretKey securekey = keyFactory.generateSecret(dks);

	    // Cipher对象实际完成加密操作
	    Cipher cipher = Cipher.getInstance(DES);

	    // 用密匙初始化Cipher对象
	    cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

	    // 执行加密操作
	    return cipher.doFinal(src);
	}

	public final static String encrypt(String password, String key) {

	    try {
	        return byte2String(encrypt(password.getBytes(), key.getBytes()));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	private static String byte2String(byte[] b) {
	    String hs="";
	    String stmp="";
	    for(int n=0;n<b.length;n++){
	        stmp=(java.lang.Integer.toHexString(b[n]&0XFF));
	        if(stmp.length() == 1)
	            hs+=hs+"0"+stmp;
	        else
	            hs=hs+stmp;
	    }
	    return hs.toUpperCase();
	}
 
 
} 