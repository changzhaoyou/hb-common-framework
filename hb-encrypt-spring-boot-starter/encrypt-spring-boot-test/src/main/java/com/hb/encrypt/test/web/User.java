package com.hb.encrypt.test.web;

import java.io.Serializable;

/**
 * Created by ycz on  2021/08/22/5:35 下午
 */
public class User implements Serializable {

    private Integer a;

    private String b;

    private UserInfo c;

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public UserInfo getC() {
        return c;
    }

    public void setC(UserInfo c) {
        this.c = c;
    }
}
