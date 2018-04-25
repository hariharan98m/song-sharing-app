package io.hasura.songapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.hasura.songapp.model.SongComments;


/**
 * Created by HARIHARAN on 22-07-2017.
 */

public class TestPagerAdapter extends PagerAdapter {
        int song_id;
        Context context;
        LayoutInflater mLayoutInflator;

        public List<SongComments> list = new ArrayList<>();

        public TestPagerAdapter(Context context, String song_name, String user) {
        mLayoutInflator=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            FirebaseAuth auth = FirebaseAuth.getInstance();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef= database.getReference("data");

            myRef.child(user).child("songs").child(song_name).child("comments_obj").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot i:dataSnapshot.getChildren()){
                        Log.i("comment: ", i.getValue(String.class)+ i.getKey());
                        list.add(new SongComments(i.getKey(), i.getValue(String.class)));
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view= mLayoutInflator.inflate(R.layout.text_view,container,false);
        TextView textView= (TextView)view.findViewById(R.id.put_text);

        view.setTag(position);

        ((ViewPager) container).addView(view);
        String user_name= list.get(position).user_name;
        String comment= list.get(position).comment_text;

        textView.setText(Html.fromHtml("<font color='#4787ed'><i>"+user_name+": </i></font>"+comment+"<br><br>"));

        return ((Object) view);
    }

    @Override
     public int getCount() {
            return list.size();
        }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(View) object;
    }
}

