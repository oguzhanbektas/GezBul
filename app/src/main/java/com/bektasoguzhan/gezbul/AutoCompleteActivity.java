package com.bektasoguzhan.gezbul;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;


public class AutoCompleteActivity extends AppCompatActivity {
    private static String LOG_TAG = "AutoCompleteActivity";
//DENEME İÇİN YAPILDI İŞLEVİ YOK ARTIK. MAPS AKTİVİTY E AKTARILDI....

    private TextView txtSelectedPlaceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autocomplete);

        txtSelectedPlaceName = (TextView) this.findViewById(R.id.txtSelectedPlaceName);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment_autocomplete);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(LOG_TAG, "Place: " + place.getName());
                txtSelectedPlaceName.setText(String.format("Selected places : %s  - %s" , place.getName(), place.getAddress()));
            }

            @Override
            public void onError(Status status) {
                Log.i(LOG_TAG, "An error occurred: " + status);
                Toast.makeText(AutoCompleteActivity.this, "Place cannot be selected!!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
