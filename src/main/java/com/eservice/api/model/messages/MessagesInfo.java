package com.eservice.api.model.messages;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

public class MessagesInfo extends Messages {

    /**
     * 消息状态： 已读、未读
     */
    private String messageStatus;

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    /**
     * 消息接受者账号,账号有唯一性
     */
    private String messageUserAccount;

    public String getMessageUserAccount() {
        return messageUserAccount;
    }

    public void setMessageUserAccount(String messageUserAccount) {
        this.messageUserAccount = messageUserAccount;
    }

}