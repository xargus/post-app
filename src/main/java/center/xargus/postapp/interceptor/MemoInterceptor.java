package center.xargus.postapp.interceptor;

import center.xargus.postapp.auth.dao.AuthenticationDao;
import center.xargus.postapp.auth.model.UserInfoModel;
import center.xargus.postapp.auth.type.AuthType;
import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemoInterceptor extends HandlerInterceptorAdapter{
    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getParameter("userId");
        String accessToken = request.getParameter("accessToken");

        log.info("userId : " + userId + ", accessToken : " + accessToken);
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(accessToken)) {
            ApiResultModel model = new ApiResultModel();
            model.setResult(ResultConfig.INVALID_PARAMETER);
            response.getWriter().write(new Gson().toJson(model));
            return false;
        }

        UserInfoModel userInfoModel = authDao.queryUserInfo(userId);
        if (userInfoModel == null) {
            ApiResultModel model = new ApiResultModel();
            model.setResult(ResultConfig.WRONG_APPROACH);
            response.getWriter().write(new Gson().toJson(model));
            return false;
        }

        if (!userInfoModel.getAccessToken().equals(accessToken)) {
            if (AuthType.getType(userInfoModel.getOauthPlatform().toUpperCase()).validateAccessToken(userId, accessToken, restTemplate)) {
                authDao.updateAccessToken(userId, accessToken);
            } else {
                ApiResultModel model = new ApiResultModel();
                model.setResult(ResultConfig.WRONG_APPROACH);
                response.getWriter().write(new Gson().toJson(model));
                return false;
            }
        }

        return true;
    }
}
