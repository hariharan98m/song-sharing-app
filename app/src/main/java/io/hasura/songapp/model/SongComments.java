package io.hasura.songapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARIHARAN on 11-07-2017.
 */

public class SongComments {

    public String comment_text;

    public String user_name;

    public SongComments(String comment_text, String user_name) {
        this.comment_text = comment_text;
        this.user_name = user_name;
    }
}
