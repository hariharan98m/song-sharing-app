package io.hasura.songapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import static io.hasura.songapp.R.id.holder;
import static java.sql.Types.NULL;

public class SongActivity extends AppCompatActivity implements View.OnTouchListener, MediaPlayer.OnPreparedListener, View.OnClickListener{

    static Data d;
    static boolean user;
    TextView composer_name;

    CoordinatorLayout song_coordinator;
    RecyclerView recyclerView;
    WifiManager.WifiLock wifiLock;
    Timer timer;
    int page = 1;
    AlertDialog AD;
    private boolean first_time= true;
    private ImageButton buttonPlayPause, back_button;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    android.os.Handler handler= new android.os.Handler();
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
    private ConstraintLayout no_songs_holder;

    public static void startActivity(Activity startingActivity, Data data, boolean isUser) {
        Intent intent = new Intent(startingActivity, SongActivity.class);
        startingActivity.startActivity(intent);
        d=data;
        if(d==null){
            d= new Data();
            d.username= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        user= isUser;
        //To clear the stack, so that the user cannot go back to the authentication activity on hardware back press
    }

    String song;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef= database.getReference("data");
    SongsListAdapter songsListAdapter= new SongsListAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        Log.i("SONG", "here");


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dot_progress_bar, null);
        dialogBuilder.setView(dialogView);

        AD= dialogBuilder.create();
        final TextView his_songs= (TextView) findViewById(R.id.his_songs);

        no_songs_holder= (ConstraintLayout) findViewById(R.id.no_songs_holder);
        song_coordinator= (CoordinatorLayout) findViewById(R.id.coordinatorLayout2);
        recyclerView = (RecyclerView) findViewById(R.id.songs_list);

        recyclerView.setAdapter(songsListAdapter);
        //Step2 -- Set the layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));


        final Bundle bundl = new Bundle();

        if(user){
            Log.i("OUTd=null", "true");
            myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    d= dataSnapshot.getValue(Data.class);
                    d.username= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    Log.i("d=null", "true");
                    bundl.putString("friendname", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    bundl.putParcelable("friend", d);
                    his_songs.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"'s songs");
                    FriendProfileFragment NS= new FriendProfileFragment();
                    NS.setArguments(bundl);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.profileLayout, NS).commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            bundl.putString("friendname", d.username);
            his_songs.setText(d.username+"'s songs");
            bundl.putParcelable("friend", d);
            FriendProfileFragment NS= new FriendProfileFragment();
            NS.setArguments(bundl);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.profileLayout, NS).commit();

        }


        final NothingToShow NS2= new NothingToShow();

        Bundle bundl2 = new Bundle();
        bundl2.putString("title","No Songs");
        if(user) {
            bundl2.putString("msg", "You haven't added any Songs yet\nGet started with the Hamburger Button in the Home Page");
            bundl2.putInt("mode", 2);
        }else{
            bundl2.putString("msg", "Your friend hasn't added any Songs yet\nEndorse your friends with Crashz Media Library");
            bundl2.putInt("mode", 3);
        }

        NS2.setArguments(bundl2);

        myRef.child(d.username).child("songs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i: dataSnapshot.getChildren()) {
                    Song d = i.getValue(Song.class);
                    Log.i("Song", i.getKey()+ " "+d.date_time+ " "+ d.download_url + "likes "+ d.likes);
                    songsListAdapter.songNames.add(i.getKey());
                    songsListAdapter.songs.add(d);
                    songsListAdapter.notifyDataSetChanged();

                    if(NS2 !=null){
                        getSupportFragmentManager().beginTransaction().remove(NS2).commit();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DataRead", "Failed to read value.", error.toException());
            }
        });

        if(songsListAdapter.songNames.size()==0) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.no_songs_holder, NS2).commit();
        }

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        SeekBar sb = (SeekBar)v;
        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
        mediaPlayer.seekTo(playPositionInMillisecconds);
        return false;
    }


    private void primarySeekBarProgressUpdater() {
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100)); // This math construction give a percentage of "was playing"/"song length"
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }
    }

    //
    @Override
    public void onPrepared(MediaPlayer mp) {
        AD.dismiss();
        Log.i("OnPrepared", "Prepare over");
        mediaPlayer.start();
        buttonPlayPause.setImageResource(R.drawable.pause_button);
        first_time=false;
        primarySeekBarProgressUpdater();
        mediaFileLengthInMilliseconds=mediaPlayer.getDuration();
    }

    @Override
    public void onClick(View v) {
        if(mediaPlayer.isPlaying() && !first_time){
            buttonPlayPause.setImageResource(R.drawable.play_button);
            mediaPlayer.pause();
        }
        else if(!mediaPlayer.isPlaying() && !first_time){
            buttonPlayPause.setImageResource(R.drawable.pause_button);
            mediaPlayer.start();
        }
    }

    final int WRITE_STORAGE= 8;

    public class SongsListAdapter extends RecyclerView.Adapter<SongsListViewHolder> {
        public List<Song> songs = new ArrayList<>();
        public List<String> songNames = new ArrayList<>();
        //private List<FragmentViewAdapter> comment_adapter= new ArrayList<>();

        public int mExpandedPosition = -1;

        @Override
        public SongsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_plus_comments, parent, false);
            return new SongsListViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final SongsListViewHolder holder, final int position) {
            if(songNames!=null && songs!=null) {
                final SharedPreferences user_id_song_id_like = getApplicationContext().getSharedPreferences(songNames.get(position), MODE_PRIVATE);
                holder.song_name.setText(songNames.get(position));

            /*holder.song_tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //play the song
                }
            });

            */
                final SharedPreferences song_download = getApplicationContext().getSharedPreferences(songNames.get(position)+".mp3", MODE_PRIVATE);

                if(song_download.getBoolean("download", false)) {
                    holder.composer_name.setText("DOWNLOADED");
                    holder.composer_name.setEnabled(false);
                    holder.composer_name.setTextColor(Color.WHITE);
                }

                    holder.composer_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        song= songNames.get(position);
                        holder.composer_name.setText("DOWNLOADING");
                        holder.composer_name.setEnabled(false);

                            if (ContextCompat.checkSelfPermission(SongActivity.this,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Permission is not granted
                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(SongActivity.this,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    Toast.makeText(SongActivity.this, "Add Permissions to Continue", Toast.LENGTH_SHORT).show();
                                    // Show an explanation to the user *asynchronously* -- don't block
                                    // this thread waiting for the user's response! After the user
                                    // sees the explanation, try again to request the permission.

                                } else {

                                    // No explanation needed; request the permission
                                    ActivityCompat.requestPermissions(SongActivity.this,
                                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            WRITE_STORAGE);

                                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                    // app-defined int constant. The callback method gets the
                                    // result of the request.
                                }
                            } else {
                                // Permission has already been granted
                                try {
                                    composer_name= holder.composer_name;
                                    downloadFile(songNames.get(position), holder.composer_name, ".mp3");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                    }
                });
                holder.send_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Check if the comments tab is empty
                        String comment_text= holder.user_comment_enter.getText().toString().trim();
                        if (comment_text.isEmpty()) {
                            Toast.makeText(getBaseContext(), "Can't post empty comments", Toast.LENGTH_SHORT).show();
                        }
                        //insert the comment and increment the comment count
                        else {

                            songs.get(position).setComments(songs.get(position).getComments() + 1);
                            Toast.makeText(SongActivity.this, "Wow, that's a Comment!!", Toast.LENGTH_LONG).show();
                            myRef.child(d.username).child("songs").child(songNames.get(position)).child("comments").setValue(songs.get(position).getComments());
                            myRef.child(d.username).child("songs").child(songNames.get(position)).child("comments_obj").child(comment_text).setValue(d.username);
                            notifyDataSetChanged();
                        }
                    }
                });

                if (!user_id_song_id_like.getBoolean("like", false))
                    holder.add_like.setImageResource(R.drawable.before_like);
                else
                    holder.add_like.setImageResource(R.drawable.after_like);

                holder.add_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //insert the like.
                        if (!user_id_song_id_like.getBoolean("like", false)) {
                            holder.add_like.setImageResource(R.drawable.after_like);
                            songs.get(position).setLikes(songs.get(position).getLikes() + 1);
                            holder.add_like.setImageResource(R.drawable.before_like);

                            myRef.child(d.username)
                                    .child("songs")
                                    .child(songNames.get(position))
                                    .child("likes")
                                    .setValue(songs.get(position).getLikes());
                            Log.i("like added", "changed" );
                            notifyDataSetChanged();
                            user_id_song_id_like.edit().putBoolean("like", true).apply();

                        } else {
                            holder.add_like.setImageResource(R.drawable.before_like);

                            if (songs.get(position).getLikes() != 0) {
                                songs.get(position).setLikes(songs.get(position).getLikes() - 1);
                                myRef.child(d.username).child("songs").child(songNames.get(position)).child("likes").setValue(songs.get(position).getLikes());
                            }
                            Log.i("like discarded", "changed" );


                            notifyDataSetChanged();
                            user_id_song_id_like.edit().putBoolean("like", false).apply();
                        }
                    }
                });

            if (songs.get(position).getDate_time() != null)
                    holder.date.setText(songs.get(position).getDate_time());


            if (songs.get(position).getComments() != NULL) {
                holder.num_of_comments.setText(Integer.toString(songs.get(position).getComments()));
            } else
                holder.num_of_comments.setText(Integer.toString(0));

                Log.i("#likes", Integer.toString(songs.get(position).getLikes()));
            if (songs.get(position).getLikes() != NULL)
                holder.num_of_likes.setText(Integer.toString(songs.get(position).getLikes()));
            else
                holder.num_of_likes.setText(Integer.toString(0));



            holder.comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(songs.get(position).getComments()>0) {
                        Dialog dialog = new Dialog(SongActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.comment_box_layout);
                        TestPagerAdapter adapter = new TestPagerAdapter(getBaseContext(), songNames.get(position), d.username);
                        AutoScrollViewPager pager = (AutoScrollViewPager) dialog.findViewById(R.id.comments_view_pager);
                        pager.startAutoScroll(3000);
                        pager.setInterval(3500);
                        pager.setScrollDurationFactor(6);
                        pager.setAdapter(adapter);
                        dialog.show();
                    }
                    else{
                        Toast.makeText(SongActivity.this,"No comments",Toast.LENGTH_SHORT);
                    }
                }
            });
                holder.play_song_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            play_song(songs.get(position).getDownload_url(), songNames.get(position));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        private void downloadFile(final String songname, final TextView composer_name, String ext) throws IOException {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            song_coordinator.bringToFront();
            final Snackbar snackbar = Snackbar.make(song_coordinator, Html.fromHtml("<font color=\"#fafafb\"size=\"2\"><i>Downloading in the background</i></font>"), Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(Color.BLACK);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(SongActivity.this, R.color.colorAccent));
            snackbar.show();


            StorageReference riversRef = storageRef.child("songs/"+songname+ext);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("songDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath=new File(directory,songname+".mp3");
            Log.i("path", ""+mypath);
            Log.i("downloadPath", cw.getDir("songDir", Context.MODE_PRIVATE)+ "/"+songname+".mp3");

            riversRef.getFile(mypath)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            snackbar.dismiss();
                            final Snackbar snackbar = Snackbar.make(song_coordinator, Html.fromHtml("<font color=\"#fafafb\"size=\"2\"><i>Now you can Listen Offline</i></font>"), Snackbar.LENGTH_LONG);
                            song_coordinator.bringToFront();
                            snackbar.setActionTextColor(Color.BLACK);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(SongActivity.this, R.color.blue_500));
                            snackbar.show();
                            final SharedPreferences song_download = getApplicationContext().getSharedPreferences(songname+".mp3", MODE_PRIVATE);
                            song_download.edit().putBoolean("download", true).commit();
                            composer_name.setText("DOWNLOADED");
                            composer_name.setEnabled(false);
                            composer_name.setTextColor(Color.WHITE);

                            Toast.makeText(SongActivity.this, "Downloaded!!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    Log.i("failed coz of", exception.getMessage());
                    try {
                        downloadFile(songname, composer_name, ".m4a");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            Log.i("size", ""+songs.size());
            return songs.size();
        }

        }




    public void play_song(String url, String song_name) throws IOException {
        View view= getLayoutInflater().inflate(R.layout.play_music,null);
        Log.i("play_song",url);
        final SharedPreferences song_download = getApplicationContext().getSharedPreferences(song_name+".mp3", MODE_PRIVATE);
        if(song_download.getBoolean("download", false)){
            Log.i("Downloaded Song", song_name);
        }


        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();

        final AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setView(view);
        dialog.setCancelable(false);
        final AlertDialog alert= dialog.create();
        alert.show();

        back_button= (ImageButton) view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                mediaPlayer.release();
                mediaPlayer=null;
            }
        });

        Log.i("i am here","after wifi lock");
        seekBar= (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(99);
        seekBar.setOnTouchListener(this);

        buttonPlayPause= (ImageButton) view.findViewById(R.id.play_pause);


        mediaPlayer= new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        if(song_download.getBoolean("download", false)){
            Log.i("Downloaded Song", song_name);

            song_coordinator.bringToFront();
            final Snackbar snackbar = Snackbar.make(song_coordinator, Html.fromHtml("<font color=\"#fafafb\"size=\"2\"><i>Playing Offline!</i></font>"), Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.BLACK);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(SongActivity.this, R.color.blue_500));
            snackbar.show();

            mediaPlayer.setDataSource(cw.getDir("songDir", MODE_PRIVATE) + File.separator + song_name+".mp3");
            mediaPlayer.prepare();

            mediaPlayer.start();
            buttonPlayPause.setImageResource(R.drawable.pause_button);
            first_time=false;
            primarySeekBarProgressUpdater();
            mediaFileLengthInMilliseconds=mediaPlayer.getDuration();

        }else {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.prepareAsync();
            AD.show();
        }

        buttonPlayPause.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
        if(wifiLock!=null) wifiLock.release();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        songsListAdapter.downloadFile(song, composer_name, ".mp3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(this, "Add Permissions to Access Files", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }








}


