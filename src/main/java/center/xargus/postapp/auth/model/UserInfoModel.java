package center.xargus.postapp.auth.model;

public class UserInfoModel {
    private String userId;
    private String oauthPlatform;
    private String accessToken;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOauthPlatform() {
        return oauthPlatform;
    }

    public void setOauthPlatform(String oauthPlatform) {
        this.oauthPlatform = oauthPlatform;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
