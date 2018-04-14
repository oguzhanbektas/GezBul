package com.bektasoguzhan.gezbul;

import android.Manifest;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.bektasoguzhan.gezbul.R.drawable.a_letter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {
    private static String LOG_TAG = "MapsActivity";
    private GoogleMap mMap;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    private String KullaciID = "null", selectedType = "none", title = null, info = null;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrentLocation;
    Button mKaydetButton;
    LatLng mLatLng;
    private int PROXIMITY_RADIUS = 100000, PLACE_PICKER_REQUEST = 1;
    private double position_latitude = 1, position_longitude = 1, latitude, longitude, end_latitude, end_longitude;//Konum değişkenleri


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

    private void start() {//Tanımlamalar
        Intent i = getIntent();
        KullaciID = i.getStringExtra("kullaniciID");//G+ veya Facebookla giren kullanıcıların ID sini çekmek için.
        selectedType = i.getStringExtra("selectedType");
        info = i.getStringExtra("info");
        Toast.makeText(this, "info -> " + info, Toast.LENGTH_SHORT).show();
        mKaydetButton = (Button) findViewById(R.id.kaydetbtn);

    }

    public void fill_select(String select) {
        mMap.clear();
        if (select == "none") {
            mMap.clear();
        } else {
            Object dataTransfer[] = new Object[2];
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            Toast.makeText(MapsActivity.this, select, Toast.LENGTH_SHORT).show();
            String url = getUrl(latitude, longitude, select);
            //mMap.addMarker(new MarkerOptions().position((new LatLng(40.593584899999996, 26.902812500000003))));
            //String url = getUrl(40.593584899999996, 26.902812500000003, select);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;

            getNearbyPlacesData.execute(dataTransfer);
            selectedType = "none";
        }
    }

    private void autoComplete() {//Otomatik arama yapmak için
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment_autocomplete);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.e(LOG_TAG, "Place: " + place.getName() + "Koordinat: " + place.getLatLng());
                LatLng location = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(location).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
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
        mMap.setOnMapLongClickListener(this);//Haritaya uzun basıldığında işlem yapabilmek için
        //Kullanıcı iznini alma
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {//Eğer izin daha önceden alındıysa
            try {
                buildGoogleApiClient();


            } catch (Exception ex) {
                Log.d("İzin zaten verilmiş", ex.toString());
            }
        }
        if (info.equals("SaveActivity")) {//İstenilen yeri kaydettikden sonra geri döndüğünde aynı yerden devam etsin diye.
            double y_lat, y_lon;
            String title;
            Intent i = getIntent();
            title = i.getStringExtra("title");
            y_lat = i.getDoubleExtra("lat", 1);
            y_lon = i.getDoubleExtra("lon", 1);
            LatLng latLng = new LatLng(y_lat, y_lon);
            mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        //  fill_select("campground");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override//Eğer kullanıcının izni yoksa buraya geliyor . Burdan izin işlemleri yapılacak.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    buildGoogleApiClient();
                } catch (Exception ex) {
                    Log.e("İzin verirken hata", ex.toString());
                }
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrentLocation != null) {
            mCurrentLocation.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Buradasın");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrentLocation = mMap.addMarker(markerOptions);


        //Kamera hareketi
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Locasyon Güncellendi");
        }
        try {//Koordinatları aldıktan sonra işlem yaptırdık.
            if (selectedType.equals("none")) {

            } else {
                fill_select(selectedType);
            }
        } catch (Exception ex) {
            Log.d("deneme", ex.toString());
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        Log.d("Marker ' a Tıklandı ", "id->" + marker.getId() + "\n title->" + marker.getTitle() + "\n position" + marker.getPosition()
                + "\n snippet->" + marker.getSnippet());
        title = marker.getTitle();
        position_latitude = marker.getPosition().latitude;
        position_longitude = marker.getPosition().longitude;
        mLatLng = marker.getPosition();
        mKaydetButton.setVisibility(View.VISIBLE);
        if (!info.equals("anonim")) {
            mKaydetButton.setText("Kaydet");
            mKaydetButton.setEnabled(true);
        } else if (info.equals("anonim")) {
            mKaydetButton.setText("Kaydet");
            mKaydetButton.setEnabled(false);
        }
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d("onMarkerDragStart", "Çalıştı");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d("onMarkerDrag", "Çalıştı");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;
        Log.d("onMarkerDragEnd", "Çalıştı");
        Log.d("end_lat", "" + end_latitude);
        Log.d("end_lng", "" + end_longitude);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {//Yakındaki yerleri çektiğimiz yer.
        Log.i("gelen tür", nearbyPlace);
        if (nearbyPlace.equals("none")) {
            return "none";
        } else if (!nearbyPlace.equals("none")) {
            StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + latitude + "," + longitude);
            googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
            googlePlacesUrl.append("&type=" + nearbyPlace);
            googlePlacesUrl.append("&sensor=true");
            googlePlacesUrl.append("&key=" + "AIzaSyADks2GNlxgv0Se-Cs6iqRK5WY_7hWs1nc");
            Log.d("getUrl", googlePlacesUrl.toString());
            return (googlePlacesUrl.toString());
        } else
            return "none";
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("onMapLongClick", "Haritaya uzun basıldı");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void Kaydet(View view) {
        Toast.makeText(this, title + " : " + mLatLng + "ayrı ayrı " + position_latitude + "-" + position_longitude, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, SaveActivity.class);
        i.putExtra("kullaniciID", KullaciID);
        i.putExtra("title", title);
        i.putExtra("lat", position_latitude);
        i.putExtra("lon", position_longitude);
        i.putExtra("info", info);
        i.putExtra("selectedType", selectedType);
        startActivity(i);

    }

}