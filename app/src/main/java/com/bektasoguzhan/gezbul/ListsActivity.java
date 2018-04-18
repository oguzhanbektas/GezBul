package com.bektasoguzhan.gezbul;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    ArrayList<User> users;
    String kullaniciID;
    Button mBtnListele;
    ListView mListView;
    ArrayList<String> titles = new ArrayList<String>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        //Tanımlamalar yapıldı
        mListView = (ListView) findViewById(R.id.listView);
        mBtnListele = (Button) findViewById(R.id.btnListele);
        //intent çağrıldı
        Intent i = getIntent();
        kullaniciID = i.getStringExtra("kullaniciID");
        //Veri tabanı için bağlantılar sağlandı
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        users = new ArrayList<>();
        //Sürekli dinleme sağlandı users>kullaniciID
        myRef.child("users").child(kullaniciID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        User user = postSnapshot.getValue(User.class);
                        String xkey = postSnapshot.getKey();
                        Log.d("DATA :", "Title : " + user.getTitle() + " numberOfReviews : " + user.getNumberOfReviews() + " Comment : " + user.getComment() + " Lat : " + user.getLat() + " Lon: " + user.getLon() + "Key : " + postSnapshot.getKey() + "-----" + user.getKey());
                        // public User(String title, String comment, String numberOfReviews, double lat, double lon)
                        //Bu yapıya göre users listine eklenmeler yapıldı
                        users.add(
                                new User(
                                        user.getTitle(),
                                        user.getComment(),
                                        user.getNumberOfReviews(),
                                        user.getLat(),
                                        user.getLon(),
                                        xkey
                                ));
                    } catch (Exception ex) {
                        Log.d("Veri Çekerken Hata:", ex.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBtnListele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int k = 0; k < users.size(); k++) {
                    Log.d("Kullanıcı", users.get(k).getTitle() + "Key deneme : " + users.get(k).getKey());
                    titles.add(users.get(k).getTitle());
                }
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, titles);
                mListView.setAdapter(adapter);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //    Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListsActivity.this, SaveActivity.class);
                    intent.putExtra("kullaniciID", kullaniciID);
                    intent.putExtra("info", "Update-Save");
                    intent.putExtra("title", users.get(position).getTitle());
                    intent.putExtra("lat", users.get(position).getLat());
                    intent.putExtra("lon", users.get(position).getLon());
                    intent.putExtra("comment", users.get(position).getComment());
                    intent.putExtra("key", users.get(position).getKey());
                    intent.putExtra("numberOfReviews", users.get(position).getNumberOfReviews());
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.d("ListView de Hata : ", ex.toString());
                }
            }
        });

    }

}
