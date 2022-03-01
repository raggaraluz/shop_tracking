package com.example.demo.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Sim;

/**
 * This is the repository to store the SIM registry
 */
public interface SimRepository extends JpaRepository<Sim, Long> {
}