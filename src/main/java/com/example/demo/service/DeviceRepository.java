package com.example.demo.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Device;

/**
 * This is the repository to store the Device registry
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

	List<Device> findBySimStatus(String status);

	/**
	 * Gets the list of devices available for sale.
	 * 
	 * The shop can sell a device only if it meets the UK government&#39;s industry
	 * standard. A configured device will have a status &quot;READY&quot;, an ideal
	 * temperature between (-25&#39;C to 85&#39;C) and a configured SIM.
	 * 
	 * @return
	 */
	@Query("SELECT d FROM Device d WHERE d.sim IS NOT NULL AND "
			+ " d.status = 'READY' AND d.temperature BETWEEN -25 and 85 " + " ORDER by d.sim.id")
	List<Device> findForSale();
}
