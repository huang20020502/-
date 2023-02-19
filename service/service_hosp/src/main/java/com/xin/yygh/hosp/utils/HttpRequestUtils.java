package com.xin.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequestUtils {

    public static Map<String,Object> switchMap(Map<String,String[]> map) {
        Map<String,Object> resultMap = new HashMap<>();

        Set<Map.Entry<String, String[]>> entries = map.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            resultMap.put(key,value);
        }

        return resultMap;
    }
}
