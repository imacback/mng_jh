package com.jh.mng.util;

import java.io.UnsupportedEncodingException;


public class Base64Util {
	private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '+', '/' };

	private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
			60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
			10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
			-1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
			38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1,
			-1, -1 };

	// 编码
	public static String encode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int len = data.length;
		int i = 0;
		int b1, b2, b3;
		while (i < len) {
			b1 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
				sb.append("==");
				break;
			}
			b2 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[((b1 & 0x03) << 4)
						| ((b2 & 0xf0) >>> 4)]);
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
				sb.append("=");
				break;
			}
			b3 = data[i++] & 0xff;
			sb.append(base64EncodeChars[b1 >>> 2]);
			sb.append(base64EncodeChars[((b1 & 0x03) << 4)
					| ((b2 & 0xf0) >>> 4)]);
			sb.append(base64EncodeChars[((b2 & 0x0f) << 2)
					| ((b3 & 0xc0) >>> 6)]);
			sb.append(base64EncodeChars[b3 & 0x3f]);
		}
		return sb.toString();
	}

	// 解码
	public static byte[] decode(String str) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		byte[] data = str.getBytes("US-ASCII");
		int len = data.length;
		int i = 0;
		int b1, b2, b3, b4;
		while (i < len) {
			/* b1 */
			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1)
				break;
			/* b2 */
			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1)
				break;
			sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
			/* b3 */
			do {
				b3 = data[i++];
				if (b3 == 61)
					return sb.toString().getBytes("iso8859-1");
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1)
				break;
			sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
			/* b4 */
			do {
				b4 = data[i++];
				if (b4 == 61)
					return sb.toString().getBytes("iso8859-1");
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1)
				break;
			sb.append((char) (((b3 & 0x03) << 6) | b4));
		}
		return sb.toString().getBytes("iso8859-1");
	}
	
	public static byte[] decode(String str, String byteCode, String code) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		byte[] data = str.getBytes(byteCode);
		int len = data.length;
		int i = 0;
		int b1, b2, b3, b4;
		while (i < len) {
			/* b1 */
			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1)
				break;
			/* b2 */
			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1)
				break;
			sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
			/* b3 */
			do {
				b3 = data[i++];
				if (b3 == 61)
					return sb.toString().getBytes(code);
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1)
				break;
			sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
			/* b4 */
			do {
				b4 = data[i++];
				if (b4 == 61)
					return sb.toString().getBytes(code);
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1)
				break;
			sb.append((char) (((b3 & 0x03) << 6) | b4));
		}
		return sb.toString().getBytes(code);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
//		String transido = String.valueOf(System.currentTimeMillis() /1000);
//		System.out.println(Base64Util.encode("p1KN>-tFN11122".getBytes()));
//		System.out.println(Base64Util.encode(MD5Util.getUpperCaseMd5("1&12001000&1.0&" + transido).getBytes()));
//		System.out.println(Base64Util.encode(("2&12001000&1.0&12001000").getBytes()));
//		System.out.println(new String(Base64Util.decode("MIIEaDADAgEAMIIEXwYJKoZIhvcNAQcCoIIEUDCCBEwCAQExCzAJBgUrDgMCGgUAMGwGCyqGSIb3DQEJEAEEoF0EWzBZAgEBMCEwCQYFKw4DAhoFAAQUvN2iK9JBmOqrXHqjnqZgUjyC7e0EAAICAX4XDTExMDMzMTA0MDMxOVowCQIBAAIBAAIBAAEB/wIEO5rJ/4AIY2NpdC1UU0GgggLmMIIC4jCCAk2gAwIBAgIDNI+BMAsGCSqGSIb3DQEBBTA1MQswCQYDVQQGEwJDTjENMAsGA1UECgwEQ01DQTEXMBUGA1UEAwwOQ01DQSBTZXJ2ZXIgQ0EwHhcNMTEwMzE0MTUwMjE5WhcNMTMwMzE0MTUwMjE5WjBHMQswCQYDVQQGEwJDTjEcMBoGA1UEAwwTQ01DQeaXtumXtOaIs+acjeWKoTEaMBgGA1UEBRMRMjAxMTAzMTcwNTIyNDkyMDQwgZ0wCwYJKoZIhvcNAQEBA4GNADCBiQKBgQCx0CvFDvJm5GnytuMEsAJ/x8nW2Tw8uLgEks8wiRqlqYxaHWelg+439uag/FMlN+6a1qRJpGXwsF7UuGX2Z3oZdXliV9hAJxjyQQ7YQxCEhMcRQgzhSlPdnC+dixoW/N1tw8GwJX+WOscDoSqV/aQlhEr5LMCXJneGHxRAXiAudQIDAQABo4HzMIHwMCoGA1UdJQQjMCEGCCsGAQUFBwMBBglghkgBhvhCBAEGCisGAQQBgjcKAwMwHwYDVR0jBBgwFoAU1FQ+mDhD2u+CMWj5hLp2wTlkLkQwdQYDVR0fBG4wbDBqoC2gK4YpaHR0cDovL3d3dy5jbWNhLm5ldC9kb3dubG9hZC9jcmwvQ1JMMy5jcmyiOaQ3MDUxFzAVBgNVBAMMDkNNQ0EgU2VydmVyIENBMQ0wCwYDVQQKDARDTUNBMQswCQYDVQQGEwJDTjALBgNVHQ8EBAMCALEwHQYDVR0OBBYEFPoJYzXQJeoUE+m2NOZlV322wREgMAsGCSqGSIb3DQEBBQOBgQAkB2XKmjh58xJly1g+47vgU8KV+Zvy5ZcMy2mNztKzelPaC4lSMaVghTeIzHiUrK+i0F3ArTdBgETqjp58ciH0SLKI3FarTEl9cSJ4Ksefm7HWpEOx2/tHEyWXYmyt2DESVB963s7hlsiq6T2YG/TEvg/RQtWJdyONKDuzy3lCpDGB4TCB3gIBATA8MDUxCzAJBgNVBAYTAkNOMQ0wCwYDVQQKDARDTUNBMRcwFQYDVQQDDA5DTUNBIFNlcnZlciBDQQIDNI+BMAkGBSsOAwIaBQAwDQYJKoZIhvcNAQEBBQAEgYCB8W1NlbW1ERSTdXgkQVC4TJdx+dHrOROmDfk4Hy44U6zvulNc2HQERo4/4rWcS/K7yy8o0QBPnvqpNfJDO8gQ/9fdIOcpupkDkSGnYYC6Gbb7DIrF+YblDJ1EMS45dN+Qqxtw1DZ5uiot44Zd7aCnC2fFb0fjKBMhUFGezVoeFQ==", "gb2312", "UTF-8")));
		System.out.println(new String(Base64Util.decode("MDAwMDYzNDU4NlAyNDcvJTA1NTQ5Mjg0ZjAwYTAyNDNNRDhwOTYiWlQ3aGlrRWVNTk0wTCpNIk13 QkooPT00O2hlMTd+KzNDNzQxOD5RMjApUm02MDJkMjU0MDJlTTAwMHwwSDAwMDAwMSZ5ckhGJ3Qx VXRqUHdtQmpBUlJYa2xRbktPPg==")));
	}
}
