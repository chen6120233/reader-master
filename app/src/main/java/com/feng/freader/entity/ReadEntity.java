package com.feng.freader.entity;

import java.io.Serializable;

public class ReadEntity  implements Serializable{
    String url;
    String name;
    String next;//下一页

    /**转文字条件*/
    String condition;
    /**标签头*/
    String titleName;
    /**下一页*/
    String next_url;//下一页
    /**上一页*/
    String prev_url;//上一页
    /**文本*/
    String content;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }

    public String getPrev_url() {
        return prev_url;
    }

    public void setPrev_url(String prev_url) {
        this.prev_url = prev_url;
    }

    public ReadEntity(String url, String name, String condition, String titleName, String next_url, String prev_url, String content) {
        this.url = url;
        this.name = name;

        this.condition = condition;
        this.titleName = titleName;
        this.next_url = next_url;
        this.prev_url = prev_url;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public ReadEntity() {

    }

    public ReadEntity(String url, String name) {
        this.url = url;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ReadEntity{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", next='" + next + '\'' +
                ", condition='" + condition + '\'' +
                ", titleName='" + titleName + '\'' +
                ", next_url='" + next_url + '\'' +
                ", prev_url='" + prev_url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}