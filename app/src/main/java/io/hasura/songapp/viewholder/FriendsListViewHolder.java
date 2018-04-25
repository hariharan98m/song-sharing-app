package io.hasura.songapp.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.hasura.songapp.R;

/**
 * Created by HARIHARAN on 28-06-2017.
 */

public class FriendsListViewHolder extends RecyclerView.ViewHolder{
    CardView cardView;
    public TextView friendName, friendInfo;
    public ImageButton song_page;
    public ImageView prof_image;


    public FriendsListViewHolder(View itemView) {
        super(itemView);
        friendName= (TextView) itemView.findViewById(R.id.friend_name2);
        friendInfo=(TextView) itemView.findViewById(R.id.profileLayout);
        cardView= (CardView) itemView.findViewById(R.id.friendCard);
        song_page= (ImageButton) itemView.findViewById(R.id.song_page);
    }

}
