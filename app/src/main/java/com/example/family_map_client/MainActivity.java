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

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (getIntent().getExtras() != null) {
            notifyDone();
        } else if (currentFragment == null) {
            Fragment newFragment = new LoginFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, newFragment)
                    .commit();
            ((LoginFragment) newFragment).setListener(this);
        }
    }

    @Override
    public void notifyDone() {
        Fragment mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, mapFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
