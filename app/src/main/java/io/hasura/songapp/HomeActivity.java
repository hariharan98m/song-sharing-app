package io.hasura.songapp;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.hasura.songapp.model.BitmapResponse;
import io.hasura.songapp.model.DrawerListItems;
import io.hasura.songapp.viewholder.FriendsListViewHolder;

public class HomeActivity extends AppCompatActivity implements GuysYouMayKnowFragment.Helper{
    static List<ViewPagerFriends> fr= new ArrayList<>(50);
    static List<ViewPagerFriends> conf= new ArrayList<>(50);
    static List<Data> friend= new ArrayList<>(50);
    FriendsListAdapter friendsListAdapter= new FriendsListAdapter();
    boolean flagGlobal=false;
    public static void startActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, HomeActivity.class);
        startingActivity.startActivity(intent);
        //To clear the stack, so that the user cannot go back to the authentication activity on hardware back press
        startingActivity.finish();
    }

    boolean flagGlobal2=false;
    boolean shouldGoInvisible,setter;
    boolean isForeground= true;

    ImageButton left_arrow, right_arrow;
    ConstraintLayout cl;
    RecyclerView recyclerView;
    ViewPager viewPager;
    CardView card_view_list;
    DotProgressBar dotProgressBar;
    AlertDialog AD;
    ListView listView;
    float mPreviousOffset;
    TextView song_name, link;
    EditText composer_name;
    EditText song_link;
    Button add_it;
    //DrawerLayout
    private Animator mCurrentAnimator;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    CoordinatorLayout coordinatorLayout;
    GuysYouMayKnowAdapter pagerAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef= database.getReference("data");

    @Override
    protected void onResume() {
        super.onResume();
        isForeground= true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground= false;
    }

    NothingToShow NS = new NothingToShow();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Home Page");
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        cl=(ConstraintLayout) findViewById(R.id.con_holder);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        left_arrow= (ImageButton) findViewById(R.id.left_arrow);
        right_arrow= (ImageButton) findViewById(R.id.right_arrow);

        left_arrow.setVisibility(View.VISIBLE);
        right_arrow.setVisibility(View.VISIBLE);

        listView = (ListView)findViewById(R.id.list_view2);
        List<DrawerListItems> items=new ArrayList<>();

        String[] display={"Edit Profile","Your Songs","Add a song","Logout"};
        for (int i=0;i<4;i++){
            items.add(new DrawerListItems(display[i],i));
        }
        listView.setAdapter(new CustomAdapter(getBaseContext(), items));
        // Set the list's click listener
        listView.setOnItemClickListener(new DrawerItemClickListener());


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open,R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle("Home Page");
                shouldGoInvisible = false;
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle("What Next");
                shouldGoInvisible = true;
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //Get the ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewPagerFriends);
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        //Create the adapter linking the fragment
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "io.hasura.songapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        pagerAdapter = new GuysYouMayKnowAdapter(getSupportFragmentManager(), fr, conf);
        //Get the data for the adapter
        viewPager.setAdapter(pagerAdapter);

        addNothingToShowReqOrConfirm();

        left_arrow.setVisibility(View.GONE);
        right_arrow.setVisibility(View.GONE);

        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                pagerAdapter.requestList= new ArrayList<ViewPagerFriends>();
                pagerAdapter.notifyDataSetChanged();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    i++;
                    final String clubkey = childSnapshot.getKey();

                    boolean flag= childSnapshot.getValue(Boolean.class);
                    if(flag) {
                        myRef.child(clubkey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Data d = dataSnapshot.getValue(Data.class);
                                left_arrow.setVisibility(View.VISIBLE);
                                right_arrow.setVisibility(View.VISIBLE);

                                if(NS!=null && isForeground){
                                    getSupportFragmentManager().beginTransaction().remove(NS).commit();
                                    NS=null;
                                }

                                if(!containsReq(pagerAdapter.requestList, clubkey)) {
                                    if (!d.city.isEmpty())
                                        pagerAdapter.requestList.add(new ViewPagerFriends(clubkey, d.city, true));
                                    else {
                                        Log.i("EMAIL"+clubkey, d.email);
                                        pagerAdapter.requestList.add(new ViewPagerFriends(clubkey, d.email, true));
                                    }
                                    pagerAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("DataRead", "Failed to read value.", error.toException());
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DataRead", "Failed to read value.", error.toException());
            }
        });


        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("confirm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                Log.i("Change in Confirm", "fired");
                pagerAdapter.confirmList= new ArrayList<ViewPagerFriends>();
                pagerAdapter.notifyDataSetChanged();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    i++;
                    final String clubkey = childSnapshot.getKey();
                    boolean flag= childSnapshot.getValue(Boolean.class);
                    if(flag) {
                        myRef.child(clubkey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Data d = dataSnapshot.getValue(Data.class);

                                left_arrow.setVisibility(View.VISIBLE);
                                right_arrow.setVisibility(View.VISIBLE);

                                if(NS!=null && isForeground){
                                    getSupportFragmentManager().beginTransaction().remove(NS).commit();
                                    NS=null;
                                }

                                if(!containsReq(pagerAdapter.confirmList, clubkey)) {
                                    if (!d.city.isEmpty())
                                        pagerAdapter.confirmList.add(new ViewPagerFriends(clubkey, d.city, true));
                                    else {
                                        pagerAdapter.confirmList.add(new ViewPagerFriends(clubkey, d.email, true));
                                        Log.i("CONFIRM"+clubkey, d.email);
                                    }
                                    pagerAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("DataRead", "Failed to read value.", error.toException());
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DataRead", "Failed to read value.", error.toException());
            }
        });


        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.friendsList);

        getRecyclerViewFriendsData(false);

            //Step 1--Set the adapter
        recyclerView.setAdapter(friendsListAdapter);

            //Step2 -- Set the layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean containsReq(List<ViewPagerFriends> h, String name) {
        for (ViewPagerFriends item : h) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("Position is", Integer.toString(position));
            Toast.makeText(HomeActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        }
    }


    void addNothingToShowReqOrConfirm(){
        Bundle bundl = new Bundle();
        bundl.putString("title", "Nothing to Show");
        bundl.putString("msg", "You are connected with everybody on Crashz.\nEither invited or befriended!\n");
        bundl.putInt("mode", 0);

        NS= new NothingToShow();
        NS.setArguments(bundl);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.cardViewHolder, NS, "NS")
                .commit();

    }

    void addNothingToShowFriends(){
        Log.i("i am here", "friend.size=0");
        Bundle bundl= new Bundle();
        bundl.putString("title","No Friends?");
        bundl.putString("msg","Sorry, but you haven't got any friends\nGet started with invites");
        bundl.putInt("mode",1);
        NS1= new NothingToShow();
        NS1.setArguments(bundl);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.nothing_to_show_container,NS1, "NS1")
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    public class CustomAdapter extends ArrayAdapter<DrawerListItems> {
        Context context;

        public CustomAdapter(@NonNull Context context, @NonNull List<DrawerListItems> objects) {
            super(context,0, objects);
            context= context;
        }


        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            DrawerListItems item = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_layout_card, parent, false);
            }
            final TextView text_msg=(TextView) convertView.findViewById(R.id.msg);
            text_msg.setText(item.getMsg());

            CardView cardView= (CardView) convertView.findViewById(R.id.card_view_list);
            cardView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawer(listView);
                    Handler handler= new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (position){
                                case 0: EditProfileActivity.startActivity(HomeActivity.this,false);break;
                                case 1:
                                    SongActivity.startActivity(HomeActivity.this, null, true); break;
                                case 2:
                                    chooseFile();break;
                                case 3: callLogOut(); break;
                            }
                        }
                    }, 300);
                }
            });

            return convertView;
        }


    }


    NothingToShow NS1= new NothingToShow();

    public boolean getRecyclerViewFriendsData(final boolean isWannaTuneTo){
        Log.i("i am here", "recyclerviewGET");
        addNothingToShowFriends();
        final int[] i = {0};
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("friend").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsListAdapter.h= new ArrayList<Data>();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    i[0]++;
                    final String clubkey = childSnapshot.getKey();
                    Log.i("REQ", clubkey);
                    boolean flag= childSnapshot.getValue(Boolean.class);
                    if(flag) {
                        myRef.child(clubkey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Data d = dataSnapshot.getValue(Data.class);
                                Log.i("FRIEND"+ clubkey, d.email);
                                d.setUsername(clubkey);
                                if(!contains(friend, d))
                                    friendsListAdapter.h.add(d);
                                if(NS1!=null && isForeground){
                                    getSupportFragmentManager().beginTransaction().remove(NS1).commit();
                                    NS1=null;
                                }

                                friendsListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("DataRead", "Failed to read value.", error.toException());
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DataRead", "Failed to read value.", error.toException());
            }
        });

        return true;
    }


    boolean contains(List<Data> list, Data d) {
        for (Data item : list) {
            if (item.getUsername().equals(d.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void left_click(View view){
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1);

    }
    public void right_click(View view){
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);

    }

    int viewPagerSize=0;
    public Target target;

    MenuItem item;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_button, menu);
        item=menu.findItem(R.id.edit_profile);
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                item.setIcon(drawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) { }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        };

        String pic_url;
        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null) {
            pic_url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
            Log.i("url", pic_url);
            Picasso.with(HomeActivity.this).load(pic_url).transform(new CircleTransform()).into(target);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (id){
            case R.id.edit_profile:
                Log.i("EP", "edit profile");
                EditProfileActivity.startActivity(HomeActivity.this,false);
                break;
            case R.id.logout:
                callLogOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(listView);
        if(shouldGoInvisible&&setter) {
            menu.findItem(R.id.edit_profile).setVisible(!shouldGoInvisible);
            menu.findItem(R.id.logout).setVisible(!shouldGoInvisible);
        }
        return super.onPrepareOptionsMenu(menu);

    }


    private void callLogOut(){
        drawerLayout.closeDrawer(listView);
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("CallLogout", "LoggedOut");
                    }
                });


        WelcomeActivity.startActivity(HomeActivity.this);
    }

    @Override
    public void onButtonClick(String s, String fname) {
        if(pagerAdapter.requestList.size()+pagerAdapter.confirmList.size()==1){
            Log.i("removed", "RemoveAll");
            pagerAdapter.requestList.removeAll(pagerAdapter.requestList);
            pagerAdapter.confirmList.removeAll(pagerAdapter.confirmList);
            pagerAdapter.notifyDataSetChanged();
            addNothingToShowReqOrConfirm();
        }
        if(s=="Send an Invite"){
            Log.i("send an invite", "called");

            myRef.child(fname).child("confirm").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(true);
            myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("req").child(fname).setValue(false);
            myRef.child(fname).child("req").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(false);

            Snackbar snackbar=Snackbar.make(coordinatorLayout,Html.fromHtml("<font color=\"#fafafb\"size=\"2\"><i>Request Sent</i></font>"),Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.BLACK);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.blue_500));
            snackbar.show();

        }else{
            Log.i("Confirm", "called");
                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("confirm").child(fname).setValue(false);
                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("friend").child(fname).setValue(true);
                myRef.child(fname).child("friend").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(true);

            Snackbar snackbar=Snackbar.make(coordinatorLayout,Html.fromHtml("<font color=\"#fafafb\"size=\"2\"><i>Friend Added!</i></font>"),Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.BLACK);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorAccent));
                snackbar.show();
        }

    }

    private void chooseFile() {
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("audio/mpeg");
        Intent chooser = Intent.createChooser(intent, "Choose a Song");

// Verify the intent will resolve to at least one activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, REQUEST_CODE);
        }

    }


    class GuysYouMayKnowAdapter extends FragmentStatePagerAdapter{

        List<ViewPagerFriends> requestList= new ArrayList<>();

        List<ViewPagerFriends> confirmList= new ArrayList<>();

        public int size;


        public GuysYouMayKnowAdapter(FragmentManager fm, List<ViewPagerFriends> req, List<ViewPagerFriends> conf) {
            super(fm);
            this.requestList= req;
            this.confirmList= conf;
            this.size=requestList.size()+confirmList.size();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {

            if (position >= (confirmList.size())) {
                Bundle bundle = new Bundle();
                bundle.putString("name", requestList.get(position-confirmList.size()).getName());
                bundle.putString("desc", requestList.get(position-confirmList.size()).getDesc());
                bundle.putString("add_or_remove_button", "Send an Invite");

                GuysYouMayKnowFragment reqFragment = new GuysYouMayKnowFragment();
                reqFragment.setArguments(bundle);
                return reqFragment;
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString("name", confirmList.get(position).getName());
                bundle.putString("desc", confirmList.get(position).getDesc());
                bundle.putString("add_or_remove_button", "Confirm me?");

                GuysYouMayKnowFragment reqFragment = new GuysYouMayKnowFragment();
                reqFragment.setArguments(bundle);
                return reqFragment;
            }

        }

        @Override
        public int getCount() {
            return requestList.size()+confirmList.size();
        }

    }



    public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListViewHolder>{
        List<Data> h=new ArrayList<>();
        RecyclerView recyclerView;
        List<BitmapResponse> dp_links= new ArrayList<>();
        // Hold a reference to the current animator,
        // so that it can be canceled mid-way.
        // The system "short" animation time duration, in milliseconds. This
        // duration is ideal for subtle animations or animations that occur
        // very frequently.
        FragmentTransaction fm= getSupportFragmentManager().beginTransaction();

        @Override
        public FriendsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate the layout
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list, parent, false);
            return new FriendsListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FriendsListViewHolder holder, final int position) {
            //Set the data
            holder.friendName.setText(h.get(position).username);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongActivity.startActivity(HomeActivity.this, h.get(position), false);
                }
            });

        }

        @Override
        public int getItemCount() {
            return h.size();
        }

        public String setString(int pos){

            String s= "Hi! How are you?";
            Log.i("s is ",s);


            return s;
        }

        public void setData(List<Data> h, RecyclerView recyclerView) {
            this.h= h;
            Log.i("h.size= ", ""+h.size());
            this.recyclerView=recyclerView;
            notifyDataSetChanged();
        }


    }


    private void uploadFile(String path, String name) {
        Uri file = Uri.fromFile(new File(path));
        Log.i("filaName", fileName);
        Log.i("filaNameLastIndex", fileName.substring(fileName.lastIndexOf(".")));

        if(!fileName.endsWith(".mp3")) {
            Toast.makeText(this, "Upload only .mp3 files", Toast.LENGTH_SHORT).show();
            return;
        }
        fileName = fileName.substring(0,fileName.lastIndexOf("."));
        Log.i("SONGNAME", fileName);
        fileName = fileName.replaceAll("[^a-zA-Z0-9]", " ");

        final Snackbar snackbar= Snackbar.make(coordinatorLayout,Html.fromHtml("<font color=\"#fafafb\"size=\"2\"><i>Uploading in the background</i></font>"),Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.BLACK);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorAccent));

        StorageReference riversRef = mStorageRef.child("songs/"+fileName+".mp3");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.show();

            }
        }, 400);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        DateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
                        Date date = new Date();
                         //2016/11/16 12:08:43
                        Log.i("songname", fileName);
                        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("songs").child(fileName).setValue(new Song(downloadUrl.toString(), dateFormat.format(date),0, 0));

                        Toast.makeText(HomeActivity.this, "Song Uploaded to Cloud", Toast.LENGTH_LONG).show();
                        snackbar.dismiss();
                        Log.i("downloadUrl", ""+downloadUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.i("downFailed", "Failed");
                        // ...
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 4: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    uploadFile(filePath, fileName);

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

    private StorageReference mStorageRef;
    int REQUEST_CODE=1;
    int READ_STORAGE=4;


    private String filePath;
    private String fileName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                filePath= data.getData().getPath();
                fileName=filePath.substring(filePath.lastIndexOf("/")+1);

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "Add Permissions to Continue", Toast.LENGTH_SHORT).show();
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    uploadFile(filePath, fileName);
                }


                // Do something with the contact here (bigger example below)
            }
        }
    }


}

