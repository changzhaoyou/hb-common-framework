package com.hb.encrypt.config;

import com.hb.encrypt.filter.RSAFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ycz on  2021/08/22/5:19 下午
 */
@Configuration
@ConditionalOnProperty(value="rsa.secret.enabled",havingValue = "true")
@EnableConfigurationProperties({RSAConfigurationProperties.class})
public class FilterConfiguration implements Ordered {
    
    @Value("${rsa.secret.enabled}")
    private String enabled;

    @Value("${rsa.secret.excludes}")
    private String excludes;

    @Value("${rsa.secret.publicKey}")
    private String publicKey;

    @Value("${rsa.secret.privateKey}")
    private String privateKey;

    @Value("${rsa.secret.timestampCheck}")
    private String timestampCheck;

    @Bean
    public FilterRegistrationBean<RSAFilter> rsaFilter() {
        FilterRegistrationBean<RSAFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RSAFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", excludes);
        initParameters.put("enabled", enabled);
        initParameters.put("publicKey", publicKey);
        initParameters.put("privateKey", privateKey);
        initParameters.put("timestampCheck", timestampCheck);
        registration.setInitParameters(initParameters);
        return registration;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
