package io.hasura.songapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static io.hasura.songapp.R.id.f_city;

/**
 * Created by HARIHARAN on 20-04-2018.
 */

public class FriendProfileFragment extends Fragment {

    TextView fname, profile;
    ImageView dp;
    Context c;
    TextView city;

    public FriendProfileFragment(Context c) {
        this.c = c;
    }

    public FriendProfileFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.card_friend_details,container,false);
        fname= (TextView) view.findViewById(R.id.friend_name2);
        profile= (TextView) view.findViewById(R.id.profileLayout);
        city= (TextView) view.findViewById(R.id.f_city);
        dp=(ImageView) view.findViewById(R.id.dp);

        String name= getArguments().getString("friendname");
        Data h= (Data)getArguments().get("friend");

        fname.setText(name);

        String profileStr= setString(h);

        if(profileStr.isEmpty())
            profile.setVisibility(View.INVISIBLE);
        else profile.setText(Html.fromHtml(profileStr));

        if(h.getDp_url()!=null && !h.getDp_url().isEmpty())
            Picasso.with(c).load(h.getDp_url()).transform(new CircleTransform()).into(dp);

        if(h.getCity()!=null && (h.getCity().length()!=0) ) {
            city.setVisibility(View.VISIBLE);
            city.setText(Html.fromHtml("<i>" + h.getCity() + "</i>"));
        }



        return view;
    }

    public String setString(Data h){

        String s="";

        if(h.getDesc()!=null && (h.getDesc().length()!=0) )
            s+="<h6>About me</h6><p>"+h.getDesc()+"</p>";
        if(h.getPassion()!=null && (h.getPassion().length()!=0)) {
            String music=" ";
            if(h.getMusic_style()!=null && !h.getMusic_style().isEmpty()) music="<b><i> "+h.getMusic_style()+" </i></b>";
            Log.i("music=",music);
            s += "<h6>Here's my" + music + "Music Story</h6>" + h.getPassion()+" <br>";
        }
        Log.i("s is ",s);

        return s;
    }

}
