package io.hasura.songapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HARIHARAN on 14-04-2018.
 */

public class Data implements Parcelable {

    public String username;
    public String email;
    public String desc;
    public String city;
    public String music_style;
    public String passion;
    public String hobbies;
    public String dp_url;

    public Data(){

    }

    public Data( String email, String desc, String city, String music_style, String passion, String hobbies, String dp_url) {

        this.email = email;
        this.desc = desc;
        this.city = city;
        this.music_style = music_style;
        this.passion = passion;
        this.hobbies = hobbies;
        this.dp_url = dp_url;
    }

    protected Data(Parcel in) {
        username = in.readString();
        email = in.readString();
        desc = in.readString();
        city = in.readString();
        music_style = in.readString();
        passion = in.readString();
        hobbies = in.readString();
        dp_url = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMusic_style() {
        return music_style;
    }

    public void setMusic_style(String music_style) {
        this.music_style = music_style;
    }

    public String getPassion() {
        return passion;
    }

    public void setPassion(String passion) {
        this.passion = passion;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getDp_url() {
        return dp_url;
    }

    public void setDp_url(String dp_url) {
        this.dp_url = dp_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(desc);
        parcel.writeString(city);
        parcel.writeString(music_style);
        parcel.writeString(passion);
        parcel.writeString(hobbies);
        parcel.writeString(dp_url);

    }
}
