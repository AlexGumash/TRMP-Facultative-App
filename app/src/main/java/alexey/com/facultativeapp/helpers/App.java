package alexey.com.facultativeapp.helpers;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.transport.TransportFactory;

import alexey.com.facultativeapp.R;
import alexey.com.facultativeapp.sync.NetClient;

/*
Главный класс
 */

public class App extends Application {

    /*
    Список переменных, принадлежащих приложению на гитхабе
     */
    private static final String APP_ID = "961773";
    private static final String AUTH_URL = "https://github.com/";
    private static final String BASE_URL = "https://api.github.com/";
    private static final String clientId = "cbd21af15d229c70275a";
    private static final String clientSecret = "35e0e737fffebfce6a0f4bc38042af283c357388";
    private static final String redirectUri = "alexey.com.facultativeapp://callback"; //callback для окончания авторизации

    private static String USERNAME;
    private static NetClient netClient;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        Инициализация карт по Api Key
         */
        MapKitFactory.setApiKey("701548a4-ae64-4f6a-a6a5-5ba41e0b5a08");
        MapKitFactory.initialize(this);
        TransportFactory.initialize(this);

        /*
        Проверка был ли пользователь авторизован
         */
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.prefs_name), MODE_PRIVATE);
        USERNAME = sharedPreferences.getString(String.valueOf(R.string.username), null);
        if (sharedPreferences.getString(String.valueOf(R.string.token), null) != null) {
            setBaseNetClient();
        } else {
            setAuthNetClient();
        }

    }

    public static NetClient getNetClient() {
        return netClient;
    }

    //Мы будем использовать этот нетклиент для авторизации
    public static void setAuthNetClient() {
        netClient = new NetClient(AUTH_URL, null);
    }

    //А этот нетклиент для отправки запрос к апи
    public static void setBaseNetClient() {
        netClient = new NetClient(BASE_URL, getAccessToken());
    }

    /*
    Методы для хранения данных приложения.
    Сохранение токена
    Получение токена
    Очистка токена
    Очистка имени пользователя
    Установка имени пользователя
     */
    public static void setAccessToken(String token) {
        sharedPreferences.edit().putString(String.valueOf(R.string.token), token).apply();
    }

    public static String getAccessToken() {
        return sharedPreferences.getString(String.valueOf(R.string.token), null);
    }

    public static void setUsername(String username) {
        USERNAME = username;
        sharedPreferences.edit().putString(String.valueOf(R.string.username), username).apply();
    }

    /*
    Методы получения различных данных
     */
    public static String getClientId() {
        return clientId;
    }

    public static String getClientSecret() {
        return clientSecret;
    }

    public static String getRedirectUri() {
        return redirectUri;
    }

    public static String getUsername() {
        return USERNAME;
    }

}
