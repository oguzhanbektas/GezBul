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
    private TextView mTextViewAd;
    private Button mSignInButtonGoogle, mSignOutButtonGoogle, mSignInButtonFacebook, mSignOutButtonFacebook;
    private String KullaniciID = "null";
    private ImageView mImageView;

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
                        mTextViewAd.setText("Hoşgeldiniz " + user.getDisplayName());
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
        mSignOutButtonGoogle = (Button) findViewById(R.id.signOut_button);
        mSignInButtonFacebook = (Button) findViewById(R.id.signIn_button_facebook);
        mSignOutButtonFacebook = (Button) findViewById(R.id.signOut_button_facebook);
        mImageView = (ImageView) findViewById(R.id.imageView);
    }

    public void gecis(View view) {//Google Map ekranına geçiş
        //KullaniciID yolla.
        try {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            i.putExtra("veri", KullaniciID);
            startActivity(i);
            Log.e("Geçiş", "Maps aktivity e geçildi.");
        } catch (Exception ex) {
            Log.e("Geçiş HATA !", "HATA-->" + ex.toString());
        }

    }

    public void signInFacebook(View view) {//Facebook ile giriş

    }

    public void signOutFacebook(View view) {//Facebook için çıkış

    }

    public void signIn(View view) {//google+ ile giriş
        try {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, requestCode_SIGN_IN);
        } catch (Exception ex) {
            Log.e("Sign in de hata", ex.toString());
        }
    }

    public void signOut(View view) {//google+ için çıkış
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
        } catch (Exception ex) {
            Log.e("Sign out da hata", ex.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//Google için bağlantı
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
                } else {
                    Log.e("Google Login", "Google hesabıyla oturum açma isteği yapılamadı.");
                }
            }
        } catch (Exception ex) {
            Log.e("ForResult", ex.toString());
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
}
