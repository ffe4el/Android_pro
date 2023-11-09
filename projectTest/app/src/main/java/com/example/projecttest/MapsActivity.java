package com.example.projecttest;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.projecttest.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<LatLng> coordinates = getIntent().getParcelableArrayListExtra("coordinates");
        HashMap<LatLng, String> markerInfoMap = (HashMap<LatLng, String>) getIntent().getSerializableExtra("markerInfoMap");

        // 인텐트에서 좌표 리스트를 가져옴
        if (coordinates != null) {
            for (LatLng coordinate : coordinates) {
                //정보가 있다면 마커에 정보 추가
                if (markerInfoMap != null && markerInfoMap.containsKey(coordinate)) {
                    mMap.addMarker(new MarkerOptions().position(coordinate).title(markerInfoMap.get(coordinate)));
                }else {
                    mMap.addMarker(new MarkerOptions().position(coordinate));
                }
            }
        }

//        LatLng SEOUL = new LatLng(37.556, 126.97);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국 수도");
//        mMap.addMarker(markerOptions);

        //첫번째 좌표로 카메라 이동
        if (!coordinates.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0), 10));
        }
    }
}