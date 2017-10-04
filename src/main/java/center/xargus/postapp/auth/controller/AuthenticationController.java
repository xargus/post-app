package center.xargus.postapp.auth.controller;


import java.sql.SQLException;

import center.xargus.postapp.auth.type.AuthType;
import center.xargus.postapp.model.ApiResultModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
			RegisterResultModel model = new RegisterResultModel();
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
					  @RequestParam(value= "accessToken") String accessToken) {
		if (TextUtils.isEmpty(userId) ||
				TextUtils.isEmpty(oauthPlatform) || AuthType.getType(oauthPlatform.toUpperCase()) == null ||
				TextUtils.isEmpty(accessToken)) {
			RegisterResultModel model = new RegisterResultModel();
			model.setResult(ResultConfig.INVALID_PARAMETER);
			return new Gson().toJson(model);
		}

		ApiResultModel model;
		AuthType authType = AuthType.getType(oauthPlatform.toUpperCase());
		if (authType.validateAccessToken(accessToken, restTemplate)) {
			updateAccessTokenIfNecessary(userId, accessToken);
			makeSession();

			model = new RegisterResultModel();
			model.setResult(ResultConfig.SUCCESS);
		} else {
			model = new RegisterResultModel();
			model.setResult(ResultConfig.NOT_REGISTERED_USER);
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

	private void updateAccessTokenIfNecessary(String userId, String newAccessToken) {
		try {
			String accessToken = authDao.queryAccessToken(userId);
			if (!newAccessToken.equals(accessToken)) {
				authDao.updateAccessToken(userId, newAccessToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makeSession() {

	}
}
