package com.hb.encrypt.enums;

/**
 * result enum
 * @author hz18123767
 */
public enum ResultCodeEnum {
    /**
     * illegal argument
     */
    ILLEGAL_ARGUMENT_EXCEPTION("1000","illegal argument exception"),
    /**
     * rsa check exception
     */
    RSA_CHECK_EXCEPTION("1001","rsa check exception"),
    /**
     * sign exception
     */
    RSA_SIGN_EXCEPTION("1002","rsa sign exception");

    /**
     * 编码
     */
    private final String code;
    /**
     * 描述
     */
    private final String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
