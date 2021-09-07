package com.hb.encrypt.result;

import com.hb.encrypt.enums.ResultCodeEnum;

import java.util.HashMap;
import java.util.Objects;

/**
 * json result
 *
 * @author hz18123767
 */
public class Result<T> extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static final String CODE_TAG = "code";


    public static final String MSG_TAG = "msg";

    public static final String DATA_TAG = "data";

    public Result(String code, String msg, T data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (!Objects.isNull(data)) {
            super.put(DATA_TAG, data);
        }
    }

    public static Result<Void> error(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static Result<Void> error(ResultCodeEnum resultCodeEnum) {
        return Result.error(resultCodeEnum.getCode(), resultCodeEnum.getMsg());
    }
}
