package com.gsq.springboot.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException{
    private String code;
    public BusinessException(String code, String msg){
        super(msg);
        this.code=code;
    }
}
