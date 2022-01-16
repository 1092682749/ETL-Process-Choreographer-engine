package com.dyz.etlcomposer.utils;

import com.alibaba.fastjson.JSON;

/**
 * 统一API响应结果封装
 */
public class Result {
    private Integer code;
    private String message;
    private Object data;
    private Object obj;

    public void setCode(Integer resultCode) {
        this.code = resultCode;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }
    public Object getObj() {
        return obj;
    }

    public Result setObj(Object obj) {
        this.obj = obj;
        return this;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
