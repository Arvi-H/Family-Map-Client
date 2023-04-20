package Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.family_map_client.DataCache;
import com.example.family_map_client.MainActivity;
import com.example.family_map_client.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch lifeStoryLines;
    private Switch familyLines;
    private Switch spouseLines;

    private DataCache data = DataCache.getInstance();

    // This method is called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Enable the "up" button in the action bar to allow navigation to the parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get references to the life story, family tree, and spouse lines switches in the layout
        lifeStoryLines = findViewById(R.id.lifeStoryLinesSwitch);
        familyLines = findViewById(R.id.familyTreeLinesSwitch);
        spouseLines = findViewById(R.id.spouseLinesSwitch);

        // Set the switches to their previous values from the data cache
        if (data.isLifeEvent()) {
            lifeStoryLines.setChecked(true);
        }
        if (data.isFamilyEvent()) {
            familyLines.setChecked(true);
        }
        if (data.isSpouseEvent()) {
            spouseLines.setChecked(true);
        }

        // Set listeners for the switches to update the data cache when they are changed
        lifeStoryLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setLifeEvent(isChecked);
            }
        });
        familyLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setFamilyEvent(isChecked);
            }
        });
        spouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setSpouseEvent(isChecked);
            }
        });

        // Get a reference to the logout button in the layout
        RelativeLayout logoutLayout = findViewById(R.id.logoutButton);

        // Set a click listener for the logout button to clear the data cache, launch the MainActivity, and finish the activity
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.logout();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Launch the MainActivity with the setting flag and clear the activity stack
            startActivity(new Intent(this, MainActivity.class).putExtra("SETTING", true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}