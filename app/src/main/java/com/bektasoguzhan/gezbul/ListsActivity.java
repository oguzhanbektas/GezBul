package com.bektasoguzhan.gezbul;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    ArrayList<User> users;

    String kullaciID;
    String titles[], numberOfReviews[], comment[];
    Double lat[], lon[];
    Button mBtnListele;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        mBtnListele = (Button) findViewById(R.id.btnListele);
        Intent i = getIntent();
        kullaciID = i.getStringExtra("kullaniciID");
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        users = new ArrayList<>();

        myRef.child("users").child(kullaciID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        User user = postSnapshot.getValue(User.class);
                        String xkey=postSnapshot.getKey();
                        Log.d("DATA :", "Title : " + user.getTitle() + " numberOfReviews : " + user.getNumberOfReviews() + " Comment : " + user.getComment() + " Lat : " + user.getLat() + " Lon: " + user.getLon() + "Key : " + postSnapshot.getKey()+"-----"+user.getKey());
                        // public User(String title, String comment, String numberOfReviews, double lat, double lon) {
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
                }

            }
        });
    }

}
