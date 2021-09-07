package com.hb.encrypt.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hb.encrypt.domain.RequestBody;
import com.hb.encrypt.enums.ResultCodeEnum;
import com.hb.encrypt.exception.RSAException;
import com.hb.encrypt.result.Result;
import com.hb.encrypt.utils.RSAUtils;
import com.hb.encrypt.wrapper.ParameterRequestWrapper;
import com.hb.encrypt.wrapper.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * RsaFilter 加解密验过滤器
 *
 * @author zhangzhaoyou
 */
public class RSAFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(RSAFilter.class);

    private final List<String> excludes = new ArrayList<>();

    private boolean enabled = false;

    private String publicKey;

    private String privateKey;

    private boolean timestampCheck;

    @Override
    public void init(FilterConfig filterConfig) {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        String tempEnabled = filterConfig.getInitParameter("enabled");
        String tempPrivateKey = filterConfig.getInitParameter("privateKey");
        String tempPublicKey = filterConfig.getInitParameter("publicKey");
        String tempTimestampCheck = filterConfig.getInitParameter("timestampCheck");
        if (enabled && (publicKey == null || privateKey == null)) {
            throw new RSAException("private or public  key is null ");
        }
        if (!StringUtils.isEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(",");
            excludes.addAll(Arrays.asList(url));
        }
        if (!StringUtils.isEmpty(tempEnabled)) {
            enabled = Boolean.parseBoolean(tempEnabled);
        }
        if (!StringUtils.isEmpty(tempPublicKey)) {
            publicKey = tempPublicKey;
        }
        if (!StringUtils.isEmpty(tempPrivateKey)) {
            privateKey = tempPrivateKey;
        }

        if (!StringUtils.isEmpty(timestampCheck)) {
            timestampCheck = Boolean.parseBoolean(tempTimestampCheck);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (!enabled || handleExcludeURL(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        RequestBody requestBody = getRequestParameter(httpServletRequest);
        log.info("requestBody:{}", requestBody);
        Optional<String> decryptData = verifyDecryptSign(response, requestBody);
        if (!decryptData.isPresent()) return;
        request = new ParameterRequestWrapper(httpServletRequest, decryptData.get());
        ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
        chain.doFilter(request, responseWrapper);
        responseBody(responseWrapper);
    }

    /**
     * Request parameter decryption and signature verification
     *
     * @param response    ServletResponse
     * @param requestBody Request parameter
     * @return decrypt data
     * @throws IOException io exception
     */
    private Optional<String> verifyDecryptSign(ServletResponse response, RequestBody requestBody) throws IOException {
        if (StrUtil.isEmpty(requestBody.getParams()) || StrUtil.isEmpty(requestBody.getSign())) {
            log.error("argument params or sign is null");
            responseJson(response, ResultCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION);
            return Optional.empty();
        }
        String decryptData;
        boolean verify;
        try {
            decryptData = RSAUtils.decrypt(requestBody.getParams(), privateKey);
            verify = RSAUtils.verify(decryptData, publicKey, requestBody.getSign());
        } catch (Exception e) {
            log.error("rsa filter decrypt or sign exception : {}", e.getMessage(), e);
            responseJson(response, ResultCodeEnum.RSA_CHECK_EXCEPTION);
            return Optional.empty();
        }
        log.info("sign verify result {}", verify);
        if (!verify) {
            responseJson(response, ResultCodeEnum.RSA_SIGN_EXCEPTION);
            return Optional.empty();
        }
        return Optional.of(decryptData);
    }

    /**
     * return encrypt body
     *
     * @param responseWrapper ResponseWrapper
     * @throws IOException io exception
     */
    private void responseBody(ResponseWrapper responseWrapper) throws IOException {
        byte[] content = responseWrapper.getContent();
        if (content != null) {
            String responseJson = new String(content, StandardCharsets.UTF_8);
            String encryptJson;
            try {
                encryptJson = RSAUtils.encrypt(responseJson, publicKey);
            } catch (Exception e) {
                log.error("rsa filter encrypt exception : {}", e.getMessage(), e);
                responseJson(responseWrapper, ResultCodeEnum.RSA_CHECK_EXCEPTION);
                return;
            }
            log.info("responseJson:{},encryptJson:{}", responseJson, encryptJson);
            outputStream(responseWrapper, encryptJson);
        }
    }

    /**
     * get Request Parameter
     *
     * @param httpServletRequest HttpServletRequest
     * @return RequestBody
     * @throws IOException io exception
     */
    public RequestBody getRequestParameter(HttpServletRequest httpServletRequest) throws IOException {
        RequestBody requestBody = new RequestBody();
        if (HttpMethod.POST.matches(httpServletRequest.getMethod()) || HttpMethod.PUT.matches(httpServletRequest.getMethod())) {
            requestBody = JSONUtil.toBean(getBody(httpServletRequest), RequestBody.class);
            log.info("body:{}", requestBody);
            requestBody.setParams(requestBody.getParams());
            requestBody.setSign(requestBody.getSign());
        } else {
            requestBody.setParams(httpServletRequest.getParameter("params"));
            requestBody.setSign(httpServletRequest.getParameter("sign"));
        }
        return requestBody;
    }

    /**
     * response exception result json
     *
     * @param resultCodeEnum ResultCodeEnum
     */
    private void responseJson(ServletResponse response, ResultCodeEnum resultCodeEnum) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        Result<Void> result = Result.error(resultCodeEnum);
        String json = JSONUtil.toJsonStr(result);
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }

    /**
     * output json data
     *
     * @param responseWrapper httpServletResponse wrapper
     * @param encryptJson     json data
     * @throws IOException IO exception
     */
    private void outputStream(ResponseWrapper responseWrapper, String encryptJson) throws IOException {
        ServletOutputStream outputStream = responseWrapper.getOutputStream();
        outputStream.write(encryptJson.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }


    public String getBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * check url is exclude
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    private boolean handleExcludeURL(HttpServletRequest request) {
        if (!enabled) {
            return true;
        }
        if (CollectionUtils.isEmpty(excludes)) {
            return false;
        }
        return excludes.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }
}
