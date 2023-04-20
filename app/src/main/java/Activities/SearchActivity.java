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
    private static final int PERSON_TYPE = 0;
    private static final int EVENT_TYPE = 1;
    private DataCache data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        data = DataCache.getInstance();
        EditText searchText = findViewById(R.id.searchViewText);

        ImageView icon = findViewById(R.id.search_view_icon);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.search));

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Person> people = data.searchPeopleByName(s.toString());
                ArrayList<Event> events = data.searchEventsByID(s.toString());
                SearchViewAdapter adapter = new SearchViewAdapter(people, events);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private class SearchViewAdapter extends RecyclerView.Adapter<searchViewHolder> {
        private final ArrayList<Person> people;
        private final ArrayList<Event> events;

        public SearchViewAdapter(ArrayList<Person> people, ArrayList<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_TYPE : EVENT_TYPE;
        }

        @Override
        public searchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event, parent, false);
            }

            return new searchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull searchViewHolder holder, int position) {
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

        public searchViewHolder(@NonNull View view, int viewType) {
            super(view);
            this.viewType = viewType;
            person = null;
            event = null;

            itemView.setOnClickListener(this);
            iconView = itemView.findViewById(R.id.iconView);
            if (viewType == PERSON_TYPE) {
                upperText = itemView.findViewById(R.id.nameOfPerson);
            } else {
                upperText = itemView.findViewById(R.id.nameOfEvent);
                lowerText = itemView.findViewById(R.id.userOfEvent);
            }
        }

        public void bind(Person person) {
            this.person = person;
            if (person.getGender().toLowerCase().equals("m")){
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.download));
            }
            else {
                iconView.setImageDrawable(getResources().getDrawable(R.drawable.girl));
            }
            upperText.setText(person.getFirstName() + " " + person.getLastName());
        }

        public void bind(Event event) {
            this.event = event;
            iconView.setImageDrawable(getResources().getDrawable(R.drawable.marker));
            upperText.setText(event.getEventType() + ": " + event.getCity() + ", "
                    + event.getCountry() + "(" + event.getYear() + ")");
            lowerText.setText(data.getPeople().get(event.getPersonID()).getFirstName() + " "
                    + data.getPeople().get(event.getPersonID()).getLastName());
        }

        @Override
        public void onClick(View v) {
            if (viewType == PERSON_TYPE) {
                Intent intent = new Intent();
                intent.putExtra("PERSON_ID",person.getPersonID());
                intent.setClass(SearchActivity.this, PersonActivity.class);
                SearchActivity.this.startActivity(intent);
            } else {
                data.setSelectEvent(data.getEvents().get(event.getEventID()));
                Intent intent = new Intent();
                intent.putExtra("EVENT_ID", event.getEventID());
                intent.setClass(SearchActivity.this, EventActivity.class);
                SearchActivity.this.startActivity(intent);
            }
        }
    }
}