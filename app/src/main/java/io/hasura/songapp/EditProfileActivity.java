package io.hasura.songapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class EditProfileActivity extends AppCompatActivity{
    public EditText work, city, music_style, passion, description;
    public TextView name;

    public static void startActivity(Activity startingActivity, boolean isFirstTime) {
        Intent intent = new Intent(startingActivity, EditProfileActivity.class);
        intent.putExtra("isFirstTime",isFirstTime);
        startingActivity.startActivity(intent);
        if(isFirstTime){
            startingActivity.finish();
        }
        //To clear the stack, so that the user cannot go back to the authentication activity on hardware back press
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");


        Log.i("In EditProfile","Before parsing the intent");
        name= (TextView) findViewById(R.id.prof_name);
        name.setText("Hi "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+", lets setup your profile");
        city= (EditText) findViewById(R.id.city_input);
        music_style= (EditText) findViewById(R.id.music_style_input);
        passion= (EditText) findViewById(R.id.passion_with_music);
        description= (EditText) findViewById(R.id.description);

        if (getIntent() != null) {
            boolean aBoolean = getIntent().getBooleanExtra("isFirstTime", false);
            if (!aBoolean) {
                Log.i("In EditProfile","firstTime is false");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setData();
            }
            else{
                Log.i("In EditProfile","firstTime is true");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    public void insertOrUpdate(View view){
        Log.i("Inside insertorUpdate","I am here-- Save button clicked");
                if (IsFormValid()) {
                    //Insert the data
                    Log.i("Inside insertorUpdate", "aBoolean is true-- first Time / Insert now");
                    SharedPreferences user_profile_edited_flag= getApplicationContext().getSharedPreferences("user_profile_edited",MODE_PRIVATE);
                    user_profile_edited_flag.edit().putBoolean("user_profile_edited_flag",true).commit();
                    Log.i("Inside insertorUpdate","user_edited_flag is set to true");
                    insertOrUpdateData("insert");
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    HomeActivity.startActivity(EditProfileActivity.this);
                }

    }


    private void setData() {
        Log.i("sharedPref-- setData","Inside Setdata");

        SharedPreferences user_profile = getBaseContext().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"prof", getBaseContext().MODE_PRIVATE);
            city.setText(user_profile.getString("city", ""));
            music_style.setText(user_profile.getString("music_style", ""));
            passion.setText(user_profile.getString("passion", ""));
            description.setText(user_profile.getString("desc", ""));
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("data");

    private void insertOrUpdateData(String s) {
        Log.i("sharedPref setData","Inside Setdata");
        saveToSharedPreferences();

        Log.i("In InsertOrUpdateData",s);
        //put it in an array of Args.Objects

        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("city").setValue(city.getText().toString().trim());
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("desc").setValue(description.getText().toString().trim());
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("passion").setValue(passion.getText().toString().trim());
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("music_style").setValue(music_style.getText().toString().trim());

    }

    private void saveToSharedPreferences() {

        //Put the user_id in that user's edit_profile
        SharedPreferences user_profile = getBaseContext().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"prof", getBaseContext().MODE_PRIVATE);
        SharedPreferences.Editor editor= user_profile.edit();

        editor.putString("city",city.getText().toString().trim());
        editor.putString("music_style",music_style.getText().toString().trim());
        editor.putString("passion",passion.getText().toString().trim());
        editor.putString("desc",description.getText().toString().trim());

        editor.commit();
    }

    private boolean IsFormValid() {
        Log.i("Inside isFormValid","true");
        if(city.getText().toString().trim().isEmpty()){
            Log.i("Inside isFormValid","name is empty");
            Toast.makeText(this,"City cannot be left empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(description.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Description cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(music_style.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Set a Music Genre of your choice", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(passion.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Your Passion needs to be set", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
