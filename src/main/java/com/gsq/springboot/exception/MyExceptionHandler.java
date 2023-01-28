package com.gsq.springboot.exception;


import com.gsq.springboot.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(value = {BusinessException.class})
    @ResponseBody
    public Result handle(BusinessException se) {
        return Result.error(se.getCode(),se.getMessage());
    }
}
