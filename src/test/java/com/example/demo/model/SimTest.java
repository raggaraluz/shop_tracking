package com.example.demo.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

/**
 * Unit testing SIM model
 */
class SimTest {

	/**
	 * Checks that "toString" have the proper information
	 */
	@Test
	final void testToString() {
		Sim sim = new Sim(52334, 234, "Italy", "Waiting for activation");

		String str = sim.toString();

		assertThat(str, containsString("52334"));
		assertThat(str, containsString("234"));
		assertThat(str, containsString("Italy"));
		assertThat(str, containsString("Waiting for activation"));
	}
}
