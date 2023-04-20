package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.family_map_client.DataCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.family_map_client.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Activities.PersonActivity;
import Activities.SearchActivity;
import Activities.SettingsActivity;
import Model.Event;
import Model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private List<Event> eventList;
    private List<Polyline> lineList = new ArrayList<>();
    private final Map<String, Float> colors = new HashMap();
    private Map<Marker, Event> markers = new HashMap<>();
    private DataCache dataCache = DataCache.getInstance();
    String selectedEventId = null;

    private Float marker = 60f;
    private Marker mainMarker;
    private TextView personName;
    private TextView eventName;
    private TextView year;
    private ImageView icon;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        // Get the selected event ID from the fragment arguments, if any
        if (getArguments() != null) {
            selectedEventId = getArguments().getString("EVENT_ID");
        }

        // If there is no selected event ID, enable options menu for this fragment
        if (selectedEventId == null) {setHasOptionsMenu(true);}

        // Find views by ID
        personName = view.findViewById(R.id.person_name);
        eventName = view.findViewById(R.id.event_details);
        year = view.findViewById(R.id.year);
        icon = view.findViewById(R.id.map_icon);

        // Get the Google Map asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null; // Make sure the map fragment is not null
        mapFragment.getMapAsync(this);

        // Return the inflated view
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        eventList = dataCache.getUserEvents();
        addMarkers();
    }

    // Define an OnClickListener for a text view
    View.OnClickListener onClickText = v -> {
        // Create an intent to start the PersonActivity
        Intent intent = new Intent(getActivity(), PersonActivity.class);

        // Get the Person object associated with the selected marker
        Person person = dataCache.getPeople().get(markers.get(mainMarker).getPersonID());

        // Put the person ID as an extra in the intent
        intent.putExtra("PERSON_ID", person.getPersonID());

        // Start the PersonActivity
        startActivity(intent);
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);     // Inflate the options menu layout
        super.onCreateOptionsMenu(menu, inflater);    // Call the superclass implementation
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Declare an intent variable to hold the intent to start an activity
        Intent intent;

        // Switch statement to handle different menu item selections
        switch (item.getItemId()) {
            // If the "Search" menu item is selected, start the SearchActivity
            case R.id.menu_item_search:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            // If the "Settings" menu item is selected, start the SettingsActivity
            case R.id.menu_item_settings:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            // If an unknown menu item is selected, call the superclass implementation
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLoaded() {}

    /**
     * Adds markers to the map for each event in the event list.
     */
    private void addMarkers() {
        // Set the onMarkerClickListener for the map
        map.setOnMarkerClickListener(mar -> {
            markerClick(mar);
            return true;
        });

        // Define the colors to use for different event types
        colors.put("birth", BitmapDescriptorFactory.HUE_RED);
        colors.put("death", BitmapDescriptorFactory.HUE_BLUE);
        colors.put("marriage", BitmapDescriptorFactory.HUE_GREEN);

        // Loop through each event in the event list
        for (Event currEvent : eventList) {
            // Determine the color for the marker based on the event type
            Float color = colors.get(currEvent.getEventType().toLowerCase());
            if (color == null) {
                // If no color is defined for this event type, generate a new color and store it in the map
                color = marker;
                marker += 15;
                colors.put(currEvent.getEventType().toLowerCase(), color);
            }

            // Add the marker to the map and store it in the markers map
            LatLng newMark = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            Marker newM = map.addMarker(new MarkerOptions()
                    .position(newMark)
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
                    .title(currEvent.getEventType()));
            markers.put(newM, currEvent);

            // If this event is the selected event, set the mainMarker
            if (dataCache.getSelectEvent() != null && dataCache.getSelectEvent().equals(currEvent)) {
                mainMarker = newM;
            }
        }

        // If a selected event was passed in, center the map on the mainMarker and update the UI
        if (selectedEventId != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(mainMarker.getPosition()));
            markerClick(mainMarker);
        }
    }



    void markerClick(Marker m) {
        // Get the Event and Person associated with the clicked Marker
        Event currE = markers.get(m);
        Person currP = dataCache.getPeople().get(currE.getPersonID());

        // Construct the person name, event info, and year info strings
        String newName = currP.getFirstName() + " " + currP.getLastName();
        StringBuilder eventInfo = new StringBuilder();
        eventInfo.append(currE.getEventType()).append(": ").append(currE.getCity()).append(", ").append(currE.getCountry());
        String yearInfo = "(" + currE.getYear() + ")";

        // Set the text and visibility for the person name, event info, and year info TextViews
        personName.setText(newName);
        eventName.setText(eventInfo);
        year.setText(yearInfo);
        personName.setVisibility(View.VISIBLE);
        eventName.setVisibility(View.VISIBLE);
        year.setVisibility(View.VISIBLE);

        // Set the icon drawable based on the person's gender
        int iconDrawableId = currP.getGender().toLowerCase().equals("m") ? R.drawable.download : R.drawable.girl;
        icon.setImageDrawable(getResources().getDrawable(iconDrawableId));
        icon.setVisibility(View.VISIBLE);

        // Set the onClickListener for all the TextViews and the icon
        personName.setOnClickListener(onClickText);
        eventName.setOnClickListener(onClickText);
        year.setOnClickListener(onClickText);
        icon.setOnClickListener(onClickText);

        // Save the clicked Marker as the mainMarker and the associated Event as the selected event in the data cache
        mainMarker = m;
        dataCache.setSelectEvent(currE);

        // Draw the associated lines on the map
        draw(currE);
    }

    /**
     * Draw lines on the map based on the current event and the types of lines that should be drawn
     * @param event The current event to draw lines from
     */
    private void draw(Event event) {
        removeLines();
        if (dataCache.isLifeEvent()){lifeStoryLines(event);}
        if (dataCache.isFamilyEvent()){
            familyLines(event);}
        if (dataCache.isSpouseEvent()){spouseLines(event);}
    }

    /**
     * Draw life story lines for a person based on their life events
     * @param currE The current event to start drawing lines from
     */
    private void lifeStoryLines(Event currE) {
        // Get the list of life events for the current person
        List<Event> lifeEvents = dataCache.getPeopleEventsList().get(currE.getPersonID());

        // Declare a variable to hold the previous event
        Event prevEvent = null;

        // Iterate through each life event for the current person and draw lines between them
        for (Event lifeEvent : lifeEvents) {
            if (prevEvent != null) {
                // Add a new polyline between the previous event and the current event
                Polyline newestLine = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(prevEvent.getLatitude(), prevEvent.getLongitude()),
                                new LatLng(lifeEvent.getLatitude(), lifeEvent.getLongitude()))
                        .color(0xff00BFFF)); // Blue
                lineList.add(newestLine);
            }

            // Set the previous event to the current event for the next iteration
            prevEvent = lifeEvent;
        }
    }

    /**
     * Draw family lines for a person based on their family relationships
     *
     * @param event The current event to start drawing lines from
     */
    private void familyLines(Event event) {
        familyTreeLines(dataCache.getPeople().get(event.getPersonID()), event, 10);
    }

    /**
     * Helper method to draw family tree lines recursively
     * @param person The current person to draw lines for
     * @param currEvent The current event to start drawing lines from
     * @param generation The current generation to draw lines for
     */
    private void familyTreeLines(Person person, Event currEvent, int generation) {
        if (person == null || generation == 0) { return; }

        if (person.getFatherID() != null) {
            addFamilyLine(currEvent, person.getFatherID(), 0xbbb444, generation);
        }

        if (person.getMotherID() != null) {
            addFamilyLine(currEvent, person.getMotherID(), 0xfffb6eee, generation);
        }
    }

    /**
     * Add a family line for a person's father or mother
     * @param currEvent The current event to start drawing lines from
     * @param parentID The ID of the parent to draw lines for
     * @param color The color to use for the line
     * @param generation The current generation to draw lines for
     */
    private void addFamilyLine(Event currEvent, String parentID, int color, int generation) {
        List<Event> eventsList = dataCache.getPeopleEventsList().get(parentID);
        for (Event event : eventsList) {
            if (dataCache.getEvents().containsValue(event)) {
                Polyline newestLine = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                new LatLng(event.getLatitude(), event.getLongitude()))
                        .color(color)
                        .width(generation));
                lineList.add(newestLine);
                familyTreeLines(dataCache.getPeople().get(parentID), event, generation / 2);
                break;
            }
        }
    }

    /**
     * Draw spouse lines for a person based on their spouse relationship
     *
     * @param currE The current event to start drawing lines from
     */
    private void spouseLines(Event currE) {
        Person currPerson = dataCache.getPeople().get(currE.getPersonID());
        List<Event> eventsList = dataCache.getPeopleEventsList().get(currPerson.getSpouseID());
        for (Event event : eventsList) {
            if (dataCache.getEvents().containsValue(event)) {
                Polyline newestLine = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(event.getLatitude(), event.getLongitude()),
                                new LatLng(currE.getLatitude(), currE.getLongitude()))
                        .color(0xFF8B00FF)); // Purple
                lineList.add(newestLine);
                break;
            }
        }
    }


    private void removeLines(){
        for (Polyline line : lineList) {line.remove();}
        lineList = new ArrayList<>();
    }
}