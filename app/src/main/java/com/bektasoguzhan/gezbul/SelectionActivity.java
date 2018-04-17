package com.bektasoguzhan.gezbul;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    Button mNone, mCafe, mSchool, mShopping, mHospital, mbtnListele;
    int sayac = 0;
    String kullaciID = null, selectedType = "none", info = null;
    private String[] forSpinnerString = {"ATM", "Muhasebe", "Hava Alanı", "Luna Park", "Akvaryum", "Sanat Galarisi", "Fırın", "Güzellik Salonu", "Kitapçı", "Bowling", "Otobüs Durağı", "Kamp Alanı", "Araba Kiralama", "Araba Tamircisi", "Mezarlık", "Kilise", "Belediye Binası", "Market", "Adliye", "Kütüphane", "Pansiyon", "Camii", "Park", "Otopark", "Eczane", "Restaurant", "Karavan Park Alanı", "Stadyum", "Metro İstasyonu", "Super Market", "Sinagog", "Taxi Durağı", "Tren İstasyonu", "Hayvanat Bahçesi"};
    private String[] xForSpinnerString = {"atm", "accounting", "airport", "amusement_park", "aquarium", "art_gallery", "bakery", "beauty_salon", "book_store", "bowling_alley", "bus_station", "campground", "car_rental", "car_repair", "cemetery", "church", "city_hall", "convenience_store", "courthouse", "library", "lodging", "mosque", "park", "parking", "pharmacy", "restaurant", "rv_park", "stadium", "subway_station", "supermarket", "synagogue", "taxi_stand", "train_station", "zoo"};
    private ArrayAdapter<String> dataAdapterForSpinner;
    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        start();
        Intent i = getIntent();
        kullaciID = i.getStringExtra("kullaniciID");//G+ veya Facebookla giren kullanıcıların ID sini çekmek için.
        info = i.getStringExtra("info");
        // Toast.makeText(this, "Kullanıcı id " + kullaciID + " İnfo " + info, Toast.LENGTH_SHORT).show(); //veriler doğru çekiliyor
        mSpinner = (Spinner) findViewById(R.id.spinner);
        //Spinner için adapterleri hazırlıyoruz.
        dataAdapterForSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, forSpinnerString);
        //Listelenecek verilerin görünümünü belirliyoruz.
        dataAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Hazırladğımız Adapter'leri Spinner'lara ekliyoruz.
        mSpinner.setAdapter(dataAdapterForSpinner);
        if (info.equals("anonim")) {
            mbtnListele.setVisibility(View.INVISIBLE);
        } else {
            mbtnListele.setVisibility(View.VISIBLE);
        }
    }

    private void start() {//İhtiyaç olur diye butonların tanımı yapıldı. Btn Listele için faydası oldu
        mNone = (Button) findViewById(R.id.btnNone);
        mCafe = (Button) findViewById(R.id.btnCafe);
        mSchool = (Button) findViewById(R.id.btnSchool);
        mShopping = (Button) findViewById(R.id.btnShopping);
        mHospital = (Button) findViewById(R.id.btnHospital);
        mbtnListele = (Button) findViewById(R.id.btnListele);
    }

    private void transition(String selectedType) {//Diğer forma geçiş için bütün butonlar için ayrı ayrı yapmak yerine bu yapı kuruldu
        Intent intent = new Intent(SelectionActivity.this, MapsActivity.class);
        intent.putExtra("kullaniciID", kullaciID);
        intent.putExtra("selectedType", selectedType);
        intent.putExtra("info", info);
        startActivity(intent);
    }

    private void Belirle() {//Diğer yerlerde seçilen ifadenin kaçıncı sırada olduğunu öğrenmek için
        for (int i = 0; i < forSpinnerString.length; i++) {
            if (forSpinnerString[i].equals(mSpinner.getSelectedItem().toString())) {
                sayac = i;
                //    Log.d("Sayac=>", String.valueOf(sayac));
            }
        }
    }

    @Override
    public void onClick(View v) {//Click işlemleri yapıldı
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
            case R.id.btnBank: {
                selectedType = "bank";
                transition(selectedType);
                break;
            }
            case R.id.btnBar: {
                selectedType = "bar";
                transition(selectedType);
                break;
            }
            case R.id.btnGasStation: {
                selectedType = "gas_station";
                transition(selectedType);
                break;
            }
            case R.id.btnHairCare: {
                selectedType = "hair_care";
                transition(selectedType);
                break;
            }
            case R.id.btnGYM: {
                selectedType = "gym";
                transition(selectedType);
                break;
            }
            case R.id.btnMuseum: {
                selectedType = "museum";
                transition(selectedType);
                break;
            }
            case R.id.btnGo: {
                Belirle();
                selectedType = xForSpinnerString[sayac];
                transition(selectedType);
                break;
            }
            case R.id.btnListele: {
                Intent z = new Intent(SelectionActivity.this, ListsActivity.class);
                z.putExtra("kullaniciID", kullaciID);
                startActivity(z);
                break;
            }
        }
    }
}
