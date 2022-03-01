package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.service.DeviceRepository;
import com.example.demo.service.SimRepository;

@SpringBootTest
class DbInitTest {

	@MockBean
	private DeviceRepository deviceRepo;

	@MockBean
	private SimRepository simRepo;

	@Autowired
	private DbInit dbInit;

	/**
	 * Mocks to avoid writing to DB
	 */
	@BeforeEach
	public void before() {
		when(deviceRepo.saveAll(any())).then(i -> {
			Iterable devices = i.getArgument(0);
			
			// Check there are some devices
			assertTrue(IterableUtils.size(devices) > 0);
			return null;
		});
		when(simRepo.saveAll(any())).then(i -> {
			Iterable sims = i.getArgument(0);
			
			// Check there are some sims
			assertTrue(IterableUtils.size(sims) > 0);
			return null;
		});
	}

	/**
	 * Call run clear-db
	 */
	@Test
	final void testDeleteDb() {
		dbInit.run("clear-db");

		// Check the delete method was called once
		verify(deviceRepo, times(1)).deleteAllInBatch();
		verify(simRepo, times(1)).deleteAllInBatch();

		// Check the saveAll method was not called
		verify(deviceRepo, times(0)).saveAll(any());
		verify(simRepo, times(0)).saveAll(any());
	}

	/**
	 * Call run init-db
	 */
	@Test
	final void testInitDb() {
		dbInit.run("init-db");

		// Check the delete method was not called
		verify(deviceRepo, times(0)).deleteAllInBatch();
		verify(simRepo, times(0)).deleteAllInBatch();

		// Check the saveAll method was called once
		verify(deviceRepo, times(1)).saveAll(any());
		verify(simRepo, times(1)).saveAll(any());
	}

	/**
	 * Call run clear-db and init-db
	 */
	@Test
	final void testClearInitDb() {
		dbInit.run("init-db", "clear-db");

		// Check the delete method was called once
		verify(deviceRepo, times(1)).deleteAllInBatch();
		verify(simRepo, times(1)).deleteAllInBatch();

		// Check the saveAll method was called once
		verify(deviceRepo, times(1)).saveAll(any());
		verify(simRepo, times(1)).saveAll(any());
	}

	/**
	 * Call run without arguments
	 */
	@Test
	final void testNoArg() {
		dbInit.run();

		// Check the delete method was not called
		verify(deviceRepo, times(0)).deleteAllInBatch();
		verify(simRepo, times(0)).deleteAllInBatch();

		// Check the saveAll method was not called
		verify(deviceRepo, times(0)).saveAll(any());
		verify(simRepo, times(0)).saveAll(any());
	}

}
