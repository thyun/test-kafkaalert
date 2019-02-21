package com.skp.kafkaalert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonTest {
	private static final Logger logger = LoggerFactory.getLogger(CommonTest.class);

	@Before
	public void setUp() {
	}

	@Test
	public void testDate() throws ParseException {
		String v = "31/Jul/2018:09:00:00";
		String pattern = "dd/MMM/yyyy:HH:mm:ss";
//		String v = "31/Jul/2018:09:00:00 +0900";
//		String pattern = "dd/MMM/yyyy:HH:mm:ss Z";
		SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.ENGLISH);

		Date timestamp = fmt.parse(v);
		logger.debug("timestamp=" + timestamp);
	}
}
