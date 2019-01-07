package alexey.com.facultativeapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import alexey.com.facultativeapp.helpers.App;

public class SplashActivity extends AppCompatActivity {

    /*
    Класс для проверки наличия accessToken.
    Если он есть, то переходим на главную активити (ActivityMain)
    Если его нет, то переходим на активити авторизации (ActivityAuth)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (App.getAccessToken() != null) {
            Intent intent = new Intent(this, ActivityMain.class);
            startActivity(intent);
            finishAffinity();
        } else {
            Intent intent = new Intent(this, ActivityAuth.class);
            startActivity(intent);
            finishAffinity();
        }
    }
}