package com.jh.mng.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtils {
	private static File file = null;

	/**
	 * 读取图像的二进制流
	 * 
	 * @param infile
	 * @return
	 */
	public static FileInputStream getByteImage(String infile) {
		FileInputStream inputImage = null;
		file = new File(infile);
		try {
			inputImage = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return inputImage;
	}

	/**
	 * 输出图片
	 * 
	 * @param inputStream
	 * @param path
	 */
	public static void readBlob(FileInputStream inputStream, String path) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, len);// 写
			}
			inputStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readImg(InputStream inputStream, String path) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, len);// 写
			}
			inputStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void downLoadImg(String imgPath,String savePath){
		//测试地址，"http://m.1more.com/image/pic1_ok.jpg";
		BufferedReader reader = null;
		try {
			URL url = new URL(imgPath);
			URLConnection conn = url.openConnection();
			ImageUtils.readImg(conn.getInputStream(), savePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}
}