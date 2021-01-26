package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.whatsappclone.Adapters.FragmentAdapter;
import com.example.whatsappclone.databinding.ActivityMainBinding;
import com.example.whatsappclone.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding; //new way to bind xml elements into java
    private FirebaseAuth mAuth; //firebase variable for authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());//inflates the view?
        setContentView(binding.getRoot());//getRoot method being important in viewBinding.
        mAuth =  FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager())); //gets the adapter code
        binding.tab.setupWithViewPager(binding.viewPager); // binds viewpager with tablayout
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.settings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                break;

            case R.id.groupChat:

                Intent in = new Intent(MainActivity.this, GroupChatActivity.class);
                startActivity(in);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}