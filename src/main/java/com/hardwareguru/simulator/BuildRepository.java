package com.hardwareguru.simulator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildRepository extends JpaRepository<BuildGuardada, String> {
    // Spring Data JPA provee los métodos básicos (save, findById, delete) por defecto.
    // Podemos agregar métodos personalizados si los necesitamos luego.
}