package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.family_map_client.DataCache;
import com.example.family_map_client.MainActivity;
import com.example.family_map_client.R;

import java.util.ArrayList;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {
    private static final int P_TYPE = 0;
    private static final int E_TYPE = 1;
    private DataCache data;

    // This method is called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get a reference to the RecyclerView in the layout and set its layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        // Get the data cache instance
        data = DataCache.getInstance();

        // Get a reference to the search text view in the layout
        EditText searchText = findViewById(R.id.searchViewText);

        // Set the search icon for the search text view
        ImageView icon = findViewById(R.id.search_view_icon);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.search));

        // Add a text change listener to the search text view
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Search for people and events based on the text in the search text view
                ArrayList<Person> people = data.searchPeopleByName(s.toString());
                ArrayList<Event> events = data.searchEventsByID(s.toString());

                // Create a new SearchViewAdapter with the search results and set it as the adapter for the RecyclerView
                SearchViewAdapter adapter = new SearchViewAdapter(people, events);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // This method is called when an options menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // If the home button is pressed, launch the MainActivity with the setting flag and clear the activity stack
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SETTING", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }


    private class SearchViewAdapter extends RecyclerView.Adapter<searchViewHolder> {
        private final ArrayList<Person> people;
        private final ArrayList<Event> events;

        // This class defines an adapter for the search results RecyclerView
        public SearchViewAdapter(ArrayList<Person> people, ArrayList<Event> events) {
            // Initialize the adapter with the given lists of people and events
            this.people = people;
            this.events = events;
        }

        // This method returns the view type of the item at the given position
        @Override
        public int getItemViewType(int position) {
            // If the position is less than the size of the people list, return PERSON_TYPE
            // Otherwise, return EVENT_TYPE
            return position < people.size() ? P_TYPE : E_TYPE;
        }

        // This method creates a new ViewHolder for the given view type
        @Override
        public searchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            // Inflate the appropriate layout based on the view type
            if (viewType == P_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event, parent, false);
            }

            // Return a new searchViewHolder with the inflated view and the given view type
            return new searchViewHolder(view, viewType);
        }

        // This method binds data to the ViewHolder at the given position
        @Override
        public void onBindViewHolder(@NonNull searchViewHolder holder, int position) {
            // If the position is less than the size of the people list, bind the person at the position to the ViewHolder
            // Otherwise, bind the event at the (position - people.size()) to the ViewHolder
            if (position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }


        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class searchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final DataCache data = DataCache.getInstance();

        private final int viewType;
        private final ImageView iconView;
        private final TextView upperText;
        private TextView lowerText;


        private Person person;
        private Event event;

        // This class defines a custom ViewHolder for the search results RecyclerView
        public searchViewHolder(@NonNull View view, int viewType) {
            super(view);
            // Initialize the ViewHolder with the given view type and null person and event objects
            this.viewType = viewType;
            person = null;
            event = null;

            // Set the onClickListener on the item view to launch the appropriate activity
            itemView.setOnClickListener(this);

            // Get references to the icon view and text views in the layout based on the view type
            iconView = itemView.findViewById(R.id.iconView);
            if (viewType == P_TYPE) {
                upperText = itemView.findViewById(R.id.nameOfPerson);
            } else {
                upperText = itemView.findViewById(R.id.nameOfEvent);
                lowerText = itemView.findViewById(R.id.userOfEvent);
            }
        }

        // This method binds a Person object to the ViewHolder
        public void bind(Person person) {
            // Set the person object and icon view based on the person's gender
            this.person = person;
            if (person.getGender().toLowerCase().equals("m")){
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.download));
            }
            else {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.girl));
            }
            // Set the text for the upper text view to the person's name
            upperText.setText(person.getFirstName() + " " + person.getLastName());
        }

        // This method binds an Event object to the ViewHolder
        public void bind(Event event) {
            // Set the event object and icon view to the marker icon
            this.event = event;
            iconView.setImageDrawable(getResources().getDrawable(R.drawable.marker));
            // Set the text for the upper and lower text views to the event's information
            upperText.setText(event.getEventType() + ": " + event.getCity() + ", "
                    + event.getCountry() + "(" + event.getYear() + ")");
            lowerText.setText(data.getPeople().get(event.getPersonID()).getFirstName() + " "
                    + data.getPeople().get(event.getPersonID()).getLastName());
        }

        // This method launches the appropriate activity when the ViewHolder is clicked
        @Override
        public void onClick(View v) {
            if (viewType == P_TYPE) {
                // If the view type is PERSON_TYPE, launch the PersonActivity for the selected person
                Intent intent = new Intent();
                intent.putExtra("PERSON_ID",person.getPersonID());
                intent.setClass(SearchActivity.this, PersonActivity.class);
                SearchActivity.this.startActivity(intent);
            } else {
                // If the view type is EVENT_TYPE, set the selected event in the data model and launch the EventActivity
                data.setSelectEvent(data.getEvents().get(event.getEventID()));
                Intent intent = new Intent();
                intent.putExtra("EVENT_ID", event.getEventID());
                intent.setClass(SearchActivity.this, EventActivity.class);
                SearchActivity.this.startActivity(intent);
            }
        }

    }
}