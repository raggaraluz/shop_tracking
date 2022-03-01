package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Device;
import com.example.demo.service.DeviceRepository;

/**
 * REST API for the device interaction
 */
@RestController
@RequestMapping(path = "/api/device", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {

	@Autowired
	private DeviceRepository repository;

	/**
	 * Returns all devices in the warehouse that are waiting for activation.
	 * 
	 * @return The list of devices
	 */
	@GetMapping("/waiting-activation")
	public List<Device> findWaitingActivation() {
		return IterableUtils.toList(repository.findBySimStatus("Waiting for activation"));
	}

	/**
	 * Management endpoint to remove a device
	 * 
	 * @param id The device ID
	 * @return 200 OK or 404 NOT FOUND
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteDevice(@PathVariable long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Management endpoint to update a device
	 * 
	 * @param id The device ID
	 * @param    new device data to update
	 * @return 200 UPDATED or 404 NOT FOUND
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Device> modifyDevice(@PathVariable long id, @RequestBody Device device) {
		Optional<Device> stored = repository.findById(id);
		if (stored.isPresent()) {
			return new ResponseEntity<>(repository.save(device), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Gets an ordered result of devices available for sale. They are ordered by SIM
	 * ID.
	 * 
	 * @return The list of devices
	 */
	@GetMapping("/available-for-sale")
	public List<Device> findAvailableForSale() {
		return IterableUtils.toList(repository.findForSale());
	}
}
