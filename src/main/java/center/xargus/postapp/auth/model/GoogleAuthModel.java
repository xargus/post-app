package center.xargus.postapp.auth.model;

public class GoogleAuthModel {
    public String error;
    public String aud;
    public String user_id;
    public String scope;
    public String expires_in;

    @Override
    public String toString() {
        return "GoogleAuthModel{" +
                "error='" + error + '\'' +
                ", aud='" + aud + '\'' +
                ", user_id='" + user_id + '\'' +
                ", scope='" + scope + '\'' +
                ", expires_in='" + expires_in + '\'' +
                '}';
    }
}
