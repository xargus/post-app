package center.xargus.postapp.auth.model;

public class UserInfoModel {
    private String user_id;
    private String oauth_platform;
    private String access_token;

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getOauthPlatform() {
        return oauth_platform;
    }

    public void setOauthPlatform(String oauthPlatform) {
        this.oauth_platform = oauthPlatform;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }
}
