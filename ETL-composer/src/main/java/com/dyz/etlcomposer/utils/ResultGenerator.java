package com.dyz.etlcomposer.utils;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    public static Result successResult() {
        Result result = new Result();
        result.setCode(Constants.SUCCESS);
        result.setMessage(Constants.messageMap.get(Constants.SUCCESS));
        return result;
    }

    public static Result successResult(Object data) {
        Result result = new Result();
        result.setCode(Constants.SUCCESS);
        result.setMessage(Constants.messageMap.get(Constants.SUCCESS));
        result.setData(data);
        return result;
    }
    public static Result successResult(Object data,Object obj) {
        Result result = new Result();
        result.setCode(Constants.SUCCESS);
        result.setMessage(Constants.messageMap.get(Constants.SUCCESS));
        result.setData(data);
        result.setObj(obj);
        return result;
    }
    public static Result errResult() {
        Result result = new Result();
        result.setCode(Constants.FAIL);
        result.setMessage(Constants.messageMap.get(Constants.FAIL));
        return result;
    }
    public static Result errResult(Integer code) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(Constants.messageMap.get(code));
        return result;
    }
    public static Result errResult(Integer code,String message) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
