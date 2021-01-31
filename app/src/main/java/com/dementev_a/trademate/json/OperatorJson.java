package com.dementev_a.trademate.json;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class OperatorJson extends EmployeeJson implements Parcelable {

    public OperatorJson(String name, String email) {
        super(name, email);
    }

    protected OperatorJson(Parcel in) {
        super();
        String name = in.readString();
        String email = in.readString();
        super.setName(name);
        super.setEmail(email);
    }

    public static final Creator<OperatorJson> CREATOR = new Creator<OperatorJson>() {
        @Override
        public OperatorJson createFromParcel(Parcel in) {
            return new OperatorJson(in);
        }

        @Override
        public OperatorJson[] newArray(int size) {
            return new OperatorJson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.getName());
        dest.writeString(super.getEmail());
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
