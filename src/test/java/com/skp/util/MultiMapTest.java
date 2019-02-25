package com.skp.util;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiMapTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void testMultiMap() throws IOException {
		// create a Multimap from past US presidents list
		MultiMap<String, String> multimap = new MultiMap();

		multimap.put("Zachary", "Taylor");
		multimap.put("John", "Adams");
		multimap.put("John", "Tyler");
		multimap.put("John", "Kennedy");
		multimap.put("George", "Washington");
		multimap.put("George", "Bush");
		multimap.put("Grover", "Cleveland");

		System.out.println("--------Printing Multimap using keySet ----------\n");
		for (String lastName : multimap.keySet()) {
			System.out.println(lastName + ": " + multimap.get(lastName));
		}
	}

}
