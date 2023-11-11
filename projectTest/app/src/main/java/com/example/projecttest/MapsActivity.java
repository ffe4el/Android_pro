package com.example.projecttest;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
        HashMap<LatLng, MarkerInfo> markerInfoMap = (HashMap<LatLng, MarkerInfo>) getIntent().getSerializableExtra("markerInfoMap");

        // 인텐트에서 좌표 리스트를 가져옴
        if (coordinates != null) {
            for (LatLng coordinate : coordinates) {
                //정보가 있다면 마커에 정보 추가
                if (markerInfoMap != null && markerInfoMap.containsKey(coordinate)) {
                    MarkerInfo markerInfo = markerInfoMap.get(coordinate);
                    mMap.addMarker(new MarkerOptions().position(coordinate).title(markerInfo.getAddress())).setTag(markerInfo);
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

        // 마커 클릭 리스너 설정
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                if (markerInfo != null) {
                    showMarkerInfoDialog(markerInfo);
                }
                return true; //이벤트소비
            }
        });

        //첫번째 좌표로 카메라 이동
        if (!coordinates.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0), 10));
        }
    }

    private void showMarkerInfoDialog(MarkerInfo markerInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("상세 정보")
                .setMessage("도로명 주소: " + markerInfo.getAddress() + "\n"
                        + "면적: " + markerInfo.getArea() + "\n"
                        + "이용 가능 인원: " + markerInfo.getUser() + "\n"
                        + "선풍기 보유 대수: " + markerInfo.getFan() + "\n"
                        + "에어컨 보유 대수: " + markerInfo.getAir())
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 누르면 알림창 닫기
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}