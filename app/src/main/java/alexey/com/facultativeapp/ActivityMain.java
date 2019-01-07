package alexey.com.facultativeapp;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ncapdevi.fragnav.FragNavController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alexey.com.facultativeapp.Model.User;
import alexey.com.facultativeapp.fragments.FragmentContacts;
import alexey.com.facultativeapp.fragments.FragmentInfo;
import alexey.com.facultativeapp.fragments.FragmentMap;
import alexey.com.facultativeapp.fragments.FragmentRepo;
import alexey.com.facultativeapp.fragments.FragmentSensor;
import alexey.com.facultativeapp.helpers.App;
import alexey.com.facultativeapp.sync.Api;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String LOG = "Logs";

    private FragNavController fragNavController;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*
        Установка View
        Настройка тулбара и навигации
         */
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView uName = navigationView.getHeaderView(0).findViewById(R.id.githubName);
        uName.setText(App.getUsername());

        /*
        Инициализация контроллера со списком фрагментов
         */
        fragNavController = new FragNavController(getSupportFragmentManager(), R.id.container);
        List<Fragment> rootFragments = new ArrayList<>();
        rootFragments.add(new FragmentRepo());
        rootFragments.add(new FragmentMap());
        rootFragments.add(new FragmentContacts());
        rootFragments.add(new FragmentInfo());
        rootFragments.add(new FragmentSensor());
        fragNavController.setRootFragments(rootFragments);
        fragNavController.setFragmentHideStrategy(FragNavController.HIDE);
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    Свитч для перехода между фрагментами
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_repo: fragNavController.switchTab(FragNavController.TAB1); break;
            case R.id.nav_map: fragNavController.switchTab(FragNavController.TAB2); break;
            case R.id.nav_contacts: fragNavController.switchTab(FragNavController.TAB3); break;
            case R.id.nav_info: fragNavController.switchTab(FragNavController.TAB4); break;
            case R.id.nav_sensor: fragNavController.switchTab(FragNavController.TAB5); break;
            case R.id.nav_logout: logOut(); break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Выход пользователя из приложения.
    У гита отсутствует данный метод.
    Простое удаление токена ни к чему не приводит.
    Помогает только сброс сессии.
     */

    private void logOut() {
        Intent intent = new Intent(this, ActivityAuth.class);
        startActivity(intent);

    }

    private void finishActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
