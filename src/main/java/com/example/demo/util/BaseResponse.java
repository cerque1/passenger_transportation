package com.example.demo.util;

public class BaseResponse {

    private final String status;
    private final Integer code;
    private String text;

    public BaseResponse(String status, Integer code, String text) {
        this.status = status;
        this.code = code;
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public Integer getCode() {
        return code;
    }

    public String GetText(){
        return text;
    }
}