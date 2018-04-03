package com.bektasoguzhan.gezbul;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaveActivity extends AppCompatActivity {

    private Spinner mSpinner;
    private TextView mEditTextComment, mTextTitleName;
    private ArrayAdapter<String> dataAdapterForSpinner;
    private String[] forSpinnerString = {"0", "1", "2", "3", "4", "5"};
    private String kullaniciID, title, info = "", selectedType = "None";
    double lat, lon;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        start();

        //Spinner için adapterleri hazırlıyoruz.
        dataAdapterForSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, forSpinnerString);
        //Listelenecek verilerin görünümünü belirliyoruz.
        dataAdapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Hazırladğımız Adapter'leri Spinner'lara ekliyoruz.
        mSpinner.setAdapter(dataAdapterForSpinner);
    }

    public void start() {
        mEditTextComment = (TextView) findViewById(R.id.editTextComment);
        mTextTitleName = (TextView) findViewById(R.id.textTitleName);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        //Verilerimizin çekildiği yer

        Intent i = getIntent();
        kullaniciID = i.getStringExtra("kullaniciID");//G+ veya Facebookla giren kullanıcıların ID sini çekmek için.
        title = i.getStringExtra("title");
        lat = i.getDoubleExtra("lat", 0);
        lon = i.getDoubleExtra("lon", 0);
        info = i.getStringExtra("info");
        selectedType = i.getStringExtra("selectedType");
        Toast.makeText(this, "K I -> " + kullaniciID + "Title-> " + title + "lat-lon ->" + lat + "-" + lon, Toast.LENGTH_LONG).show();
        mTextTitleName.setText(title);
    }

    public void update(View view) {
        if (mEditTextComment == null) {
            Toast.makeText(this, "Yorum Kısmı Boş Bırakılamaz", Toast.LENGTH_LONG).show();
        } else {

        }
        Toast.makeText(this, "Update Tuşuna basıldı", Toast.LENGTH_LONG).show();

    }

    public void delete(View view) {
        if (mEditTextComment == null) {
            Toast.makeText(this, "Yorum Kısmı Boş Bırakılamaz", Toast.LENGTH_LONG).show();
        } else {

        }
        Toast.makeText(this, "Delete Tuşuna basıldı", Toast.LENGTH_LONG).show();

    }

    public void save(View view) {
        if (mEditTextComment.getText().toString() == "") {
            Toast.makeText(this, "Yorum Kısmı Boş Bırakılamaz", Toast.LENGTH_LONG).show();
        } else {//String kullaniciID, String title, String comment, string numberOfReviews, double lat, double lon
            AlertDialog.Builder builder = new AlertDialog.Builder(SaveActivity.this);
            builder.setTitle("GEZBUL");
            builder.setMessage("Bu yeri kaydetmek istediğinize eminmisiniz ? ");
            builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //HAYIR butonuna basılınca yapılacaklar.Sadece kapanması isteniyorsa boş bırakılacak
                }
            });
            builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //EVET butonuna basılınca yapılacaklar
                    try {
                        User kullanici = new User(title, mEditTextComment.getText().toString(), (mSpinner.getSelectedItem().toString()), lat, lon);
                        String key = dbRef.push().getKey();
                        dbRef.child("users").child(kullaniciID + "/" + key).setValue(kullanici);
                        Log.d("Save", "veri başarı ile kaydedildi.");
                        returnMapsActivity();
                        SaveActivity.this.finishAffinity();
                    } catch (Exception ex) {
                        Log.d("SAVE", "Veriyi kaydederken sorun oluştu." + ex.toString());
                    }
                }
            });
            builder.show();
        }
    }

    private void returnMapsActivity() {
        Intent intent = new Intent(SaveActivity.this, MapsActivity.class);
        intent.putExtra("kullaniciID", kullaniciID);
        intent.putExtra("selectedType", selectedType);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        intent.putExtra("title", title);
        info = "SaveActivity";
        intent.putExtra("info", info);
        startActivity(intent);
    }
}
