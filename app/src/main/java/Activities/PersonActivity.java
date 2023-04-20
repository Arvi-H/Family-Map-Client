package Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.family_map_client.DataCache;
import com.example.family_map_client.MainActivity;
import com.example.family_map_client.R;

import java.util.ArrayList;
import java.util.List;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {
    private Person selectedPerson;

    List<Person> fam;
    List<Event> lifeEvents;

    private ExpandableListView list;
    private ExpandableListAdapter listAdapter;

    private DataCache data = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = DataCache.getInstance();
//        BuildHelper helper = new BuildHelper();
        Intent intent = getIntent();
        String pID = intent.getStringExtra("PERSON_ID");
        selectedPerson = data.getPeople().get(pID);
        data.setSelectPerson(selectedPerson);

        TextView firstNameView = findViewById(R.id.person_first_name);
        firstNameView.setText(selectedPerson.getFirstName());
        TextView lastNameView = findViewById(R.id.person_last_name);
        lastNameView.setText(selectedPerson.getLastName());
        TextView genderView = findViewById(R.id.person_gender);
        genderView.setText(selectedPerson.getGender());

        fam = data.family(selectedPerson.getPersonID());


        if (genderCheck(selectedPerson.getGender())){
            lifeEvents = data.getEventsFromPeople().get(selectedPerson.getPersonID());
        } else {
            lifeEvents = new ArrayList<>();
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        expandableListView.setAdapter(new ExpandableListAdapter());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home) {

            Intent intent= new Intent(this, MainActivity.class);
            intent.putExtra("SETTING", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        return true;
    }

    private boolean genderCheck(String g) {
        if (g.toLowerCase().equals("m") || g.toLowerCase().equals("male")) {
            return true;
        } else if (g.toLowerCase().equals("f") || g.toLowerCase().equals("female")){
            return true;
        }
        return false;

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int LIFE_EVENTS_POSITION = 0;
        private static final int FAMILY_POSITION = 1;


        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.size();
                case FAMILY_POSITION:
                    return fam.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_items, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    titleView.setText("Life Events");
                    break;
                case FAMILY_POSITION:
                    titleView.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View lifeEventView, int childPosition) {
            Event desiredEvent = lifeEvents.get(childPosition);
            if (desiredEvent!= null) {
                ImageView locationIconView = lifeEventView.findViewById(R.id.iconView);

                locationIconView.setImageDrawable(getResources().getDrawable(R.drawable.marker));

                TextView nameOfEventView = lifeEventView.findViewById(R.id.nameOfEvent);
                nameOfEventView.setText(desiredEvent.getEventType() + ": " + desiredEvent.getCity() + ", "
                        + desiredEvent.getCountry() + "(" + desiredEvent.getYear() + ")");

                TextView userOfEventView = lifeEventView.findViewById(R.id.userOfEvent);
                userOfEventView.setText(selectedPerson.getFirstName() + " " + selectedPerson.getLastName());

                lifeEventView.setOnClickListener(v -> {
                    data.setSelectEvent(desiredEvent);
                    Intent intent = new Intent();
                    intent.putExtra("EVENT_ID", desiredEvent.getEventID());
                    intent.setClass(PersonActivity.this, EventActivity.class);
                    PersonActivity.this.startActivity(intent);
                });
            }
        }

        private void initializeFamilyView(View familyView, int childPosition) {
            Person desiredPerson = fam.get(childPosition);
            if (desiredPerson!=null) {
                ImageView personIconView = familyView.findViewById(R.id.iconView);

                if (desiredPerson.getGender().toLowerCase().equals("m")){
                    personIconView.setImageDrawable(getResources().getDrawable(R.drawable.download));
                }
                else {
                    personIconView.setImageDrawable(getResources().getDrawable(R.drawable.girl));
                }

                TextView nameOfPersonView = familyView.findViewById(R.id.nameOfPerson);
                nameOfPersonView.setText(desiredPerson.getFirstName() + " " + desiredPerson.getLastName());

                TextView relationshipWithUserView = familyView.findViewById(R.id.relationshipWithUser);
                String relationshipWithUser = data.getRelationships().get(childPosition);
                relationshipWithUserView.setText(relationshipWithUser);

                familyView.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.putExtra("PERSON_ID", desiredPerson.getPersonID());
                    intent.setClass(PersonActivity.this, PersonActivity.class);
                    PersonActivity.this.startActivity(intent);
                });

            }
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


}