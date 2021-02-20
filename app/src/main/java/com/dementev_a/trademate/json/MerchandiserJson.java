package com.dementev_a.trademate.json;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class MerchandiserJson extends EmployeeJson implements Parcelable {
    private String password;

    public MerchandiserJson(String name, String email, String password) {
        super(name, email);
        this.password = password;
    }

    protected MerchandiserJson(Parcel in) {
        super();
        String name = in.readString();
        String email = in.readString();
        super.setName(name);
        super.setEmail(email);
        password = in.readString();
    }

    public static final Creator<MerchandiserJson> CREATOR = new Creator<MerchandiserJson>() {
        @Override
        public MerchandiserJson createFromParcel(Parcel in) {
            return new MerchandiserJson(in);
        }

        @Override
        public MerchandiserJson[] newArray(int size) {
            return new MerchandiserJson[size];
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
        dest.writeString(password);
    }

    public String getPassword() {
        return password;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
