package com.betimes.crawler.exception;

public class MyHttpException extends Exception {
    private int code;

    public MyHttpException(){
        super();
    }

    public MyHttpException(String message){
        super(message);
    }

    public MyHttpException(String message, Throwable cause){
        super(message, cause);
    }

    public MyHttpException(int code, String message){
        super(message);
        this.code = code;
    }

    public MyHttpException(int code, String message, Throwable cause){
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
