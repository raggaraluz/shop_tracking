package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.Device;
import com.example.demo.model.Sim;
import com.example.demo.service.DeviceRepository;
import com.example.demo.service.SimRepository;

/**
 * This is an example of integration test over Device Controller.
 * 
 * The database is implemented using H2 in-memory DB, instead of the existing
 * one in the real application
 */
@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerIntegrationTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private DeviceRepository devices;

	@Autowired
	private SimRepository sims;

	/**
	 * Clear the repositories
	 */
	@BeforeEach
	final void clearDb() {
		devices.deleteAllInBatch();
		sims.deleteAllInBatch();
	}

	/**
	 * Test waiting-activation endpoint with no data
	 * @throws Exception
	 */
	@Test
	final void testFindWaitingActivationNone() throws Exception {
		mvc.perform(get("/api/device/waiting-activation").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", empty()));
	}
	
	/**
	 * Test waiting-activation endpoint with a single value
	 * 
	 * Three devices are to be added, only one will be for waiting activation
	 * @throws Exception
	 */
	@Test
	final void testFindWaitingActivationSingle() throws Exception {
		Sim sim1 = new Sim(1332, 123, "Spain", "Waiting for activation");
		Sim sim2 = new Sim(2134, 123, "Spain", "Active");
		sims.save(sim1);
		sims.save(sim2);
		
		devices.save(new Device("READY", 12.3f, sim1));
		devices.save(new Device("READY", 15.3f, sim2));
		devices.save(new Device(null, null, null));
		
		mvc.perform(get("/api/device/waiting-activation").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].sim.id", is(1332)))
			.andExpect(jsonPath("$[0].status", is("READY")))
			.andExpect(jsonPath("$[0].temperature", is(12.3)));
	}

	/**
	 * Test deleting a device that is in the repository
	 * @throws Exception
	 */
	@Test
	final void testDeleteDeviceFound() throws Exception {
		devices.save(new Device("READY", 32.5f, null));
		Device toRemove = devices.save(new Device("READY", 12.3f, null));
		devices.save(new Device(null, 52.5f, null));
		
		// It should return OK
		mvc.perform(delete("/api/device/" + toRemove.getId()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").doesNotExist());
		
		// Expect the device is removed
		assertFalse(devices.findById(toRemove.getId()).isPresent());
	}

	
	/**
	 * Test deleting a device that is not in the repository
	 * @throws Exception
	 */
	@Test
	final void testDeleteDeviceNotFound() throws Exception {
		devices.save(new Device("READY", 32.5f, null));
		devices.save(new Device("READY", 12.3f, null));
		devices.save(new Device(null, 52.5f, null));
		
		// It should return NOT FOUND
		mvc.perform(delete("/api/device/100000").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$").doesNotExist());
		
		// Expect the device is not removed
		assertEquals(devices.count(), 3);
	}

	/**
	 * Test modify a device that is in the repository
	 * @throws Exception
	 */
	@Test
	final void testModifyDeviceFound() throws Exception {
		Sim sim1 = new Sim(1332, 123, "Spain", "Waiting for activation");
		sims.save(sim1);
		
		devices.save(new Device("READY", 32.5f, null));
		Device toModify = devices.save(new Device(null, 12.3f, null));
		devices.save(new Device(null, 52.5f, null));
		
		String newValue = String
				.format("{\"id\": %d, \"status\": \"READY\", \"temperature\": 23.4, \"sim\": {\"id\": 1332}}", 
						toModify.getId());
		
		// It should return OK
		mvc.perform(put("/api/device/" + toModify.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(newValue))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sim.id", is(1332)))
			.andExpect(jsonPath("$.status", is("READY")))
			.andExpect(jsonPath("$.temperature", is(23.4)))
			.andExpect(jsonPath("$.id", is((int)toModify.getId())));
		
		// Expect the device is not added
		assertEquals(devices.count(), 3);
	}

	
	/**
	 * Test modifying a device that is not in the repository
	 * @throws Exception
	 */
	@Test
	final void testModifyDeviceNotFound() throws Exception {
		devices.save(new Device("READY", 32.5f, null));
		devices.save(new Device("READY", 12.3f, null));
		devices.save(new Device(null, 52.5f, null));

		String newValue = "{\"id\": 1000000, \"status\": \"READY\", \"temperature\": 23.4, \"sim\": null}";

		// It should return NOT FOUND
		mvc.perform(put("/api/device/100000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(newValue))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$").doesNotExist());
		
		// Expect the device is not added
		assertEquals(devices.count(), 3);
	}
	
	/**
	 * Test available-for-sale endpoint with no data
	 * @throws Exception
	 */
	@Test
	final void testFindAvailableForSaleNone() throws Exception {
		mvc.perform(get("/api/device/available-for-sale").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", empty()));
	}
	
	/**
	 * Test available-for-sale endpoint with two values
	 * 
	 * Three devices are to be added, only two will be available for sale.
	 * They should be returned in the proper order (by sim.id)
	 * @throws Exception
	 */
	@Test
	final void testFindAvailableForSaleTwo() throws Exception {
		Sim sim1 = new Sim(6332, 123, "Spain", "Waiting for activation");
		Sim sim2 = new Sim(2134, 123, "Spain", "Active");
		sims.save(sim1);
		sims.save(sim2);
		
		devices.save(new Device("READY", 12.3f, sim1));
		devices.save(new Device("READY", 15.3f, sim2));
		devices.save(new Device(null, null, null));
		
		mvc.perform(get("/api/device/available-for-sale").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].sim.id", is(2134)))
			.andExpect(jsonPath("$[1].sim.id", is(6332)));
	}
}
