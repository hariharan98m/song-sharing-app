package io.hasura.songapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARIHARAN on 27-06-2017.
 */

public class ErrorResponse {
    @SerializedName("message")
    String message;

    public ErrorResponse(String error){
        this.message=error;
    }
    public String getMessage(){
        return message;
    }

}
