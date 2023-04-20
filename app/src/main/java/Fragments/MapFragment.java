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
    private SupportMapFragment mapFragment;
    private List<Event> events;

    private Map<String, Float> mapOfColors = new HashMap();
    private Map<Marker, Event> mapOfMarkers = new HashMap<>();
    private List<Polyline> listOfLines = new ArrayList<>();

    private DataCache data = DataCache.getInstance();
    private Float marker = 60f;
    private Marker currMarker;
    private TextView name;
    private TextView event;
    private TextView year;
    private ImageView icon;

    String ifActivity = null;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        if (getArguments() != null){
            ifActivity = getArguments().getString("EVENT_ID");
        }
        if (ifActivity == null){
            setHasOptionsMenu(true);
        }

        name = view.findViewById(R.id.person_name);
        event = view.findViewById(R.id.event_details);
        year = view.findViewById(R.id.year);
        icon = view.findViewById(R.id.map_icon);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        events = data.getUserEvents();

        addMarkers();
    }

    View.OnClickListener onClickText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            Person person = data.getPeople().get(mapOfMarkers.get(currMarker).getPersonID());
            data.setSelectPerson(person);
            intent.putExtra("PERSON_ID", person.getPersonID());
            startActivity(intent);

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_settings:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLoaded() {}

    private void addMarkers(){
        float color = 0.0f;

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker mar) {
                clickMarker(mar);
                return true;
            }
        });

        for (Event currEvent : events){
            if (currEvent.getEventType().toLowerCase().equals("birth")) {
                color = BitmapDescriptorFactory.HUE_RED;
            } else if (currEvent.getEventType().toLowerCase().equals("death")) {
                color = BitmapDescriptorFactory.HUE_BLUE;
            } else if (currEvent.getEventType().toLowerCase().equals("marriage")) {
                color = BitmapDescriptorFactory.HUE_GREEN;
            } else if (mapOfColors.containsKey(currEvent.getEventType().toLowerCase())){
                color = mapOfColors.get(currEvent.getEventType().toLowerCase());
            }
            else {
                mapOfColors.put(currEvent.getEventType().toLowerCase(), marker);
                color = marker;
                marker += 15;
            }

            LatLng newMark = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            Marker newM = map.addMarker(new MarkerOptions().position(newMark).icon(BitmapDescriptorFactory.defaultMarker(color)).title(currEvent.getEventType()));
            map.animateCamera(CameraUpdateFactory.newLatLng(newMark));
            mapOfMarkers.put(newM, currEvent);
            if (data.getSelectEvent() != null) {
                if (data.getSelectEvent().equals(currEvent)) {
                    currMarker = newM;
                }
            }
        }

        if (ifActivity != null){
            map.moveCamera(CameraUpdateFactory.newLatLng(currMarker.getPosition()));
            clickMarker(currMarker);
        }
    }

    void clickMarker(Marker m) {
        Event currE = mapOfMarkers.get(m);
        Person currP = data.getPeople().get(currE.getPersonID());
        String newName = currP.getFirstName() + " " + currP.getLastName();
        String eventInfo = currE.getEventType() + ": " + currE.getCity() + ", " + currE.getCountry();
        String yearInfo = "(" + currE.getYear() + ")";

        name.setText(newName);
        name.setVisibility(View.VISIBLE);
        name.setOnClickListener(onClickText);

        event.setText(eventInfo);
        event.setVisibility(View.VISIBLE);
        event.setOnClickListener(onClickText);

        year.setText(yearInfo);
        year.setVisibility(View.VISIBLE);
        year.setOnClickListener(onClickText);

        if (currP.getGender().toLowerCase().equals("m")){
            icon.setImageDrawable(getResources().getDrawable(R.drawable.download));
        } else {
            icon.setImageDrawable(getResources().getDrawable(R.drawable.girl));
        }

        icon.setVisibility(View.VISIBLE);
        icon.setOnClickListener(onClickText);

        currMarker = m;
        data.setSelectEvent(currE);
        drawLines(currE);
    }

    private void drawLines(Event currE) {
        removeLines();
        if (data.isLifeEvent()){
            lifeStoryLines(currE);
        }
        if (data.isFamilyEvent()){
            familyTreeLines(currE);
        }
        if (data.isSpouseEvent()){
            spouseLines(currE);
        }
    }

    private void lifeStoryLines(Event currE){
        List<Event> lifeEvents = data.getEventsFromPeople().get(currE.getPersonID());
        Event currEvent = null;
        if (lifeEvents.size() > 1) {
            for (Event lifeEvent : lifeEvents) {
                if (data.getEvents().containsValue(lifeEvent)){
                    if (currEvent != null) {
                        Polyline newestLine = map.addPolyline(new PolylineOptions()
                                .add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                        new LatLng(lifeEvent.getLatitude(), lifeEvent.getLongitude()))
                                .color(0xffF9A825));
                        listOfLines.add(newestLine);
                    }
                    currEvent = lifeEvent;

                }
            }
        } else if (lifeEvents.size() <= 1) {

        }
    }

    private void familyTreeLines(Event currE){
        familyTreeLinesHelper(data.getPeople().get(currE.getPersonID()), currE, 10);
    }

    private void familyTreeLinesHelper(Person currPerson, Event currEvent, int generation){
        if (currPerson.getFatherID() != null){
            addFatherLines(currPerson, currEvent, generation);
        }
        if (currPerson.getMotherID() != null){
            addMotherLines(currPerson, currEvent, generation);
        }
    }

    private void addFatherLines(Person currPerson, Event currEvent, int generation){
        List<Event> eventsList = data.getEventsFromPeople().get(currPerson.getFatherID());

        for (int i = 0; i < eventsList.size(); i++) {
            if (data.getEvents().containsValue(eventsList.get(i))) {
                Event validEvent = eventsList.get(i);

                Polyline newestLine = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                new LatLng(validEvent.getLatitude(), validEvent.getLongitude()))
                        .color(0xbbb444)
                        .width(generation));
                listOfLines.add(newestLine);

                Person father = data.getPeople().get(currPerson.getFatherID());
                familyTreeLinesHelper(father, validEvent, generation / 2);
                return;
            }
        }
    }

    private void addMotherLines(Person currPerson, Event currEvent, int generation){
        List<Event> eventsList = data.getEventsFromPeople().get(currPerson.getMotherID());

        for (int i = 0; i < eventsList.size(); i++) {
            if (data.getEvents().containsValue(eventsList.get(i))) {
                Event validEvent = eventsList.get(i);

                Polyline newestLine = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                new LatLng(validEvent.getLatitude(), validEvent.getLongitude()))
                        .color(0xfffb6eee)
                        .width(generation));
                listOfLines.add(newestLine);

                Person mother = data.getPeople().get(currPerson.getMotherID());
                familyTreeLinesHelper(mother, validEvent, generation / 2);
                return;
            }
        }
    }

    private void spouseLines(Event currE){
        Person currPerson = data.getPeople().get(currE.getPersonID());
        List<Event> eventsList = data.getEventsFromPeople().get(currPerson.getSpouseID());

//        Filter filter = model.getFilter();

//        if (filter.containsEventType(currEvent.getEventType())) {
        for (int i = 0; i < eventsList.size(); i++) {
            if (data.getEvents().containsValue(eventsList.get(i))) {
                Event spouseValidEvent = eventsList.get(i);

                Polyline newestLine = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(spouseValidEvent.getLatitude(), spouseValidEvent.getLongitude()),
                                new LatLng(currE.getLatitude(), currE.getLongitude()))
                        .color(0xccc8ccc6));
                listOfLines.add(newestLine);
                break;
            }
        }
//        }
    }

    private void removeLines(){
        for (Polyline curr : listOfLines){
            curr.remove();
        }
        listOfLines = new ArrayList<>();
    }
}