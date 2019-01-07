package alexey.com.facultativeapp.Model;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

    /*
    Модель токена, получемого от гитхаба
     */
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
