package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This entity represent a SIM (Subscriber Identification Module) card.
 * 
 * The SIM card holds information such as:
 * <ul>
 * <li>SIM ID - uniquely identify the SIM card.
 * <li>Operator code – uniquely identify a mobile operator
 * <li>Country – country name, e.g. Italy
 * <li>Status – devices status can be Active, Waiting for activation, Blocked or
 * Deactivated.
 */
@Entity
@Table(indexes = @Index(columnList = "status"))
public class Sim {

	@Id
	private long id;

	private int operatorCode;
	private String country;

	private String status;

	/**
	 * The SIM may be linked to a device, using this JOIN relationship
	 */
	@OneToOne(optional = true, mappedBy = "sim")
	private Device device;

	/**
	 * Private constructor
	 */
	@SuppressWarnings("unused")
	private Sim() {
	}

	/**
	 * Creates a SIM object This entity represent a SIM (Subscriber Identification
	 * Module) card.
	 * 
	 * @param id           SIM ID - uniquely identify the SIM card.
	 * @param operatorCode Uniquely identify a mobile operator
	 * @param country      Country name, e.g. Italy
	 * @param status       Devices status can be Active, Waiting for activation,
	 *                     Blocked or
	 */
	public Sim(long id, int operatorCode, String country, String status) {
		this.id = id;
		this.operatorCode = operatorCode;
		this.country = country;
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("Sim(id=%d, operatorCode=%d, country=%s, status=%s)", id, operatorCode, country, status);
	}

	/**
	 * @return The SIM ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return The operator code
	 */
	public int getOperatorCode() {
		return operatorCode;
	}

	/**
	 * @return The country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return The SIM status
	 */
	public String getStatus() {
		return status;
	}
}
