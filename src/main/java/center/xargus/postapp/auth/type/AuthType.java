package center.xargus.postapp.auth.type;

import center.xargus.postapp.auth.model.GoogleAuthModel;
import center.xargus.postapp.utils.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

public enum AuthType {
    GOOGLE {
        @Override
        public boolean validateAccessToken(String userId, String accessToken, RestTemplate restTemplate) {
            //https://developers.google.com/identity/protocols/OAuth2UserAgent
            GoogleAuthModel model = new GoogleAuthModel();
            try {
                model = restTemplate.getForObject(GOOGLE_TOKEN_INFO_URL + "?access_token=" + accessToken, GoogleAuthModel.class);
            } catch (Exception e) {
                e.printStackTrace();
                model.error = e.getMessage();
            }

            log.info(model.toString());
            if (TextUtils.isEmpty(model.error) && !TextUtils.isEmpty(model.aud) && model.user_id.equals(userId)) {
                return true;
            } else {
                return false;
            }
        }
    };

    public abstract boolean validateAccessToken(String userId, String accessToken, RestTemplate restTemplate);

    public Logger log = Logger.getLogger(this.getClass());
    private static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo";

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
