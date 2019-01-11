package com.eservice.api.model.user_msg_status_info;

import javax.persistence.*;

@Table(name = "user_msg_status_info")
public class UserMsgStatusInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 消息ID，
     */
    @Column(name = "message_id")
    private Integer messageId;

    /**
     * 消息状态，比如”未读“、“已读”、“删除"
     */
    private String status;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取消息ID，
     *
     * @return message_id - 消息ID，
     */
    public Integer getMessageId() {
        return messageId;
    }

    /**
     * 设置消息ID，
     *
     * @param messageId 消息ID，
     */
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取消息状态，比如”未读“、“已读”、“删除"
     *
     * @return status - 消息状态，比如”未读“、“已读”、“删除"
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置消息状态，比如”未读“、“已读”、“删除"
     *
     * @param status 消息状态，比如”未读“、“已读”、“删除"
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     */
    private Integer user;

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}