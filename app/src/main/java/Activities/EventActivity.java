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
        DataCache data = DataCache.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_event);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putString("EVENT_ID", data.getSelectEvent().getEventID());

            FragmentManager fm = getSupportFragmentManager();
            Fragment mapFragment = new MapFragment();
            mapFragment.setArguments(arguments);
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            fragmentTransaction.add(R.id.map_fragment, mapFragment);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("SETTING", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        }
}