package com.hb.encrypt.wrapper;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.hb.encrypt.utils.HttpParametersUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * parameter request wrapper
 *
 * @author hz18123767
 */

public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ParameterRequestWrapper.class);

    private final Map<String, String[]> parameterMap;

    public ParameterRequestWrapper(HttpServletRequest request, String parameters) {
        super(request);
        this.parameterMap = strToMap(request, parameters);
    }


    private Map<String, String[]> strToMap(HttpServletRequest request, String parameters) {
        logger.info("decrypt parameters -> {}", parameters);
        if (HttpMethod.GET.matches(request.getMethod()) || HttpMethod.DELETE.matches(request.getMethod())) {
            return HttpParametersUtil.getUrlParams(parameters);
        }
        return JSONUtil.toBean(parameters, new TypeReference<Map<String, String[]>>() {}, Boolean.FALSE);
    }


    @Override
    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }


    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<>(parameterMap.keySet());
        vector.addAll(parameterMap.keySet());
        return vector.elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = parameterMap.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values;
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        try {
            if (values == null) {
                values = parameterMap.get(name);
            }
        } catch (Throwable e) {
            logger.error("ParameterRequestWrapper getParameter() error", e);
        }
        if (null == values) {
            return null;
        }
        return values.length > 0 ? values[0] : super.getParameter(name);
    }
}
