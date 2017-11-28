package center.xargus.postapp.auth.dao;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import center.xargus.postapp.auth.model.UserInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	@PostConstruct
    public void initialize(){
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("user_info.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
	
	public int insertUserInfo(String userId, String oauthPlatform, String accessToken) throws SQLException {
		String query = "insert into USER_INFO (user_id, oauth_platform, access_token) values(?,?,?)";
		return jdbcTemplate.update(query, userId, oauthPlatform, accessToken);
	}

	public String queryAccessToken(String userId) {
    	String query = "select access_token from USER_INFO where user_id = '" + userId + "'";
    	return jdbcTemplate.queryForObject(query, String.class);
	}

	public int updateAccessToken(String userId, String accessToken) {
    	String query = "update USER_INFO set access_token = ? where user_id = ?";
    	return jdbcTemplate.update(query, accessToken, userId);
	}

	public UserInfoModel queryUserInfo(String userId) {
		String query = "select * from USER_INFO where user_id = '" + userId + "'";
		return (UserInfoModel) jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper(UserInfoModel.class));
	}
}
