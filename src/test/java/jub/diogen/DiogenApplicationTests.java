package jub.diogen;

import jub.diogen.controller.SearchController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiogenApplicationTests {
	@Autowired
	private SearchController controller;

	@Test
	public void contextLoads() {
		Assertions.assertNotNull(controller);
	}
}
