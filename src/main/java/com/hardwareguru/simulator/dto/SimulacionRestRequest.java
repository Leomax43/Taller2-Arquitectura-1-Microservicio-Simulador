package com.hardwareguru.simulator.dto;

public class SimulacionRestRequest {
    private String userId;
    private int cpuTdpWatts;
    private int gpuTdpWatts;
    private int cpuBenchmarkScore;
    private int gpuBenchmarkScore;
    private int psuWatts;
    private String resolucion;

    // Getters y Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getCpuTdpWatts() { return cpuTdpWatts; }
    public void setCpuTdpWatts(int cpuTdpWatts) { this.cpuTdpWatts = cpuTdpWatts; }

    public int getGpuTdpWatts() { return gpuTdpWatts; }
    public void setGpuTdpWatts(int gpuTdpWatts) { this.gpuTdpWatts = gpuTdpWatts; }

    public int getCpuBenchmarkScore() { return cpuBenchmarkScore; }
    public void setCpuBenchmarkScore(int cpuBenchmarkScore) { this.cpuBenchmarkScore = cpuBenchmarkScore; }

    public int getGpuBenchmarkScore() { return gpuBenchmarkScore; }
    public void setGpuBenchmarkScore(int gpuBenchmarkScore) { this.gpuBenchmarkScore = gpuBenchmarkScore; }

    public int getPsuWatts() { return psuWatts; }
    public void setPsuWatts(int psuWatts) { this.psuWatts = psuWatts; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
}