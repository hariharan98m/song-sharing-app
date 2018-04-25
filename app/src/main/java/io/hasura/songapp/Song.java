package io.hasura.songapp;

/**
 * Created by HARIHARAN on 20-04-2018.
 */

public class Song {

    String download_url;
    String date_time;
    int likes, comments;

    public Song(){

    }
    public Song(String download_url, String date_time, int likes, int comments) {
        this.download_url = download_url;
        this.date_time = date_time;
        this.likes = likes;
        this.comments = comments;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
