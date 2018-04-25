package io.hasura.songapp.network;

import java.util.List;

import io.hasura.songapp.model.AuthenticationRequest;
import io.hasura.songapp.model.BitmapResponse;
import io.hasura.songapp.model.CommentTextListRequest;
import io.hasura.songapp.model.DPSelect;
import io.hasura.songapp.model.DataHandlingResponse;
import io.hasura.songapp.model.DpInsert;
import io.hasura.songapp.model.ErrorResponse;
import io.hasura.songapp.model.FriendSongsList;
import io.hasura.songapp.model.HomePageFriendsReqORConfirm;
import io.hasura.songapp.model.InsertIntoRequestORConfirm;
import io.hasura.songapp.model.InsertLikeModelRequest;
import io.hasura.songapp.model.InsertORUpdateIntoUserTable;
import io.hasura.songapp.model.MessageResponse;
import io.hasura.songapp.model.SelectFriendsData;
import io.hasura.songapp.model.SelectFriendsSongsRequest;
import io.hasura.songapp.model.SongComments;
import io.hasura.songapp.model.SongInsert;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by HARIHARAN on 27-06-2017.
 */

public interface ApiInterface {

    @POST(NetworkURL.LOGIN_URL)
    Call<MessageResponse> login(@Body AuthenticationRequest body);

    @POST(NetworkURL.REGISTER)
    Call<MessageResponse> register(@Body AuthenticationRequest body);

    @POST(NetworkURL.MOB_CONFIRM)
    Call<MessageResponse> mconfirm(@Body AuthenticationRequest body);

    @GET(NetworkURL.USER_ACCOUNT_INFO)
    Call<MessageResponse> user_account_info();

    @POST(NetworkURL.UPDATE_PROFILE)
    Call<DataHandlingResponse> insert_or_update(@Body InsertORUpdateIntoUserTable body);

    @POST(NetworkURL.insert_confirm_req)
    Call<DataHandlingResponse> insert_into_request_or_confirm(@Body InsertIntoRequestORConfirm body);

    @POST(NetworkURL.update_confirm_req)
    Call<DataHandlingResponse> update_request_or_confirm(@Body InsertIntoRequestORConfirm body);

    @POST(NetworkURL.QUERY)
    Call<List<HomePageFriendsReqORConfirm>> select(@Body SelectFriendsData body);

    @POST(NetworkURL.select_confirms)
    Call<List<HomePageFriendsReqORConfirm>> select_confirms(@Body SelectFriendsData body);

    @POST(NetworkURL.select_invites)
    Call<List<HomePageFriendsReqORConfirm>> select_invites(@Body SelectFriendsData body);

    @POST(NetworkURL.select_friends)
    Call<List<HomePageFriendsReqORConfirm>> select_friends(@Body SelectFriendsData body);

    @POST(NetworkURL.get_songs)
    Call<List<FriendSongsList>> get_songs_for_this_friend(@Body SelectFriendsSongsRequest body);

    @POST(NetworkURL.get_comments)
    Call<List<SongComments>> get_comments_for_this_song(@Body CommentTextListRequest body);

    @POST(NetworkURL.comment_insert)
    Call<DataHandlingResponse> insert_comments_for_this_song(@Body CommentTextListRequest body);

    @POST(NetworkURL.song_insert)
    Call<DataHandlingResponse> insert_song(@Body SongInsert body);

    @GET(NetworkURL.LOGOUT)
    Call<ErrorResponse> logout();

    @POST(NetworkURL.QUERY)
    Call<DataHandlingResponse> insert_like(@Body InsertLikeModelRequest body);

    @POST(NetworkURL.QUERY)
    Call<DataHandlingResponse> delete_like(@Body InsertLikeModelRequest body);



    @POST(NetworkURL.dp_insert)
    Call<DataHandlingResponse> insert_dp(@Body DpInsert body);

    @POST(NetworkURL.prof_pic)
    Call<List<BitmapResponse>> prof_pic(@Body DPSelect body);


    //@POST(NetworkURL.ARTICLES)
    //Call<List<ArticlesResponse>> fetch_articles(@Body ArticleListRequest body);
}
