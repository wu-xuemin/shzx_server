package com.eservice.api.service.park.model;

/**
 * @program:arcsoft-face
 * @description:用于接收对象值
 * @author: Mr.Henry
 * @create:2019/8/13 11:19
 */
public class FaceDTO {
    private String base64;
    private String name;
    private String cardNum;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
}
