package com.eservice.api.service.park;


import com.eservice.api.service.park.model.FaceUserInfo;

import java.util.ArrayList;

/**
 * Created by nan on 2017/12/12.
 */
public class FeatureResponseData {
    private int code = -1;
    private String message = "";
    private ArrayList<FaceUserInfo> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public  ArrayList<FaceUserInfo> getData() {
        return this.data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData( ArrayList<FaceUserInfo> data) {
        this.data = data;
    }
}
