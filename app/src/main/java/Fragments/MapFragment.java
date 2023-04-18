package Fragments;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.family_map_client.DataCache;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.family_map_client.R;

import java.util.Map;

import Model.Event;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private Map<String, Event> events;
    private final DataCache dataCache = DataCache.getInstance();

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

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        events = dataCache.getEvents();

        addMarkers();
    }

    @Override
    public void onMapLoaded() {}

    private void addMarkers(){
        for(Event currEvent : events.values()){
            LatLng newMark = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            map.addMarker(new MarkerOptions().position(newMark).title(currEvent.getEventType()));
            map.animateCamera(newLatLng(newMark));
        }
    }
}