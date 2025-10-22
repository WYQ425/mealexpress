package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获新增员工时已存在重复账号
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String exMessage = ex.getMessage();
        log.error("异常信息：{}", exMessage);
        if (exMessage.contains("Duplicate entry")){
            //依次信息示例：Duplicate entry 'wudi' for key 'employee.idx_username'
            //根据空格分割提取重复的账号信息并重新拼接返回
            String[] split = exMessage.split(" ");
            String repeatMessage = split[2];
            String msg=repeatMessage+ MessageConstant.ALREADY_EXISTS;

            return Result.error(msg);
        }else {
            //不知道的数据库异常
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
