package com.hardwareguru.simulator.service;

import com.hardwareguru.simulator.model.Simulacion;
import com.hardwareguru.simulator.repository.SimulacionRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

// Importa las clases generadas por gRPC
import com.hardwareguru.simulator.grpc.SimulatorServiceGrpc;
import com.hardwareguru.simulator.grpc.SimulacionRequest;
import com.hardwareguru.simulator.grpc.SimulacionResponse;

@GrpcService
public class SimulatorServiceImpl extends SimulatorServiceGrpc.SimulatorServiceImplBase {

    @Autowired
    private SimulacionRepository simulacionRepository;

    @Override
    public void simularRendimiento(SimulacionRequest request, StreamObserver<SimulacionResponse> responseObserver) {
        
        // 1. Extraer los datos enviados desde el MCP Server de Node.js
        String userId = request.getUserId();
        int cpuWatts = request.getCpuTdpWatts();
        int gpuWatts = request.getGpuTdpWatts();
        int cpuScore = request.getCpuBenchmarkScore();
        int gpuScore = request.getGpuBenchmarkScore();
        int psuWatts = request.getPsuWatts();
        String resolucion = request.getResolucion();

        // ========================================================
        // 2. LÓGICA MATEMÁTICA REAL DE HARDWAREGURU
        // ========================================================
        
        // A) Consumo Estimado
        // Sumamos CPU + GPU y le añadimos un margen de 50W para placa madre, RAM y ventiladores
        int consumoTotal = cpuWatts + gpuWatts + 50; 
        
        // B) Cuello de Botella
        // Usamos una proporción básica entre los puntajes de PassMark
        // (En un entorno real, estos cálculos son mucho más complejos)
        double ratio = (double) cpuScore / gpuScore;
        int cuelloBotella = 0;
        String componenteCulpable = "";
        
        // Ajustamos la sensibilidad según la resolución
        double limiteInferior = resolucion.equals("1080p") ? 0.75 : 0.85;
        double limiteSuperior = resolucion.equals("1080p") ? 1.25 : 1.15;

        if (ratio < limiteInferior) {
            // El CPU es muy débil para la GPU
            cuelloBotella = (int) Math.round((1 - ratio) * 100);
            componenteCulpable = "CPU";
        } else if (ratio > limiteSuperior) {
            // La GPU es muy débil para el CPU
            cuelloBotella = (int) Math.round((1 - (1/ratio)) * 100);
            componenteCulpable = "GPU";
        }
        
        // Topamos el cuello de botella a 100% por sanidad visual
        if(cuelloBotella > 100) cuelloBotella = 100;

        // C) Evaluación de Riesgo y Motivos
        String estado = "Óptimo";
        String motivo = "Tu sistema está perfectamente balanceado.";
        
        // Evaluar la fuente de poder primero (Riesgo Crítico)
        if (consumoTotal > (psuWatts * 0.85)) { // Si supera el 85% de la capacidad de la fuente
            estado = "Riesgo Crítico";
            motivo = "El consumo estimado (" + consumoTotal + "W) excede el margen seguro del 85% de tu fuente (" + psuWatts + "W). ¡Peligro de apagones!";
        } 
        // Evaluar cuellos de botella
        else if (cuelloBotella > 15) {
            estado = "Desbalanceado";
            motivo = "Tienes un cuello de botella del " + cuelloBotella + "% causado por tu " + componenteCulpable + ". No aprovecharás el 100% de tu hardware en " + resolucion + ".";
        }
        
        // ========================================================
        // 3. PERSISTENCIA EN BASE DE DATOS
        // ========================================================
        
        if (userId != null && !userId.isEmpty() && !userId.equals("0")) {
            try {
                Simulacion nuevaBuild = new Simulacion();
                nuevaBuild.setUserId(userId);
                nuevaBuild.setCpuTdpWatts(cpuWatts);
                nuevaBuild.setGpuTdpWatts(gpuWatts);
                nuevaBuild.setPsuWatts(psuWatts);
                nuevaBuild.setResolucion(resolucion);
                nuevaBuild.setConsumoTotalEstimado(consumoTotal);
                nuevaBuild.setCuelloBotellaPorcentaje(cuelloBotella);
                nuevaBuild.setEstadoRiesgo(estado);
                
                simulacionRepository.save(nuevaBuild);
                System.out.println("✅ Build guardada exitosamente en BD para el usuario: " + userId);
                
            } catch (Exception e) {
                System.err.println("❌ Error al guardar en base de datos: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Build generada pero NO guardada (No se proporcionó User ID válido)");
        }

        // ========================================================
        // 4. RETORNO gRPC AL MCP SERVER
        // ========================================================
        
        SimulacionResponse response = SimulacionResponse.newBuilder()
                .setConsumoTotalWatts(consumoTotal)
                .setCuelloBotellaPorcentaje(cuelloBotella)
                .setEstado(estado)
                .setMotivo(motivo)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}