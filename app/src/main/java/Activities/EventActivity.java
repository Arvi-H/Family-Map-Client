package Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.family_map_client.DataCache;
import com.example.family_map_client.MainActivity;
import com.example.family_map_client.R;

import Fragments.MapFragment;

public class EventActivity extends AppCompatActivity {
    DataCache dataCache = DataCache.getInstance();

    // This method is called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        // Enable the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent from the previous activity
        Intent intent = getIntent();
        Bundle arguments = new Bundle();

        // Set the selected event ID as an argument to be passed to the map fragment
        arguments.putString("EVENT_ID", dataCache.getSelectEvent().getEventID());

        // Get the fragment manager and create a new instance of the map fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();
        mapFragment.setArguments(arguments);

        // Begin a new fragment transaction and add the map fragment to the activity
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.map_fragment, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    // This method is called when a menu item in the action bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the back button is selected, return to the previous activity
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SETTING", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}