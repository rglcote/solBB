package com.rcsoft.solbb.model;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by RDCoteRi on 2017-09-14.
 */

public class EbookData {

    ArrayList<ChapterEntry> chapters = new ArrayList<>();
    String author;
    String title;
    String filePath;
    Uri coverImage;

    public ArrayList<ChapterEntry> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<ChapterEntry> chapters) {
        this.chapters = chapters;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Uri getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Uri coverImage) {
        this.coverImage = coverImage;
    }
}
