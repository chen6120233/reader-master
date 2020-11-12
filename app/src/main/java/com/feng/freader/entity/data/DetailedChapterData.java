package com.feng.freader.entity.data;

/**
 * @author Feng Zhaohao
 * Created on 2019/11/25
 */
public class DetailedChapterData {
    private String name;    // 章节名
    private String content; // 章节内容
    String titleName ;
    String next_url;//下一页
    String url;//当前页
    String prev_url;//上一页

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrev_url() {
        return prev_url;
    }

    public void setPrev_url(String prev_url) {
        this.prev_url = prev_url;
    }

    public DetailedChapterData() {
    }

    public DetailedChapterData(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DetailedChapterData{" +
                "name='" + name + '\'' +

                ", next_url='" + next_url + '\'' +
                ", url='" + url + '\'' +
                ", prev_url='" + prev_url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
