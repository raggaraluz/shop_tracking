package com.example.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.Device;
import com.example.demo.model.Sim;
import com.example.demo.service.DeviceRepository;
import com.example.demo.service.SimRepository;

/**
 * This will take care of database initialization, in case the command line includes this option.
 * 
 * Command line options
 * <ul>
 * <li>clear-db: It will delete all entries in the database
 * <li>init-db: It will generate a random database which may be used for testing 
 * </ul>
 */
@Component
public class DbInit implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(DbInit.class);

	/**
	 * Device repository
	 */
	@Autowired
	private DeviceRepository devices;

	/**
	 * Sim repository
	 */
	@Autowired
	private SimRepository sims;
	
	@Override
	public void run(String... a) {
		List<String> args = Arrays.asList(a);
		if (args.contains("clear-db")) {
			clearSampleDb();
		}
		if (args.contains("init-db")) {
			initSampleDb();
		}
	}

	/**
	 * Clear the database, both for device and SIMs
	 */
	private void clearSampleDb() {
		log.info("Clearing device DB");
		devices.deleteAllInBatch();
		log.info("Clearing sim DB");
		sims.deleteAllInBatch();
		log.info("All databased cleared");
	}

	/**
	 * Initializes the database with a randomly generated sims
	 * and devices. Approximately half of the devices are ready
	 * for sell.
	 */
	private void initSampleDb() {
		log.info("Generating SIMs");
		List<Sim> newSims = LongStream.range(10000000l, 10200000l).boxed().map(DbInit::createSim)
				.collect(Collectors.toList());
		Collections.shuffle(newSims);
		log.info("Storing SIMs");
		sims.saveAll(newSims);
		
		log.info("Generating Devices");
		Stream<Device> newDevs = IntStream.range(0, 200000).boxed().map(id -> createDevice(newSims));
		log.info("Storing Devices");
		devices.saveAll(newDevs::iterator);
		
		log.info("Database initialized");
	}

	/**
	 * Function to create a device, randomly assigning (or not) a SIM, and
	 * a temperature.
	 * 
	 * To avoid repetitions in the SIM, the full list of SIMs is passed as a
	 * parameter. If the SIM is assigned, it is being removed from the list.
	 * 
	 * @param sims List of available SIMs. This variable may be updated inside
	 * 	the function
	 * @return The generated device
	 */
	private Device createDevice(List<Sim> sims) {
		float readyProb = rand.nextFloat();
		String status = null;
		Sim sim = null;
		Float temperature = null;
		if (readyProb > 0.5) {
			status = "READY";
			if (!sims.isEmpty()) {
				sim = sims.remove(0);
			}
			temperature = -25 + (85 + 25) * rand.nextFloat();
		} 
		
		return new Device(status, temperature, sim);
	}

	
	/**
	 * It randomly generates a SIM object with certain SIM id.
	 * 
	 * Country value is taken from COUNTRIES array, with uniform probability.
	 * Operator code is assigned a random integer, so that different countries
	 * will have different operators. 
	 *  
	 * @param id The SIM id
	 * @return The generated SIM object
	 */
	private static Sim createSim(long id) {
		// Country
		int country = rand.nextInt(COUNTRIES.length);
		
		// Operator
		int operator = 10 * country + rand.nextInt(4);
		
		// Status
		String status = createStatus();
		
		return new Sim(id, operator, COUNTRIES[country], status);
	}
	
	
	/**
	 * Select an status with different probabilities
	 * 
	 * <ul>
	 * <li>Active ~ 60%
	 * <li>Waiting for activation ~ 30%
	 * <li>Deactivated ~ 9%
	 * <li>Blocked ~ 1%
	 * </ul>
	 * @return
	 */
	private static String createStatus() {
		float statusProb = rand.nextFloat();
		String status = "Active";
		if (statusProb > 0.99) {
			status = "Blocked";
		} else if (statusProb > 0.9) {
			status = "Deactivated";
		} else if (statusProb > 0.6) {
			status = "Waiting for activation";
		}
		
		return status;
	}

	
	/**
	 * List of available countries to be picked from.
	 */
	private static final String COUNTRIES[] = new String[] {"Spain", "Italy", "Germany", "UK"};
	
	
	/**
	 * Random number generator, to be shared for all methods in the class.
	 */
	private static final Random rand = new Random();
}
