package com.hardwareguru.simulator.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulaciones")
public class Simulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; 

    private int cpuTdpWatts;
    private int gpuTdpWatts;
    private int psuWatts;
    private String resolucion;
    
    private int consumoTotalEstimado;
    private int cuelloBotellaPorcentaje;
    private String estadoRiesgo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getCpuTdpWatts() { return cpuTdpWatts; }
    public void setCpuTdpWatts(int cpuTdpWatts) { this.cpuTdpWatts = cpuTdpWatts; }

    public int getGpuTdpWatts() { return gpuTdpWatts; }
    public void setGpuTdpWatts(int gpuTdpWatts) { this.gpuTdpWatts = gpuTdpWatts; }

    public int getPsuWatts() { return psuWatts; }
    public void setPsuWatts(int psuWatts) { this.psuWatts = psuWatts; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public int getConsumoTotalEstimado() { return consumoTotalEstimado; }
    public void setConsumoTotalEstimado(int consumoTotalEstimado) { this.consumoTotalEstimado = consumoTotalEstimado; }

    public int getCuelloBotellaPorcentaje() { return cuelloBotellaPorcentaje; }
    public void setCuelloBotellaPorcentaje(int cuelloBotellaPorcentaje) { this.cuelloBotellaPorcentaje = cuelloBotellaPorcentaje; }

    public String getEstadoRiesgo() { return estadoRiesgo; }
    public void setEstadoRiesgo(String estadoRiesgo) { this.estadoRiesgo = estadoRiesgo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}