package com.bektasoguzhan.gezbul;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static String LOG_TAG = "MapsActivity";
    private GoogleMap mMap;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    private TextView txtSelectedPlaceName;//DENEME İÇİN YAPILDI PROJE BİTERKEN SİL.
    private String KullaciID = "null";
    private Button mHospitalButton, mSchoolButton, mCafeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        start();
        autoComplete();
    }

    private void start() {
        mHospitalButton = (Button) findViewById(R.id.btnHospital);
        mSchoolButton = (Button) findViewById(R.id.btnSchool);
        mCafeButton = (Button) findViewById(R.id.btnCafe);
        txtSelectedPlaceName = (TextView) findViewById(R.id.txtSelectedPlaceName);
        Intent i = getIntent();
        KullaciID = i.getStringExtra("veri");//G+ veya Facebookla giren kullanıcıların ID sini çekmek için.
        txtSelectedPlaceName.setText(KullaciID);//Deneme İçin
    }

    public void hospital(View view) {
        Toast.makeText(MapsActivity.this, "HOSPİTAL", Toast.LENGTH_SHORT).show();
        /*Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        mMap.clear();
        String hospital = "hospital";
        String url = getUrl(latitude, longitude, hospital);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);
        Toast.makeText(MapsActivity.this, "Yakındaki Hastahaneler Gösteriliyor", Toast.LENGTH_SHORT).show();
*/
    }

    public void school(View view) {
        Toast.makeText(MapsActivity.this, "SCHOOL", Toast.LENGTH_SHORT).show();
    }

    public void cafe(View view) {
        Toast.makeText(MapsActivity.this, "CAFE", Toast.LENGTH_SHORT).show();
    }

    private void autoComplete() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment_autocomplete);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.e(LOG_TAG, "Place: " + place.getName() + "Koordinat: " + place.getLatLng());
                txtSelectedPlaceName.setText(String.format("Seçilen Yer : %s  - %s", place.getName(), place.getAddress()));//Deneme için
                LatLng location = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(location).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                //mMap.animateCamera(CameraUpdateFactory.zoomBy(12));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            }

            @Override
            public void onError(Status status) {
                Log.i(LOG_TAG, "Hata Oluştu : " + status);
                Toast.makeText(MapsActivity.this, "Yer Seçilemedi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //Kullanıcı iznini alma
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {//Eğer izin daha önceden alındıysa
            try {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

                mMap.clear();

                Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17));
                mMap.setMyLocationEnabled(true);
            } catch (Exception ex) {
                Log.d("İzin zaten verilmiş", ex.toString());
            }
        }
    }

    @Override//Eğer kullanıcının izni yoksa buraya geliyor . Burdan izin işlemleri yapılacak.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    mMap.clear();
                    Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17));
                    mMap.setMyLocationEnabled(true);
                } catch (Exception ex) {
                    Log.e("İzin verirken hata", ex.toString());
                }
            }
        }
    }
}