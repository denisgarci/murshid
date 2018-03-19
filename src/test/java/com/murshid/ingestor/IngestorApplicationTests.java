package com.murshid.ingestor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
public class IngestorApplicationTests {

	@Test
	public void contextLoads() {
		assertTrue("hello", 1==1);
	}

}
