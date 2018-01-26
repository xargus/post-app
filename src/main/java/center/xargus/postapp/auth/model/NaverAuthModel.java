package center.xargus.postapp.auth.model;

public class NaverAuthModel {
    public String resultcode;
    public String message;
    public Response response;

    public static class Response {
        public String email;
        public String id;
        public String name;

        @Override
        public String toString() {
            return "Response{" +
                    "email='" + email + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NaverAuthModel{" +
                "resultcode='" + resultcode + '\'' +
                ", message='" + message + '\'' +
                ", response=" + response +
                '}';
    }
}
