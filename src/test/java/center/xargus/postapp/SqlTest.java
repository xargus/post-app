package center.xargus.postapp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import center.xargus.postapp.auth.dao.AuthenticationDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-config.xml")
public class SqlTest {
	
	@Autowired
	private AuthenticationDao registerDao;
	
	@Test
	public void test() {
		assertTrue(true);
	}

}
