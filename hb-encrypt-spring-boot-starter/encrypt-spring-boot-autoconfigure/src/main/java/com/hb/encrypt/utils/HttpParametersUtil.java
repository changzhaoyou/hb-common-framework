package com.hb.encrypt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * http params util
 *
 * @author hz18123767
 */
public class HttpParametersUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpParametersUtil.class);

    private static final String BLANK = "";

    private static final String STRING_NUll = "null";

    /**
     * Get method request parameter to map
     *
     * @param param Get method params
     * @return Map<String, String [ ]>
     */
    public static Map<String, String[]> getUrlParams(String param) {
        Map<String, String[]> map = new HashMap<>();
        if (StringUtils.isEmpty(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (String s : params) {
            String[] p = s.split("=");
            if (!StringUtils.isEmpty(p[0])) {
                String key = p[0];
                String value = p[1];
                logger.info("parameter key->{},value->{}", key, value);
                if (BLANK.equals(p[1]) || STRING_NUll.equals(p[1]) || null == p[1]) {
                    continue;
                }
                String[] values = map.get(key);
                if (values != null) {
                    Set<String> oldValue = new HashSet<>(Arrays.asList(values));
                    if (p[1].contains(",")) {
                        String[] splitValues = p[1].split(",");
                        Set<String> newValue = new HashSet<>(Arrays.asList(splitValues));
                        oldValue.addAll(newValue);
                        String[] appendValue = oldValue.stream().toArray(String[]::new);
                        map.put(key, appendValue);
                    } else {
                        oldValue.add(p[1]);
                        String[] appendValue = oldValue.toArray(new String[0]);
                        map.put(key, appendValue);
                    }
                    continue;
                }
                map.put(key, new String[]{p[1]});
            }
        }
        return map;
    }

}
