package com.hardwareguru.simulator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "builds")
public class BuildGuardada {

    @Id
    private String id;
    private String userId;
    private String nombreBuild;
    private String cpuId;
    private String gpuId;
    private String motherboardId;
    private String ramId;
    private String psuId;
    private String resolucion;
    private int consumoTotalWatts;

    // Constructor vacío requerido por JPA
    public BuildGuardada() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNombreBuild() { return nombreBuild; }
    public void setNombreBuild(String nombreBuild) { this.nombreBuild = nombreBuild; }

    public String getCpuId() { return cpuId; }
    public void setCpuId(String cpuId) { this.cpuId = cpuId; }

    public String getGpuId() { return gpuId; }
    public void setGpuId(String gpuId) { this.gpuId = gpuId; }

    public String getMotherboardId() { return motherboardId; }
    public void setMotherboardId(String motherboardId) { this.motherboardId = motherboardId; }

    public String getRamId() { return ramId; }
    public void setRamId(String ramId) { this.ramId = ramId; }

    public String getPsuId() { return psuId; }
    public void setPsuId(String psuId) { this.psuId = psuId; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public int getConsumoTotalWatts() { return consumoTotalWatts; }
    public void setConsumoTotalWatts(int consumoTotalWatts) { this.consumoTotalWatts = consumoTotalWatts; }
}