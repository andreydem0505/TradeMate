package com.dementev_a.trademate.json;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestJson implements Parcelable {
    private String subject;
    private String text;
    private String operator;
    private String sender;
    private String dateTime;

    public RequestJson(String subject, String text, String operator, String sender, String dateTime) {
        this.subject = subject;
        this.text = text;
        this.operator = operator;
        this.sender = sender;
        this.dateTime = dateTime;
    }

    protected RequestJson(Parcel in) {
        subject = in.readString();
        text = in.readString();
        operator = in.readString();
        sender = in.readString();
        dateTime = in.readString();
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
        dest.writeString(sender);
        dest.writeString(dateTime);
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getOperator() {
        return operator;
    }

    public String getSender() {
        return sender;
    }

    public String getDateTime() {
        return dateTime;
    }
}
