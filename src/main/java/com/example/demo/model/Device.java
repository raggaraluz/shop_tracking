package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * This entity represents an IoT device in a shop warehouse. A configured device
 * will have a status &quot;READY&quot;, an ideal temperature between (-25&#39;C
 * to 85&#39;C), and a SIM device assigned
 */
@Entity
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String status;

	private Float temperature;

	/**
	 * SIM is stored in a different entity. Join is needed here.
	 */
	@OneToOne(optional = true)
	@JoinColumn(name = "sim_id", unique = true, nullable = true, updatable = true)
	private Sim sim;

	/**
	 * Private default constructor
	 */
	@SuppressWarnings("unused")
	private Device() {
	}

	/**
	 * Device constructor
	 * 
	 * @param status      Status - READY or null
	 * @param temperature - Ideal temperature
	 * @param sim         - Assigned SIM
	 */
	public Device(String status, Float temperature, Sim sim) {
		this.status = status;
		this.temperature = temperature;
		this.sim = sim;
	}

	@Override
	public String toString() {
		return String.format("Device(id=%d, status=%s, temperature=%f, sim=%s)", id, status, temperature, sim);
	}

	/**
	 * @return The device ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return The device SIM
	 */
	public Sim getSim() {
		return sim;
	}

	/**
	 * @return The device Status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return The device ideal temperature
	 */
	public Float getTemperature() {
		return temperature;
	}
}
