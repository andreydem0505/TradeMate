package com.dementev_a.trademate.json;


import android.os.Parcel;
import android.os.Parcelable;

public class MerchandiserJson implements Parcelable {
    private String name;
    private String email;

    public MerchandiserJson(String name, String email) {
        this.name = name;
        this.email = email;
    }

    protected MerchandiserJson(Parcel in) {
        name = in.readString();
        email = in.readString();
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

    // getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "MerchandiserJson{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
    }
}
