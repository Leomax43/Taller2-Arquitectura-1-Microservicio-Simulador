package com.hardwareguru.simulator.controller;

import com.hardwareguru.simulator.dto.SimulacionRestRequest;
import com.hardwareguru.simulator.model.Simulacion;
import com.hardwareguru.simulator.repository.SimulacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulador")
public class SimulatorController {

    @Autowired
    private SimulacionRepository simulacionRepository;

    @PostMapping("/calcular")
    public Map<String, Object> calcularRendimientoREST(@RequestBody SimulacionRestRequest request) {
        
        String userId = request.getUserId();
        int cpuWatts = request.getCpuTdpWatts();
        int gpuWatts = request.getGpuTdpWatts();
        int cpuScore = request.getCpuBenchmarkScore();
        int gpuScore = request.getGpuBenchmarkScore();
        int psuWatts = request.getPsuWatts();
        String resolucion = request.getResolucion();

        int consumoTotal = cpuWatts + gpuWatts + 50; 
        
        double ratio = (double) cpuScore / gpuScore;
        int cuelloBotella = 0;
        String componenteCulpable = "";
        
        double limiteInferior = resolucion.equals("1080p") ? 0.75 : 0.85;
        double limiteSuperior = resolucion.equals("1080p") ? 1.25 : 1.15;

        if (ratio < limiteInferior) {
            cuelloBotella = (int) Math.round((1 - ratio) * 100);
            componenteCulpable = "CPU";
        } else if (ratio > limiteSuperior) {
            cuelloBotella = (int) Math.round((1 - (1/ratio)) * 100);
            componenteCulpable = "GPU";
        }
        
        if (cuelloBotella > 100) cuelloBotella = 100;

        String estado = "Óptimo";
        String motivo = "Tu sistema está perfectamente balanceado.";
        
        if (consumoTotal > (psuWatts * 0.85)) {
            estado = "Riesgo Crítico";
            motivo = "El consumo estimado (" + consumoTotal + "W) excede el margen seguro del 85% de tu fuente (" + psuWatts + "W). ¡Peligro de apagones!";
        } else if (cuelloBotella > 15) {
            estado = "Desbalanceado";
            motivo = "Tienes un cuello de botella del " + cuelloBotella + "% causado por tu " + componenteCulpable + ". No aprovecharás el 100% de tu hardware en " + resolucion + ".";
        }

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
                System.out.println("✅ [REST] Build guardada en BD para el usuario: " + userId);
            } catch (Exception e) {
                System.err.println("❌ [REST] Error al guardar en base de datos: " + e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("consumoTotalWatts", consumoTotal);
        response.put("cuelloBotellaPorcentaje", cuelloBotella);
        response.put("estado", estado);
        response.put("motivo", motivo);

        return response;
    }
}