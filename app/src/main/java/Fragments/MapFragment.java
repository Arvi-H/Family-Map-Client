package Fragments;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import Activities.PersonActivity;
import Activities.SearchActivity;
import Activities.SettingsActivity;
import Model.Event;
import Model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Map<String, Event> events;
    private Map<String, Float> mapOfColors = new HashMap();
    private Map<Marker, Event> mapOfMarkers = new HashMap<>();
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
        events = data.getEvents();

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

        for (Event currEvent : events.values()){
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
    }
}