package com.jh.mng.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

/**
 * 
 * @ClassName: RequestUtil
 * @Description:  request获取参数
 * @author gs
 * @date Nov 10, 2011 3:59:39 PM
 * 
 */
public class RequestUtil {

	public static Integer GetParamInteger(HttpServletRequest request,
			String name) {
		return RequestUtil.GetParamInteger(request, name, 0);
	}

	public static Integer GetParamInteger(HttpServletRequest request,
			String name, Integer val) {
		Integer ret = val;

		try {
			ret = new Integer(request.getParameter(name));
		} catch (NumberFormatException e) {
			// logger.warn("GetParamInteger " + name + " Error ! Return Default
			// : " + ret);
		}
		return ret;
	}

	public static Short GetParamShort(HttpServletRequest request, String name) {
		return RequestUtil.GetParamShort(request, name, new Short("0"));
	}

	public static Short GetParamShort(HttpServletRequest request, String name,
			Short val) {
		Short ret = val;

		try {
			ret = new Short(request.getParameter(name));
		} catch (NumberFormatException e) {
			// logger.warn("GetParamShort " + name + " Error ! Return Default :
			// " + ret);
		}
		return ret;
	}

	public static Long GetParamLong(HttpServletRequest request, String name) {
		return RequestUtil.GetParamLong(request, name, null);
	}

	public static Long GetParamLong(HttpServletRequest request, String name,
			Long val) {
		Long ret = val;
		try {
			String tempValue = request.getParameter(name);
			if (null != tempValue || tempValue != "") {
				ret = new Long(tempValue);
			} else {
				return ret;
			}
		} catch (NumberFormatException e) {
			// logger.warn("GetParamLong " + name + " Error ! Return Default : "
			// + ret);
		}
		return ret;
	}

	public static String GetParamString(HttpServletRequest request,
			String name, String val) {
		String ret = request.getParameter(name);
		if (ret == null) {
			// logger.warn("GetParamString '" + name + "' NULL ! Return Default
			// : " + val);
			return val;
		}
		if (ret == "") {
			return val;
		}
		ret.replace("\"", "&uml;");
		ret.replace("'", "&acute;");
		return ret;
	}

	public static String GetUploadString(
			HashMap<String, FileItem> mapUploadData, String name, String val) {
		if (mapUploadData != null && mapUploadData.containsKey(name)) {
			FileItem item = mapUploadData.get(name);
			if (item.isFormField()) {
				try {
					String ret = item.getString("utf-8");
					ret.replace("\"", "&uml;");
					ret.replace("'", "&acute;");
					return ret;
				} catch (UnsupportedEncodingException e) {
					// logger.error("UnsupportedEncodingException - " +
					// e.getMessage(), e);
				}
			}
		}
		// logger.warn("GetUploadString '" + name + "' NULL ! Return Default : "
		// + val);
		return val;
	}

	public static Integer GetUploadInteger(
			HashMap<String, FileItem> mapUploadData, String name, Integer val) {
		Integer ret = val;

		try {
			if (val == null)
				ret = new Integer(GetUploadString(mapUploadData, name, null));
			else
				ret = new Integer(GetUploadString(mapUploadData, name, val
						.toString()));
		} catch (NumberFormatException e) {
			// logger.warn("GetUploadInteger " + name + " Error ! Return Default
			// : " + ret);
		}

		return ret;
	}

	public static Short GetUploadShort(HashMap<String, FileItem> mapUploadData,
			String name, Short val) {
		Integer def = null;
		if (val != null)
			def = Integer.valueOf(val);

		Integer ret = GetUploadInteger(mapUploadData, name, def);
		if (ret == null)
			return null;

		return ret.shortValue();
	}

	public static FileItem GetUploadFileitem(
			HashMap<String, FileItem> mapUploadData, String name) {
		if (mapUploadData.containsKey(name)) {
			FileItem item = mapUploadData.get(name);

			if (!item.isFormField()) {
				return item;
			}
		}

		// logger.warn("GetUploadFileitem '" + name + "' NULL !");
		return null;
	}

	public static Long GetParamMoney(HttpServletRequest request, String name,
			Long val) {
		Long ret = val;
		try {
			String tempValue = request.getParameter(name);
			if (null != tempValue && tempValue.trim().length() != 0) {
				int n = tempValue.split("\\.").length - 1;
				if (null != tempValue && tempValue != "") {
					if (n == 1
							&& tempValue.substring(tempValue.indexOf(".") + 1)
									.length() == 2) {
						tempValue = tempValue.replace(".", "");
						ret = new Long(tempValue);
					} else if (n == 1
							&& tempValue.substring(tempValue.indexOf(".") + 1)
									.length() == 1) {
						tempValue = tempValue.replace(".", "");
						ret = new Long(tempValue + "0");
					} else if (tempValue.indexOf(".") == -1) {
						ret = new Long(tempValue + "00");
					}
				}
			}
		} catch (NumberFormatException e) {
			// logger.warn("GetParamMoney " + name + " Error ! Return Default :
			// " + ret);
		}
		return ret;
	}
}
