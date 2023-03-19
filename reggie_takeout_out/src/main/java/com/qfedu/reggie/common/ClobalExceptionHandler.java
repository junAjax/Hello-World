package com.qfedu.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;

/**
 * 全局异常处理
 */
//@ControllerAdvice(annotations = {RestController.class, Controller.class})拦截@RestController和@Controller注解
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class ClobalExceptionHandler {


    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//捕获SQLIntegrityConstraintViolationException这个异常类来处理
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //Duplicate entry 账号重复错误信息
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");

    }

    /**
     * 异常处理方法 自定义异常信息
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)//捕获SQLIntegrityConstraintViolationException这个异常类来处理
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());

    }


}
