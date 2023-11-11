package com.example.projecttest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
    private HashMap<Marker, MarkerInfo> markerInfoHashMap; //Marker와 MarkerInfo를 매핑하는 맵 선언
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //hashmap초기화
        markerInfoHashMap = new HashMap<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //위치 업데이트 콜백 설정
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                }
            }
        };

        //위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // 위치 업데이트 시작
            startLocationUpdates();
        }

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
                    Marker marker = mMap.addMarker(new MarkerOptions().position(coordinate).title(markerInfo.getAddress()));
                    marker.setTag(markerInfo);
                    markerInfoHashMap.put(marker, markerInfo); // HashMap에 매핑 추가
                } else {
                    mMap.addMarker(new MarkerOptions().position(coordinate));
                }
            }
        }

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
//        if (!coordinates.isEmpty()) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0), 10));
//        }

        // 기존 위치로 카메라 이동
        LatLng defaultLocation = new LatLng(37.5665, 126.9780); // 서울의 위도, 경도
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));


    }

    //위치 업데이트 시작
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000); // 5초마다 위치 업데이트
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
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

    //액티비티가 종료될 때 위치 업데이트 중지

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}