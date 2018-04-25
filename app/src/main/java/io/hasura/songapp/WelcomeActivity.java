package io.hasura.songapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    PagerAdapter pagerAdapter;

    public static void startActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, WelcomeActivity.class);
        startingActivity.startActivity(intent);
        //To clear the stack, so that the user cannot go back to the authentication activity on hardware back press
        startingActivity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final SharedPreferences database1= getApplicationContext().getSharedPreferences("database", MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(database1.getBoolean("database", false))
            database.setPersistenceEnabled(true);
        else
            database1.edit().putBoolean("database", true);
        setContentView(R.layout.activity_welcome);

        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("data");
        scoresRef.keepSynced(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            SharedPreferences user_profile_edited_flag= getApplicationContext().getSharedPreferences("user_profile_edited",MODE_PRIVATE);
            if(!user_profile_edited_flag.getBoolean("user_profile_edited_flag",false))
                EditProfileActivity.startActivity(WelcomeActivity.this, true);
            else
                HomeActivity.startActivity(WelcomeActivity.this);
        }



        getSupportActionBar().hide();

        viewPager= (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageTransformer(true,new DepthPageTransformer());
        pagerAdapter= new FragmentViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0)
        super.onBackPressed();
        else
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }

    class FragmentViewAdapter extends FragmentStatePagerAdapter {
        //List<WelcomeImageFooterFragment> welcomeImageFooterFragment= new ArrayList<>();
        String[] text={"<font color=\"#07c\" size=\"20\"><b>Welcome to Crashz</b></font><br><br> A Place to stash your Songs in the Cloud<br><br> Tune them Anytime",
            "<font color=\"#07c\"><b>A Forum to Socialize | New Way of Making Friends</b></font> <br><br>Find out what your friend listens to <br><br> Comment on their Song Collections<br>&<br> Read Out yours",
        "Over"
        };

        public FragmentViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==2)
                return new GetFragmentClass();
            Bundle bundl =new Bundle();
            bundl.putString("welcomeText",text[position]);
            if(position==0)
            bundl.putInt("welcomeImage",R.drawable.open);
            if(position==1)
                bundl.putInt("welcomeImage",R.drawable.socialise);
            WelcomeImageFooterFragment welcomeImageFooterFragment =new WelcomeImageFooterFragment();
            welcomeImageFooterFragment.setArguments(bundl);
            return welcomeImageFooterFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void startLogin(View view){
        MainActivity.startActivity(WelcomeActivity.this);
    }


}