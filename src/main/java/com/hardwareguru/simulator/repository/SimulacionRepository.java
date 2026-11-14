package com.hardwareguru.simulator.repository;

import com.hardwareguru.simulator.model.Simulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulacionRepository extends JpaRepository<Simulacion, Long> {
    // Si más adelante quieres buscar el historial de un usuario específico:
    List<Simulacion> findByUserId(String userId);
}