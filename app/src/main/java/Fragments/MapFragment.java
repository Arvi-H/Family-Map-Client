package Fragments;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
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
import Model.Event;
import Model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private Map<String, Event> events;
    private final DataCache dataCache = DataCache.getInstance();
    private final Map <Marker, Event> mapOfMarkers = new HashMap<>();

//    private LinearLayout infoLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

//        infoLayout = view.findViewById(R.id.info_layout);
//        infoLayout.setOnClickListener(onClickInfoLayout);

        return view;
    }

//    View.OnClickListener onClickInfoLayout = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(getActivity(), PersonActivity.class);
//            Person person = data.getPersonByID().get(mapOfMarkers.get(currMarker).getPersonID());
//            data.setSelectPerson(person);
//            startActivity(intent);
//        }
//    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        events = dataCache.getEvents();

        addMarkers();
    }

    @Override
    public void onMapLoaded() {
    }

//    void clickMarker(Marker m) {
//        name.setOnClickListener(null);
//        event.setOnClickListener(null);
//        year.setOnClickListener(null);
//        icon.setOnClickListener(null);
//    }

    private void addMarkers() {
        // Define a HashMap to store the event types and their corresponding colors
        Map<String, Float> mapOfColors = new HashMap<>();

        // Predefine colors for known event types
        mapOfColors.put("birth", BitmapDescriptorFactory.HUE_RED);
        mapOfColors.put("death", BitmapDescriptorFactory.HUE_BLUE);
        mapOfColors.put("marriage", BitmapDescriptorFactory.HUE_GREEN);

        // Define the initial marker color value
        float markerColorValue = 50.0f;

        // Loop through our events
        for (Event currEvent : events.values()) {
            String eventType = currEvent.getEventType().toLowerCase();
            float color;

            if (mapOfColors.containsKey(eventType)) {
                color = mapOfColors.get(eventType);
            } else {
                color = markerColorValue;
                mapOfColors.put(eventType, color);
                markerColorValue += 50;
            }

            LatLng newMark = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            Marker newM = map.addMarker(new MarkerOptions()
                    .position(newMark)
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
                    .title(currEvent.getEventType()));
            map.animateCamera(CameraUpdateFactory.newLatLng(newMark));
            mapOfMarkers.put(newM, currEvent);
        }
    }
}