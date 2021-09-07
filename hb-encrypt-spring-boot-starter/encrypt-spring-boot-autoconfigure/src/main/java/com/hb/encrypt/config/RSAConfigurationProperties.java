package com.hb.encrypt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 配置类
 *
 * @author hz18123767
 */
@ConfigurationProperties(prefix = "rsa.secret")
public class RSAConfigurationProperties {

    private String privateKey;

    private String publicKey;

    private String charset = "UTF-8";

    private List<String> excludes;

    private boolean enable = false;

    private boolean timestampCheck = false;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isTimestampCheck() {
        return timestampCheck;
    }

    public void setTimestampCheck(boolean timestampCheck) {
        this.timestampCheck = timestampCheck;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}
