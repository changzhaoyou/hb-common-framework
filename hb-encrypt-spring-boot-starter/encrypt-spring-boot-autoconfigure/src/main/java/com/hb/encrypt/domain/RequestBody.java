package com.hb.encrypt.domain;

import cn.hutool.json.JSONUtil;

import java.io.Serializable;

/**
 * Created by ycz on  2021/08/24/6:15 下午
 * @author zhangzhaoyou
 */
public class RequestBody implements Serializable {
    /**
     * body
     */
    private String params;
    /**
     * sign
     */
    private String sign;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
