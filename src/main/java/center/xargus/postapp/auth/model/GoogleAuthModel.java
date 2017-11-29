package center.xargus.postapp.auth.model;

public class GoogleAuthModel {
    public String error;
    public String sub;
    public String name;
    public String email;
    public String picture;
    public String locale;
    public boolean email_verified;

    @Override
    public String toString() {
        return "GoogleAuthModel{" +
                "error='" + error + '\'' +
                ", sub='" + sub + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                ", locale='" + locale + '\'' +
                ", email_verified=" + email_verified +
                '}';
    }
}
