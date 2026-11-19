package com.hardwareguru.simulator.dto;

public class GuardarBuildRequest {
    private String userId;
    private String nombreBuild;
    private String cpuNombre;
    private String placaMadreNombre;
    private String ramNombre;
    private String gpuNombre;
    private String fuenteNombre;
    private int cpuTdpWatts;
    private int gpuTdpWatts;
    private int psuWatts;
    private String resolucion;
    private int consumoTotalEstimado;
    private int cuelloBotellaPorcentaje;
    private String estadoRiesgo;

    // Getters y Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNombreBuild() { return nombreBuild; }
    public void setNombreBuild(String nombreBuild) { this.nombreBuild = nombreBuild; }

    public String getCpuNombre() { return cpuNombre; }
    public void setCpuNombre(String cpuNombre) { this.cpuNombre = cpuNombre; }

    public String getPlacaMadreNombre() { return placaMadreNombre; }
    public void setPlacaMadreNombre(String placaMadreNombre) { this.placaMadreNombre = placaMadreNombre; }

    public String getRamNombre() { return ramNombre; }
    public void setRamNombre(String ramNombre) { this.ramNombre = ramNombre; }

    public String getGpuNombre() { return gpuNombre; }
    public void setGpuNombre(String gpuNombre) { this.gpuNombre = gpuNombre; }

    public String getFuenteNombre() { return fuenteNombre; }
    public void setFuenteNombre(String fuenteNombre) { this.fuenteNombre = fuenteNombre; }

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
}