package alexey.com.facultativeapp.sync;

import java.util.List;

import alexey.com.facultativeapp.Model.AccessToken;
import alexey.com.facultativeapp.Model.GitHubRepo;
import alexey.com.facultativeapp.Model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    /*
    Интерфейс для сетевых запросов
     */

    /*
    Получение accessToken
     */
    @Headers("Accept: application/json")
    @POST("/login/oauth/access_token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code);

    /*
    Получение пользователя по токену
     */
    @GET("/user")
    Call<User>getCurrentUser();

    /*
    Получение репозиториев пользователя
     */
    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>>getReposForUser(@Path("user") String user);

    /*
    Выход пользователя
     */
    @DELETE("/applications/{clientId}/tokens/{token}")
    Call<String>logOut(@Path("clientId") String clientId,
                       @Path("token") String token);

}
