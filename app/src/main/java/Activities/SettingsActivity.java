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
    private Switch familyTreeLines;
    private Switch spouseLines;

    private Switch fathersSide;
    private Switch mothersSide;
    private Switch maleEvents;
    private Switch femaleEvents;

    private DataCache data = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lifeStoryLines = findViewById(R.id.lifeStoryLinesSwitch);

        if (data.isLifeEvent()) {
            lifeStoryLines.setChecked(true);
        }

        lifeStoryLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setLifeEvent(true);
                } else {
                    data.setLifeEvent(false);
                }
            }
        });

        familyTreeLines = findViewById(R.id.familyTreeLinesSwitch);

        if (data.isFamilyEvent()) {
            familyTreeLines.setChecked(true);
        }
        familyTreeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setFamilyEvent(true);
                } else {
                    data.setFamilyEvent(false);
                }
            }
        });

        spouseLines = findViewById(R.id.spouseLinesSwitch);

        if (data.isSpouseEvent()) {
            spouseLines.setChecked(true);
        }
        spouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setSpouseEvent(true);
                } else {
                    data.setSpouseEvent(false);
                }
            }
        });

        maleEvents = findViewById(R.id.settingMaleEventsSwitch);

        if (data.isMale()) {
            maleEvents.setChecked(true);
        }
        maleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setMale(true);
                } else {
                    data.setMale(false);
                }
            }
        });

        femaleEvents = findViewById(R.id.settingFemaleEventSwitch);

        if (data.isFemale()) {
            femaleEvents.setChecked(true);
        }
        femaleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    data.setFemale(true);
                } else {
                    data.setFemale(false);
                }
            }
        });

        RelativeLayout logoutLayout = findViewById(R.id.logoutButton);
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

            Intent intent= new Intent(this, MainActivity.class);
            intent.putExtra("SETTING", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

        return true;
    }
}