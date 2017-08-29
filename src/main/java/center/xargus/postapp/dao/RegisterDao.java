package center.xargus.postapp.dao;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Repository;

@Repository
public class RegisterDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	@PostConstruct
    public void initialize(){
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("register.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
	
	public int insertUserInfo(String userId, String oauthPlatform) throws SQLException {
		String quey = "insert into USER_INFO (user_id, oauth_platform) values(?,?)";
		return jdbcTemplate.update(quey, userId, oauthPlatform);
	}
}
