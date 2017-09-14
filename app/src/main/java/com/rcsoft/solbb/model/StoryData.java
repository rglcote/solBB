package com.rcsoft.solbb.model;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 14/09/12
 * Time: 14:30
 */
public class StoryData {

    private int id;
    private String urlStr;
    private int chapterCount;
    private Date lastLoadDate;

    public StoryData(int id, String urlStr, int chapterCount, Date lastLoadDate) {
        this.id = id;
        this.urlStr = urlStr;
        this.chapterCount = chapterCount;
        this.lastLoadDate = lastLoadDate;
    }

    public StoryData(String urlStr, int chapterCount) {
        this.urlStr = urlStr;
        this.chapterCount = chapterCount;
        this.lastLoadDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public Date getLastLoadDate() {
        return lastLoadDate;
    }

    public void setLastLoadDate(Date lastLoadDate) {
        this.lastLoadDate = lastLoadDate;
    }
}
