package com.dementev_a.trademate.json;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestJson implements Parcelable {
    private String subject;
    private String text;
    private String operator;

    public RequestJson(String subject, String text, String operator) {
        this.subject = subject;
        this.text = text;
        this.operator = operator;
    }

    protected RequestJson(Parcel in) {
        subject = in.readString();
        text = in.readString();
        operator = in.readString();
    }

    public static final Creator<RequestJson> CREATOR = new Creator<RequestJson>() {
        @Override
        public RequestJson createFromParcel(Parcel source) {
            return new RequestJson(source);
        }

        @Override
        public RequestJson[] newArray(int size) {
            return new RequestJson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subject);
        dest.writeString(text);
        dest.writeString(operator);
    }
}
