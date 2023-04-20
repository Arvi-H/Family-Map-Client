package com.example.family_map_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;

import com.example.family_map_client.R;

import Fragments.MapFragment;
import Fragments.LoginFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the current fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // Check if the intent has extras (i.e., if the user just logged out)
        if (getIntent().getExtras() != null) {
            // Show the map fragment
            notifyDone();
        } else if (currentFragment == null) {
            // If there is no current fragment, show the login fragment
            LoginFragment newFragment = new LoginFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, newFragment).commit();
            // Set the listener for the login fragment
            newFragment.setListener(this);
        }
    }


    @Override
    public void notifyDone() {
        // Create a new MapFragment instance
        Fragment mapFragment = new MapFragment();

        // Replace the current fragment with the MapFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).addToBackStack(null).commit();
    }

}
