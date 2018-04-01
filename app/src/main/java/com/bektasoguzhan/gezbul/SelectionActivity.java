package com.bektasoguzhan.gezbul;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    Button mNone, mCafe, mSchool, mShopping, mHospital;
    String kullaciID = null, selectedType = "None", info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        start();
        Intent i = getIntent();
        kullaciID = i.getStringExtra("kullaniciID");//G+ veya Facebookla giren kullanıcıların ID sini çekmek için.
        info = i.getStringExtra("info");
        Toast.makeText(this, "Kullanıcı id " + kullaciID + " İnfo " + info, Toast.LENGTH_SHORT).show(); //veriler doğru çekiliyor
    }

    private void start() {
        mNone = (Button) findViewById(R.id.btnNone);
        mCafe = (Button) findViewById(R.id.btnCafe);
        mSchool = (Button) findViewById(R.id.btnSchool);
        mShopping = (Button) findViewById(R.id.btnShopping);
        mHospital = (Button) findViewById(R.id.btnHospital);
    }

    private void transition(String selectedType) {
        Intent intent = new Intent(SelectionActivity.this, MapsActivity.class);
        intent.putExtra("kullaniciID", kullaciID);
        intent.putExtra("selectedType", selectedType);
        intent.putExtra("info", info);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNone: {
                selectedType = "none";
                transition(selectedType);
                break;
            }

            case R.id.btnCafe: {
                selectedType = "cafe";
                transition(selectedType);
                break;
            }
            case R.id.btnHospital: {
                selectedType = "hospital";
                transition(selectedType);
                break;
            }
            case R.id.btnSchool: {
                selectedType = "school";
                transition(selectedType);
                break;
            }
            case R.id.btnShopping: {
                selectedType = "shopping_mall";
                transition(selectedType);
                break;
            }
        }
    }
}
