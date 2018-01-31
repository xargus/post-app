package center.xargus.postapp.auth.dao;

import center.xargus.postapp.auth.model.UserInfoModel;
import org.apache.ibatis.annotations.Param;

public interface AuthenticationDao {
    void insertUserInfo(@Param("user_id") String userId, @Param("oauth_platform") String oauthPlatform, @Param("access_token") String accessToken);
    String queryAccessToken(@Param("user_id") String userId);
    void updateAccessToken(@Param("user_id") String userId, @Param("access_token") String accessToken);
    UserInfoModel queryUserInfo(@Param("user_id") String userId);
}
