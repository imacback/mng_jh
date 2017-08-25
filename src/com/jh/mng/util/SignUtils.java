/**
 * project ossp-billplatform-common
 * package com.iflytek.ossp.billplatform.common.utils
 * file SignUtils.java
 *　author by scguo
 *　date 2015年6月8日 上午4:32:50　
 */

package com.jh.mng.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * @author scguo
 * @date 2015年6月8日 上午4:32:50
 */
public class SignUtils {
    private SignUtils() {
    }

    /**
     * 对请求的参数params使用key进行签名,签名的结果作为key:Sign的value部分存进map
     * 
     * @param params
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> sign(Map<String, String> params, String key) throws UnsupportedEncodingException {
        // 除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(params);
        // 生成签名结果
        String mysign = buildRequestMysign(sPara, key);

        // 签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);

        return sPara;
    }

    /**
     * 对请求的参数是用key进行签名.
     * 
     * @param sPara
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String buildRequestMysign(Map<String, String> sPara, String key) throws UnsupportedEncodingException {
        String prestr = createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        return DigestUtils.md5Hex((prestr + key).getBytes("UTF-8"));
    }

    /**
     * 过去掉Map中value为空或者null的参数,以及Sign参数
     * 
     * @param sArray
     * @return
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 对请求参数进行拼接和排序
     * 
     * @param params
     * @return
     */
    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
}
