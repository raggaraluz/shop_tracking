package com.example.demo.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

/**
 * Unit testing Device model
 */
class DeviceTest {

	/**
	 * Checks that "toString" have the proper information
	 */
	@Test
	final void testToString() {
		Sim sim = new Sim(1234, 8876, "UK", "Active");
		Device device = new Device("READY", 45f, sim);

		String str = device.toString();

		assertThat(str, containsString("READY"));
		assertThat(str, containsString("45"));
		assertThat(str, containsString("1234"));
		assertThat(str, containsString("8876"));
		assertThat(str, containsString("UK"));
		assertThat(str, containsString("Active"));
	}

}
