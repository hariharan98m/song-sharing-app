package io.hasura.songapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARIHARAN on 27-06-2017.
 */

public class MessageResponse {
    @SerializedName("auth_token")
    String auth_token;

    @SerializedName("hasura_roles")
    String[] hasura_roles;

    @SerializedName("message")
    String message;

    @SerializedName("id")
    int id;

    @SerializedName("hasura_id")
    int hasura_id;

    public int getHasura_id() {
        return hasura_id;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public String[] getHasura_roles() {
        return hasura_roles;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }
}
