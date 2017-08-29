package center.xargus.postapp.controller;


import java.sql.SQLException;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import center.xargus.postapp.constants.ResultConfig;
import center.xargus.postapp.dao.RegisterDao;
import center.xargus.postapp.model.RegisterResultModel;
import center.xargus.postapp.utils.TextUtils;

@Controller
public class RegisterController {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private RegisterDao registerdao;
	
	@RequestMapping(value = "/api/register", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String register(@RequestParam(value="userId") String userId, @RequestParam(value="oauthPlatform") String oauthPlatform) {
		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(oauthPlatform)) {
			RegisterResultModel model = new RegisterResultModel();
			model.setResult(ResultConfig.INVALID_PARAMETER);
			return new Gson().toJson(model);
		}
		
		String result = ResultConfig.SUCCESS;
		try {
			registerdao.insertUserInfo(userId, oauthPlatform);
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
		return new Gson().toJson(model);
	}
}
