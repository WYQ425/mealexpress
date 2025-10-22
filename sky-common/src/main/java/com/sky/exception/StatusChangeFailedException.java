package com.sky.exception;

/**
 * 套餐启用失败异常
 */
public class StatusChangeFailedException extends BaseException {

    public StatusChangeFailedException(){}

    public StatusChangeFailedException(String msg){
        super(msg);
    }
}
