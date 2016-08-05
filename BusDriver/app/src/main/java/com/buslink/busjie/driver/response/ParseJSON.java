package com.buslink.busjie.driver.response;

/**
 * Created by Administrator on 2015/10/16.
 */
public abstract class ParseJSON {

    protected String resultString;
    protected boolean status;
    protected String msg;

    public abstract void parse(String jsonString);

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
