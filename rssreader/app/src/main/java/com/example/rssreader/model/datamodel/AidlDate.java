package com.example.rssreader.model.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @ClassName： AidlDate
 * @Author SH
 * @Date： 2021/5/7
 * @Description：
 */
public class AidlDate implements Parcelable {

    private Date date;

    public AidlDate(Date date){
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(date);
    }

    public static final Creator<AidlDate> CREATOR = new Creator<AidlDate>() {
        @Override
        public AidlDate createFromParcel(Parcel source) {
            return new AidlDate((Date) source.readValue(Date.class.getClassLoader()));
        }

        @Override
        public AidlDate[] newArray(int size) {
            return new AidlDate[size];
        }
    };
}
