package com.example.android.news;

import android.graphics.Bitmap;

/**
 * Created by bplewis5 on 7/3/16.
 */
public class Article {

    private String mTitle;
    private String mSectionName;
    private Bitmap mBitmap;
    private String mWebUrlStr;

    public Article(String title, String sectionName, Bitmap bitmap, String webUrlStr) {
        mTitle = title;
        mSectionName = sectionName;
        mBitmap = bitmap;
        mWebUrlStr = webUrlStr;

    }

    public String getWebURLStr() {
        return mWebUrlStr;
    }

    public void setWebURLStr(String webURLStr) {
        mWebUrlStr = webURLStr;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public void setSectionName(String sectionName) {
        mSectionName = sectionName;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
