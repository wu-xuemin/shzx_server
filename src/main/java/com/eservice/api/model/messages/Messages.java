package com.eservice.api.model.messages;

import java.util.Date;
import javax.persistence.*;

public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 发布时间
     */
    @Column(name = "send_time")
    private Date sendTime;

    /**
     * 标题
     */
    private String title;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 阅读次数
     */
    @Column(name = "read_count")
    private Integer readCount;

    /**
     * 消息的内容
     */
    private String content;

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
     * 获取发布时间
     *
     * @return send_time - 发布时间
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * 设置发布时间
     *
     * @param sendTime 发布时间
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * 获取标题
     *
     * @return title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取发布人
     *
     * @return publisher - 发布人
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * 设置发布人
     *
     * @param publisher 发布人
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * 获取阅读次数
     *
     * @return read_count - 阅读次数
     */
    public Integer getReadCount() {
        return readCount;
    }

    /**
     * 设置阅读次数
     *
     * @param readCount 阅读次数
     */
    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    /**
     * 获取消息的内容
     *
     * @return content - 消息的内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息的内容
     *
     * @param content 消息的内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}