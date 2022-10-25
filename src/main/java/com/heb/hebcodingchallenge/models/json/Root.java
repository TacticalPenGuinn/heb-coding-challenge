package com.heb.hebcodingchallenge.models.json;

public class Root {
    private Result result;
    private Status status;
    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}