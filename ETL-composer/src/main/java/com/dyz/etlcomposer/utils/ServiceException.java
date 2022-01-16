package com.dyz.etlcomposer.utils;

public class ServiceException extends RuntimeException {
    public Integer code;
    public Integer getCode(){
        return this.code;
    }
    public void setCode(Integer code){
        this.code = code;
    }
    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(Integer code) {
        super(Constants.messageMap.get(code));
        this.code = code;
    }
    public ServiceException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
