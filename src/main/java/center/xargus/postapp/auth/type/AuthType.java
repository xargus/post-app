package center.xargus.postapp.auth.type;

import center.xargus.postapp.auth.model.GoogleAuthModel;
import center.xargus.postapp.auth.model.NaverAuthModel;
import center.xargus.postapp.utils.TextUtils;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public enum AuthType {
    GOOGLE {
        @Override
        public boolean validateAccessToken(String userId, String accessToken, RestTemplate restTemplate) {
            //https://developers.google.com/identity/protocols/OAuth2UserAgent
            GoogleAuthModel model = new GoogleAuthModel();
            try {
                model = restTemplate.getForObject(GOOGLE_USER_INFO_URL + "?access_token=" + accessToken, GoogleAuthModel.class);
            } catch (Exception e) {
                e.printStackTrace();
                model.error = e.getMessage();
            }

            log.info(model.toString());
            if (TextUtils.isEmpty(model.error) && !TextUtils.isEmpty(model.sub) && model.sub.equals(userId)) {
                return true;
            } else {
                return false;
            }
        }
    },
    NAVER {
        @Override
        public boolean validateAccessToken(String userId, String accessToken, RestTemplate restTemplate) {
            try {
                String token = accessToken;
                String header = "Bearer " + token;

                URL url = new URL(NAVER_USER_INFO_URL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                if(responseCode==200) {
                    Reader reader = new InputStreamReader(con.getInputStream(), "UTF-8");
                    NaverAuthModel result  = new Gson().fromJson(reader, NaverAuthModel.class);
                    log.info(result.toString());
                    if ("00".equals(result.resultcode) && result.response.id.equals(userId)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    public abstract boolean validateAccessToken(String userId, String accessToken, RestTemplate restTemplate);

    public Logger log = Logger.getLogger(this.getClass());
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

    public static AuthType getType(String type) {
        AuthType authType = null;
        try {
            authType = AuthType.valueOf(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return authType;
    }
}
