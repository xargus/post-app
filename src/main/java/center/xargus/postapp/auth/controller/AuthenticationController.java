package center.xargus.postapp.auth.controller;


import java.sql.SQLException;

import center.xargus.postapp.auth.model.UserInfoModel;
import center.xargus.postapp.auth.type.AuthType;
import center.xargus.postapp.constants.SessionConfig;
import center.xargus.postapp.model.ApiResultModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.auth.dao.AuthenticationDao;
import center.xargus.postapp.auth.model.RegisterResultModel;
import center.xargus.postapp.utils.TextUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@Controller
public class AuthenticationController {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private AuthenticationDao authDao;

	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value = "/api/auth/register", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String register(@RequestParam(value="userId") String userId,
						   @RequestParam(value="oauthPlatform") String oauthPlatform,
						   @RequestParam(value= "accessToken") String accessToken) {
		if (TextUtils.isEmpty(userId) ||
				TextUtils.isEmpty(oauthPlatform) || AuthType.getType(oauthPlatform.toUpperCase()) == null ||
				TextUtils.isEmpty(accessToken)) {
			ApiResultModel model = new ApiResultModel();
			model.setResult(ResultConfig.INVALID_PARAMETER);
			return new Gson().toJson(model);
		}
		
		ApiResultModel model;
		AuthType authType = AuthType.getType(oauthPlatform.toUpperCase());
		if (authType.validateAccessToken(accessToken, restTemplate)) {
			model = insert(userId, oauthPlatform, accessToken);
		} else {
			model = new RegisterResultModel();
			model.setResult(ResultConfig.INVALID_TOKEN);
		}

		return new Gson().toJson(model);
	}

	@RequestMapping(value = "/api/auth/login", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String login(@RequestParam(value="userId") String userId,
						@RequestParam(value="oauthPlatform") String oauthPlatform,
						@RequestParam(value= "accessToken") String accessToken,
						HttpSession session) {
		if (TextUtils.isEmpty(userId) ||
				TextUtils.isEmpty(oauthPlatform) || AuthType.getType(oauthPlatform.toUpperCase()) == null ||
				TextUtils.isEmpty(accessToken)) {
			ApiResultModel model = new ApiResultModel();
			model.setResult(ResultConfig.INVALID_PARAMETER);
			return new Gson().toJson(model);
		}

		ApiResultModel model;
		AuthType authType = AuthType.getType(oauthPlatform.toUpperCase());
		if (authType.validateAccessToken(accessToken, restTemplate)) {
			try {
				updateAccessTokenIfNecessary(userId, accessToken);
			} catch (EmptyResultDataAccessException e) {
				model = new ApiResultModel();
				model.setResult(ResultConfig.NOT_REGISTERED_USER);
				return new Gson().toJson(model);
			}

			makeSession(session, userId, oauthPlatform, accessToken);
			model = new RegisterResultModel();
			model.setResult(ResultConfig.SUCCESS);
		} else {
			model = new RegisterResultModel();
			model.setResult(ResultConfig.INVALID_TOKEN);
		}

		return new Gson().toJson(model);
	}
	
	private ApiResultModel insert(String userId, String oauthPlatform, String accessToken) {
		String result = ResultConfig.SUCCESS;
		try {
			authDao.insertUserInfo(userId, oauthPlatform, accessToken);
		} catch (DataIntegrityViolationException e) {
			result = ResultConfig.DUPLICATED_KEY;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			result = ResultConfig.UNKNOWN_ERROR;
		}

		log.info("register success, userId : " + userId + ", oauthPlatform : " + oauthPlatform + ", result : " + result);

		RegisterResultModel model = new RegisterResultModel();
		model.setResult(result);

		return model;
	}

	private void updateAccessTokenIfNecessary(String userId, String newAccessToken) throws EmptyResultDataAccessException {
		try {
			String accessToken = authDao.queryAccessToken(userId);
			if (!newAccessToken.equals(accessToken)) {
				authDao.updateAccessToken(userId, newAccessToken);
			}
		} catch (EmptyResultDataAccessException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makeSession(HttpSession session, String userId, String oauth, String accessToken) {
		UserInfoModel model = new UserInfoModel();
		model.setAccessToken(accessToken);
		model.setOauthPlatform(oauth);
		model.setUserId(userId);

		session.setAttribute(SessionConfig.LOGIN_USER_INFO_SESSION, model);
	}
}
