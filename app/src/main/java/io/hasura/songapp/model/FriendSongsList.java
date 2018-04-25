package io.hasura.songapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FriendSongsList {

    String song_name;

    String song_link;

    int num_of_likes;

    String date_created;


    public FriendSongsList(String song_name, String song_link, int num_of_likes, String date_created) {
        this.song_name = song_name;
        this.song_link = song_link;
        this.num_of_likes = num_of_likes;
        this.date_created = date_created;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSong_link() {
        return song_link;
    }

    public void setSong_link(String song_link) {
        this.song_link = song_link;
    }

    public int getNum_of_likes() {
        return num_of_likes;
    }

    public void setNum_of_likes(int num_of_likes) {
        this.num_of_likes = num_of_likes;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
