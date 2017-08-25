package com.jh.mng.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.hisunflytone.framwork.encrypt.pbe.PBKDF2WithHmacSHA1Provider;

public class DdoSign {

	
	public static String encode(String needSignStr) {
		String sign = "";
		try {
			sign = b("sjhlhlsdoubledan", needSignStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}
	
	 public static String b(String paramString1, String paramString2)
	    throws Exception
	  {
	    SecretKeySpec localSecretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1", new PBKDF2WithHmacSHA1Provider()).generateSecret(new PBEKeySpec(paramString1.toCharArray(), paramString1.getBytes(), 128, 128)).getEncoded(), "AES/ECB/PKCS5Padding");
	    Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    localCipher.init(1, localSecretKeySpec);
	    if (paramString2 != null)
	      return a(localCipher.doFinal(paramString2.getBytes()));
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
	    
	    public static void main(String[] args) {
			System.out.println(encode("13811455759"));
		}
}


