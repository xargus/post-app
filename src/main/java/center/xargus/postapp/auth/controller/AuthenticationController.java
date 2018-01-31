package center.xargus.postapp.auth.controller;


import center.xargus.postapp.auth.dao.AuthenticationDao;
import center.xargus.postapp.auth.model.RegisterResultModel;
import center.xargus.postapp.auth.type.AuthType;
import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.model.ApiResultModel;
import center.xargus.postapp.utils.TextUtils;
import com.google.gson.Gson;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@CrossOrigin(origins = {"http://localhost:3000", "https://post.xargus.center"})
@Controller
public class AuthenticationController {
	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private SqlSession sqlSession;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void initialize(){
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("user_info.sql"));
		DatabasePopulatorUtils.execute(populator, dataSource);
	}
	
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
		if (authType.validateAccessToken(userId, accessToken, restTemplate)) {
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
			ApiResultModel model = new ApiResultModel();
			model.setResult(ResultConfig.INVALID_PARAMETER);
			return new Gson().toJson(model);
		}

		ApiResultModel model;
		AuthType authType = AuthType.getType(oauthPlatform.toUpperCase());
		if (authType.validateAccessToken(userId, accessToken, restTemplate)) {
			try {
				updateAccessTokenIfNecessary(userId, accessToken);
			} catch (EmptyResultDataAccessException e) {
				model = new ApiResultModel();
				model.setResult(ResultConfig.NOT_REGISTERED_USER);
				return new Gson().toJson(model);
			}

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
			AuthenticationDao authenticationDao = sqlSession.getMapper(AuthenticationDao.class);
			authenticationDao.insertUserInfo(userId, oauthPlatform, accessToken);
		} catch (DataIntegrityViolationException e) {
			result = ResultConfig.DUPLICATED_KEY;
		} catch (Exception e) {
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
			AuthenticationDao authenticationDao = sqlSession.getMapper(AuthenticationDao.class);
			String accessToken = authenticationDao.queryAccessToken(userId);
			if (!newAccessToken.equals(accessToken)) {
				authenticationDao.updateAccessToken(userId, newAccessToken);
			}
		} catch (EmptyResultDataAccessException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
