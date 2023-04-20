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
    private Person person;
    List<Person> family;
    List<Event> events;

    private DataCache data = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the data cache instance
        data = DataCache.getInstance();

        // Get the ID of the person to display from the intent that started this activity
        Intent intent = getIntent();
        String pID = intent.getStringExtra("PERSON_ID");

        // Get the Person object corresponding to the given ID
        person = data.getPeople().get(pID);
        // Set the text of the TextViews to display the person's name and gender
        TextView firstNameView = findViewById(R.id.person_first_name);
            firstNameView.setText(person.getFirstName());
        TextView lastNameView = findViewById(R.id.person_last_name);
            lastNameView.setText(person.getLastName());
        TextView genderView = findViewById(R.id.person_gender);
            genderView.setText(person.getGender());


        // Get the family members of the person
        family = data.getFamily(person.getPersonID());

        // If the person is male, get their events, otherwise create an empty list of events
        if (gender(person.getGender())){
            events = data.getPeopleEventsList().get(person.getPersonID());
        } else {
            events = new ArrayList<>();
        }

        // Set up the ExpandableListView to display the events
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new ExpandableListAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the selected item is the home button in the action bar
        if (item.getItemId() == android.R.id.home) {
            // Create an intent to launch the MainActivity with the "SETTING" extra
            Intent intent = new Intent(this, MainActivity.class).putExtra("SETTING", true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Start the MainActivity with the new intent
            startActivity(intent);
        }

        // Return true to indicate that the event has been consumed
        return true;
    }

    private boolean gender(String g) {
        return g.toLowerCase().matches("m|male|f|female");
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
            // Determine the number of child items in the specified group
            switch (groupPosition) {
                // If the group is for life events, return the number of events
                case LIFE_EVENTS_POSITION:
                    return events.size();
                // If the group is for family members, return the number of family members
                case FAMILY_POSITION:
                    return family.size();
                // If the group position is unrecognized, throw an exception
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }


        @Override
        public Object getGroup(int groupPosition) {return null;}

        @Override
        public Object getChild(int groupPosition, int childPosition) {return null;}

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
            // Check if the convertView is null (i.e., if the view needs to be inflated)
            if(convertView == null) {
                // Inflate the view using the list_items layout
                convertView = getLayoutInflater().inflate(R.layout.list_items, parent, false);
            }

            // Get the TextView for the group title
            TextView titleView = convertView.findViewById(R.id.listTitle);

            // Set the text of the title TextView based on the group position
            switch (groupPosition) {
                // If the group is for life events, set the title to "Life Events"
                case LIFE_EVENTS_POSITION:
                    titleView.setText("Life Events");
                    break;
                // If the group is for family members, set the title to "Family"
                case FAMILY_POSITION:
                    titleView.setText("Family");
                    break;
                // If the group position is unrecognized, throw an exception
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            // Return the view for the group title
            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            // Determine the type of child view to create based on the group position
            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    // If the group position is LIFE_EVENTS_POSITION, inflate the event layout
                    itemView = getLayoutInflater().inflate(R.layout.event, parent, false);
                    // Initialize the view using the initializeLifeEventView() method
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_POSITION:
                    // If the group position is FAMILY_POSITION, inflate the person layout
                    itemView = getLayoutInflater().inflate(R.layout.person, parent, false);
                    // Initialize the view using the initializeFamilyView() method
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    // If the group position is neither LIFE_EVENTS_POSITION nor FAMILY_POSITION, throw an exception
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            // Return the view that represents the child item
            return itemView;
        }


        // This method initializes the views in the life event layout with the appropriate data
        private void initializeLifeEventView(View lifeEventView, int childPosition) {
            // Get the Event object corresponding to the child position in the list
            Event desiredEvent = events.get(childPosition);

            // Check if the event object is not null
            if (desiredEvent!= null) {
                // Get a reference to the location icon view in the layout
                ImageView locationIconView = lifeEventView.findViewById(R.id.iconView);

                // Set the icon for the location icon view
                locationIconView.setImageDrawable(getResources().getDrawable(R.drawable.marker));

                // Get a reference to the name of event text view in the layout
                TextView nameOfEventView = lifeEventView.findViewById(R.id.nameOfEvent);
                // Set the text for the name of event text view
                nameOfEventView.setText(desiredEvent.getEventType() + ": " + desiredEvent.getCity() + ", "
                        + desiredEvent.getCountry() + "(" + desiredEvent.getYear() + ")");

                // Get a reference to the user of event text view in the layout
                TextView userOfEventView = lifeEventView.findViewById(R.id.userOfEvent);
                // Set the text for the user of event text view
                userOfEventView.setText(person.getFirstName() + " " + person.getLastName());

                // Set an onClickListener on the life event view to launch the EventActivity when clicked
                lifeEventView.setOnClickListener(v -> {
                    // Set the selected event in the data model
                    data.setSelectEvent(desiredEvent);
                    // Create an Intent to launch the EventActivity
                    Intent intent = new Intent();
                    intent.putExtra("EVENT_ID", desiredEvent.getEventID());
                    intent.setClass(PersonActivity.this, EventActivity.class);
                    // Start the EventActivity with the given intent
                    PersonActivity.this.startActivity(intent);
                });
            }
        }

        // This method initializes the views in the family member layout with the appropriate data
        private void initializeFamilyView(View familyView, int childPosition) {
            // Get the Person object corresponding to the child position in the list
            Person desiredPerson = family.get(childPosition);

            // Check if the person object is not null
            if (desiredPerson!=null) {
                // Get a reference to the person icon view in the layout
                ImageView personIconView = familyView.findViewById(R.id.iconView);

                // Set the icon for the person icon view based on the person's gender
                if (desiredPerson.getGender().toLowerCase().equals("m")){
                    personIconView.setImageDrawable(getResources().getDrawable(R.drawable.download));
                }
                else {
                    personIconView.setImageDrawable(getResources().getDrawable(R.drawable.girl));
                }

                // Get a reference to the name of person text view in the layout
                TextView nameOfPersonView = familyView.findViewById(R.id.nameOfPerson);
                // Set the text for the name of person text view
                nameOfPersonView.setText(desiredPerson.getFirstName() + " " + desiredPerson.getLastName());

                // Get a reference to the relationship with user text view in the layout
                TextView relationshipWithUserView = familyView.findViewById(R.id.relationshipWithUser);
                // Get the relationship with the user for the person
                String relationshipWithUser = data.getConnections().get(childPosition);
                // Set the text for the relationship with user text view
                relationshipWithUserView.setText(relationshipWithUser);

                // Set an onClickListener on the family member view to launch the PersonActivity when clicked
                familyView.setOnClickListener(v -> {
                    // Create an Intent to launch the PersonActivity
                    Intent intent = new Intent();
                    intent.putExtra("PERSON_ID", desiredPerson.getPersonID());
                    intent.setClass(PersonActivity.this, PersonActivity.class);
                    // Start the PersonActivity with the given intent
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