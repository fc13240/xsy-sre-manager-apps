package com.rkhd.sre.app.msg;

public class ObjectRestResponse<T> extends BaseResponse {

    T data;

    public ObjectRestResponse data(T data) {
        this.setData(data);
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static ObjectRestResponse ok(Object data) {
        return new ObjectRestResponse<Object>().data(data);
    }

    public static ObjectRestResponse ok() {
        return new ObjectRestResponse<Object>();
    }

    public ObjectRestResponse() {
    }

    public ObjectRestResponse(int status, String message, T data) {
        super(status, message);
        this.data = data;
    }
}
