package com.adchina.dp.rpc.common.model;

public class Respose {
    private String requestId;
    private Object result;
    private Throwable excption;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getExcption() {
        return excption;
    }

    public void setExcption(Throwable excption) {
        this.excption = excption;
    }

}
