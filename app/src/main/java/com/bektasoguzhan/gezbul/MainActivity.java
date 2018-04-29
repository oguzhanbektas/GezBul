package com.bektasoguzhan.gezbul;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //Firebase i sürekli dinlemek için
    private static final int requestCode_SIGN_IN = 1461;//İstediğimiz değeri verebiliriz sabit olmak zorunda (on Aktivity Result için)
    private GoogleApiClient mGoogleApiClient;
    //Buradan sonrası gerekli tanımlamalar
    private TextView mTextViewAd, mDurumTextView, mGirisTextview;
    private Button mSignInButtonGoogle, mSignOutButton, mSignInButtonFacebook, mDevamButton, mAnonimButton;
    private String KullaniciID = "null";
    private ImageView mImageView;
    String info = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();//Layouttaki viewleri atama işlemi için yazılan fonk
        try {
            mAuth = FirebaseAuth.getInstance();//Bağlantı kuruldu
            mAuthListener = new FirebaseAuth.AuthStateListener() {//Sürekli dinleme sağlantı
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.e("Firebase Login", "Kullanıcı Artık Yetkili. Kullanıcı ID : " + user.getUid() + " Email->" + user.getEmail() + " Name->" + user.getDisplayName() + "foto URL->" + user.getPhotoUrl());
                        mTextViewAd.setText("Sn. " + user.getDisplayName());
                        KullaniciID = user.getUid();
                        final String imgURL = String.valueOf(user.getPhotoUrl());
                        new DownLoadImageTask(mImageView).execute(imgURL);//Profil fotosu için fonksiyon
                    } else {
                        Log.e("Firebase Login", "Kullanıcı Artık Yetkili Değil.");
                    }
                }
            };
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception ex) {
            Log.e("Hata onCreate'de ->", ex.toString());
        }
    }

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {//Profil fotosu için
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    private void start() {//Her view in tanımı yapıldı
        mTextViewAd = (TextView) findViewById(R.id.nameTextview);
        mSignInButtonGoogle = (Button) findViewById(R.id.signIn_button);
        mSignOutButton = (Button) findViewById(R.id.signOut_button);
        mSignInButtonFacebook = (Button) findViewById(R.id.signIn_button_facebook);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mDevamButton = (Button) findViewById(R.id.buttonDevam);
        mDurumTextView = (TextView) findViewById(R.id.durumTextview);
        mGirisTextview = (TextView) findViewById(R.id.girisTextview);
        mAnonimButton = (Button) findViewById(R.id.anonim_button);
        hide();
    }

    private void hide() {
        //Visible işlemleri
        mTextViewAd.setVisibility(View.INVISIBLE);
        mSignOutButton.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
        mDurumTextView.setVisibility(View.INVISIBLE);
        mDevamButton.setVisibility(View.INVISIBLE);
        mSignInButtonFacebook.setVisibility(View.VISIBLE);
        mSignInButtonGoogle.setVisibility(View.VISIBLE);
        mGirisTextview.setVisibility(View.VISIBLE);
        mAnonimButton.setVisibility(View.VISIBLE);
    }

    private void show() {
        mTextViewAd.setVisibility(View.VISIBLE);
        mSignOutButton.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        mDurumTextView.setVisibility(View.VISIBLE);
        mDevamButton.setVisibility(View.VISIBLE);
        mSignInButtonFacebook.setVisibility(View.INVISIBLE);
        mSignInButtonGoogle.setVisibility(View.INVISIBLE);
        mAnonimButton.setVisibility(View.INVISIBLE);
        mGirisTextview.setVisibility(View.INVISIBLE);
    }

    public void gecis(View view) {//Google Map ekranına geçiş
        try {//KullaniciID yolla.
            Intent i = new Intent(MainActivity.this, SelectionActivity.class);
            i.putExtra("kullaniciID", KullaniciID);
            i.putExtra("info", info);
            startActivity(i);
            Log.e("Geçiş", "SelectionActivity aktivity e geçildi.");
        } catch (Exception ex) {
            Log.e("Geçiş HATA !", "HATA-->" + ex.toString());
        }

    }

    public void signInFacebook(View view) {//Facebook ile giriş
        try {
            info = "facebook";
            show();

        } catch (Exception ex) {
            Log.e("Facebook ile Giriş", ex.toString());
        }
    }

    public void signIn(View view) {//google+ ile giriş
        try {
            info = "google";
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, requestCode_SIGN_IN);
        } catch (Exception ex) {
            Log.e("Google+ ile Giriş", ex.toString());
        }
    }

    public void signOut(View view) {//google+ için çıkış
        if (info == "google") {
            try {
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Log.e("Google Login", "Google oturumu kapatıldı. " + status.getStatus());
                            }
                        });
                KullaniciID = "null";
                hide();
            } catch (Exception ex) {
                Log.e("Sign out da hata", ex.toString());
            }
        } else if (info == "facebook") {

            hide();
        } else {
            Log.e("Out", "Doğru değer alınamıyor");
        }
        mAnonimButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//Google için bağlantı
        super.onActivityResult(requestCode, resultCode, data);
        if (info.equals("google")) {
            try {
                Log.d("s", String.valueOf(requestCode));
                if (requestCode == requestCode_SIGN_IN) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        Log.e("Google Login", "Oturum Açılıyor..");
                        GoogleSignInAccount account = result.getSignInAccount();
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("Google Login", "Oturum Google Hesabı ile açıldı.");
                                        } else {
                                            Log.e("Google Login", "Oturum Açılamadı.", task.getException());
                                            Toast.makeText(MainActivity.this, "Bağlantısı Hatası.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        show();
                        mAnonimButton.setVisibility(View.INVISIBLE);
                    } else {
                        Log.e("Google Login", "Google hesabıyla oturum açma isteği yapılamadı." + result.getStatus());
                    }
                }
            } catch (Exception ex) {
                Log.e("ForResult", ex.toString());
            }
        } else if (info == "facebook") {
            show();
            mAnonimButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Google Login", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthListener);
        } catch (Exception ex) {
            Log.e("onStart", ex.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        } catch (Exception ex) {
            Log.e("onStop", ex.toString());
        }
    }

    public void anonim(View view) {
        try {
            info = "anonim";
            Intent i = new Intent(MainActivity.this, SelectionActivity.class);
            i.putExtra("kullaniciID", KullaniciID);
            i.putExtra("info", info);
            startActivity(i);
            Log.e("Geçiş", "SelectionActivity aktivity e geçildi.");
        } catch (Exception ex) {
            Log.e("Geçiş HATA !", "HATA-->" + ex.toString());
        }
    }
}