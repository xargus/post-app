package center.xargus.postapp;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-config.xml")
public class LoggerTest {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Test
	public void test() {
		log.info("test");
	}
}
