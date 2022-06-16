package com.baatechat.blackwhite.swip.call.Interface;

import androidx.annotation.Keep;

@Keep
public class Root { private boolean Status;

    private int HttpStatus;

    private Data Data;

    private String Message;

    public void setStatus(boolean Status){
        this.Status = Status;
    }
    public boolean getStatus(){
        return this.Status;
    }
    public void setHttpStatus(int HttpStatus){
        this.HttpStatus = HttpStatus;
    }
    public int getHttpStatus(){
        return this.HttpStatus;
    }
    public void setData(Data Data){
        this.Data = Data;
    }
    public Data getData(){
        return this.Data;
    }
    public void setMessage(String Message){
        this.Message = Message;
    }
    public String getMessage(){
        return this.Message;
    }

}
