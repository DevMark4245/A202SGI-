package com.inti.student.travelmalaysia.model;

public class HistoryModel {

    private String mIdBook;
    private String mDate;
    private String mHistory;
    private String mTotal;
    private int mImageResourceId;
    private static final int NO_IMAGE_PROVIDED = -1;

    public HistoryModel(String idBook, String date, String history, String total, int imageResourceId) {
        mIdBook = idBook;
        mDate = date;
        mHistory = history;
        mTotal = total;
        mImageResourceId = imageResourceId;
    }

    public String getIdBook() {
        return mIdBook;
    }

    public String getDate() {
        return mDate;
    }

    public String getHistory() {
        return mHistory;
    }

    public String getTotal() {
        return mTotal;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public boolean hasImage() {
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }

}