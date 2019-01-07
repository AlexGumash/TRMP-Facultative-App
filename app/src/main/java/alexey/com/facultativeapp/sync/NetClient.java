package alexey.com.facultativeapp.sync;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import alexey.com.facultativeapp.Model.AccessToken;
import alexey.com.facultativeapp.Model.GitHubRepo;
import alexey.com.facultativeapp.Model.User;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetClient {

    /*
    В этом классе определены методы для
    осуществления сетевых запросов
     */

    private String LOG_TAG = "NetClient URL";
    private Api api;

    public NetClient(String baseUrl, final String accessToken) {
        /*
        Создание http-клиента
         */
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                okhttp3.Request.Builder builder = chain.request().newBuilder();
                if (accessToken != null) {
                    builder.addHeader("Authorization", "token " + accessToken);
                }
                return chain.proceed(builder.build());
            }
        }).readTimeout(60, TimeUnit.SECONDS).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        api = retrofit.create(Api.class);
    }

    /*
    Методы, реализующие различные запросы
    Получение accessToken
    Получение списка репозиториев
    Получение пользователя по токену
    Выход пользователя из приложения
     */
    public void getAccessToken(String clientId, String clientSecret, String code, Callback<AccessToken> callback) {
        Log.d(LOG_TAG, api.getAccessToken(clientId, clientSecret, code).request().url().toString());
        api.getAccessToken(clientId, clientSecret, code).enqueue(callback);
    }

    public void getRepos(String userName, Callback<List<GitHubRepo>> callback) {
        Log.d(LOG_TAG, api.getReposForUser(userName).request().url().toString());
        api.getReposForUser(userName).enqueue(callback);
    }

    public void getCurrentUser(Callback<User> callback) {
        Log.d(LOG_TAG, api.getCurrentUser().request().url().toString());
        api.getCurrentUser().enqueue(callback);
    }

    public void logOut(String clientId, String token, Callback<String> callback) {
        Log.d(LOG_TAG, api.logOut(clientId, token).request().url().toString());
        api.logOut(clientId, token).enqueue(callback);
    }
}
