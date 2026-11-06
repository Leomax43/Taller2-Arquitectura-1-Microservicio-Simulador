package com.hardwareguru.simulator;

import com.hardwareguru.simulator.grpc.GuardarBuildRequest;
import com.hardwareguru.simulator.grpc.GuardarBuildResponse;
import com.hardwareguru.simulator.grpc.SimulacionRequest;
import com.hardwareguru.simulator.grpc.SimulacionResponse;
import com.hardwareguru.simulator.grpc.SimulatorServiceGrpc.SimulatorServiceImplBase;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class SimulatorServiceImpl extends SimulatorServiceImplBase {

    @Autowired
    private BuildRepository buildRepository; // Inyectamos el repositorio

    @Override
    public void simularRendimiento(SimulacionRequest request, StreamObserver<SimulacionResponse> responseObserver) {
        
        int consumoTotal = calcularConsumoTotal(request.getCpuTdpWatts(), request.getGpuTdpWatts());
        int cuelloBotella = calcularCuelloBotella(request.getCpuBenchmarkScore(), request.getGpuBenchmarkScore(), request.getResolucion());
        String estado = determinarEstado(consumoTotal, request.getPsuWatts(), cuelloBotella);
        String motivo = generarMotivo(estado, consumoTotal, request.getPsuWatts(), cuelloBotella);

        SimulacionResponse response = SimulacionResponse.newBuilder()
                .setConsumoTotalWatts(consumoTotal)
                .setCuelloBotellaPorcentaje(cuelloBotella)
                .setEstado(estado)
                .setMotivo(motivo)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void guardarBuild(GuardarBuildRequest request, StreamObserver<GuardarBuildResponse> responseObserver) {
        
        // 1. Mapear los datos del request a la Entidad
        BuildGuardada nuevaBuild = new BuildGuardada();
        nuevaBuild.setUserId(request.getUserId());
        nuevaBuild.setNombreBuild(request.getNombreBuild());
        nuevaBuild.setCpuId(request.getCpuId());
        nuevaBuild.setGpuId(request.getGpuId());
        nuevaBuild.setMotherboardId(request.getMotherboardId());
        nuevaBuild.setRamId(request.getRamId());
        nuevaBuild.setPsuId(request.getPsuId());
        nuevaBuild.setResolucion(request.getResolucion());
        nuevaBuild.setConsumoTotalWatts(request.getConsumoTotalWatts());

        // 2. Guardar en la base de datos a través del repositorio
        try {
            BuildGuardada buildGuardada = buildRepository.save(nuevaBuild);
            
            GuardarBuildResponse response = GuardarBuildResponse.newBuilder()
                    .setBuildId(buildGuardada.getId())
                    .setSuccess(true)
                    .build();
            responseObserver.onNext(response);

        } catch (Exception e) {
             GuardarBuildResponse response = GuardarBuildResponse.newBuilder()
                    .setBuildId("")
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
        }
        
        responseObserver.onCompleted();
    }

    // Métodos auxiliares de cálculo... (mantén los métodos calcularConsumoTotal, calcularCuelloBotella, determinarEstado y generarMotivo igual que antes)
    private int calcularConsumoTotal(int cpuTdp, int gpuTdp) {
        return (int) ((cpuTdp + gpuTdp + 50) * 1.2);
    }

    private int calcularCuelloBotella(int cpuScore, int gpuScore, String resolucion) {
        double ratio = (double) cpuScore / gpuScore;
        double umbral = resolucion.equals("1080p") ? 1.0 : 0.8; 
        
        if (ratio < umbral) {
            return (int) ((1 - ratio / umbral) * 100); 
        }
        return 0; 
    }

    private String determinarEstado(int consumoTotal, int psuWatts, int cuelloBotella) {
        if (consumoTotal > psuWatts) return "Incompatible";
        if (consumoTotal > psuWatts * 0.85 || cuelloBotella > 10) return "En riesgo";
        return "Balanceado";
    }

    private String generarMotivo(String estado, int consumo, int psu, int cuello) {
        if (estado.equals("Incompatible")) return "El consumo estimado supera la capacidad de la fuente.";
        if (estado.equals("En riesgo") && consumo > psu * 0.85) return "Tu fuente de " + psu + " W no soportará los picos de carga.";
        if (estado.equals("En riesgo") && cuello > 10) return "Hay un " + cuello + "% de cuello de botella estimado.";
        return "";
    }
}