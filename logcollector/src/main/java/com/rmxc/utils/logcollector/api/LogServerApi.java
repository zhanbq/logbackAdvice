package com.rmxc.utils.logcollector.api;

import com.rmxc.utils.logcollector.hex.MD5;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class LogServerApi {
    private String appid;
    private String securityKey;

    public LogServerApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    private Map<String, String> buildParams(String query, String from, String to) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

}
