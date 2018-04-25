package io.hasura.songapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static private String TAG= "Main";

    private static final int RC_SIGN_IN = 123;

    public static void startActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MainActivity.class);
        startingActivity.startActivity(intent);
        //To clear the stack, so that the user cannot go back to the authentication activity on hardware back press
        startingActivity.finish();
    }

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());

    // Create and launch sign-in intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("data");

                if (!Getbool()) {

                    Log.i("user", FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    Data d;
                    if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null)
                        d = new Data(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "", "", "", "", "", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());

                    else
                        d = new Data(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "", "", "", "", "", "");

                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(d);


                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String clubkey = childSnapshot.getKey();
                                if (!clubkey.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                                    myRef.child(clubkey).child("req").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(true);
                                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("req").child(clubkey).setValue(true);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("DataRead", "Failed to read value.", error.toException());
                        }
                    });

                }
                getSharedPreferences("first",MODE_PRIVATE).edit().putBoolean("first", true).commit();


                Runnable runnable= new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences user_profile_edited_flag= getApplicationContext().getSharedPreferences("user_profile_edited",MODE_PRIVATE);
                        if(!user_profile_edited_flag.getBoolean("user_profile_edited_flag",false))
                            EditProfileActivity.startActivity(MainActivity.this, true);
                        else
                            HomeActivity.startActivity(MainActivity.this);
                    }
                };
                Handler handler= new Handler();
                handler.postDelayed(runnable, 100);

                // ...
            }
        }
    }


    void signout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public boolean Getbool() {

        return getApplicationContext().getSharedPreferences("first",MODE_PRIVATE).getBoolean("first",false);
    }

}
