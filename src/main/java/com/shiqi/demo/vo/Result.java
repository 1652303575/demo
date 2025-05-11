package com.shiqi.demo.vo;

public class Result<T> {
    private boolean success;
    private T data;
    private String message;

    public Result() {
    }

    public Result(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, "操作成功");
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}