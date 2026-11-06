package com.hardwareguru.simulator;

import com.hardwareguru.simulator.grpc.SimulacionRequest;
import com.hardwareguru.simulator.grpc.SimulacionResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.hardwareguru.simulator.grpc.GuardarBuildRequest;
import com.hardwareguru.simulator.grpc.GuardarBuildResponse;




class SimulatorServiceImplTest {

    // Inyecta el servicio real que vamos a probar
    @InjectMocks
    private SimulatorServiceImpl simulatorService;

    // "Finge" el repositorio y el observador de gRPC para que no necesiten Base de Datos
    @Mock
    private BuildRepository buildRepository;

    @Mock
    private StreamObserver<SimulacionResponse> responseObserver;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSimularRendimiento_CasoBalanceado() {
        // 1. Preparar datos de entrada (Simulando un Ryzen 5 9600X + RTX 5060 Ti)
        SimulacionRequest request = SimulacionRequest.newBuilder()
                .setCpuTdpWatts(65)
                .setCpuBenchmarkScore(25000)
                .setGpuTdpWatts(160)
                .setGpuBenchmarkScore(20000)
                .setResolucion("1080p")
                .setPsuWatts(500)
                .build();

        // 2. Ejecutar la función
        simulatorService.simularRendimiento(request, responseObserver);

        // 3. Capturar la respuesta que el servicio intentó enviar
        ArgumentCaptor<SimulacionResponse> responseCaptor = ArgumentCaptor.forClass(SimulacionResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        SimulacionResponse response = responseCaptor.getValue();

        // 4. Verificar la matemática: Consumo = (65 + 160 + 50) * 1.2 = 330 Watts
        assertEquals(330, response.getConsumoTotalWatts());
        assertEquals(0, response.getCuelloBotellaPorcentaje());
        assertEquals("Balanceado", response.getEstado());
    }


    @Test
    void testGuardarBuild_Exito() {
        // 1. Preparar la petición para guardar
        GuardarBuildRequest request = GuardarBuildRequest.newBuilder()
                .setUserId("user-123")
                .setNombreBuild("PC Gamer")
                .setCpuId("ryzen-5-9600x")
                .setGpuId("rtx-5060-ti")
                .setMotherboardId("b650")
                .setRamId("ddr5-16gb")
                .setPsuId("psu-500w")
                .setResolucion("1080p")
                .setConsumoTotalWatts(330)
                .build();

        // 2. Simular que la base de datos guarda correctamente y devuelve un objeto con ID
        BuildGuardada buildSimulada = new BuildGuardada();
        buildSimulada.setId("mi-uuid-generado");
        when(buildRepository.save(any(BuildGuardada.class))).thenReturn(buildSimulada);

        // 3. Crear un observador falso para capturar la respuesta
        @SuppressWarnings("unchecked")
        StreamObserver<GuardarBuildResponse> mockObserver = org.mockito.Mockito.mock(StreamObserver.class);

        // 4. Ejecutar la función
        simulatorService.guardarBuild(request, mockObserver);

        // 5. Verificar
        ArgumentCaptor<GuardarBuildResponse> responseCaptor = ArgumentCaptor.forClass(GuardarBuildResponse.class);
        verify(mockObserver).onNext(responseCaptor.capture());
        
        GuardarBuildResponse response = responseCaptor.getValue();
        assertEquals("mi-uuid-generado", response.getBuildId());
        assertEquals(true, response.getSuccess());
    }

    @Test
    void testSimularRendimiento_CasoIncompatible() {
        // Simulamos un PC donde el consumo (TDP) destroza la fuente de poder elegida
        SimulacionRequest request = SimulacionRequest.newBuilder()
                .setCpuTdpWatts(150)
                .setCpuBenchmarkScore(25000)
                .setGpuTdpWatts(350) // ¡GPU que consume muchísimo!
                .setGpuBenchmarkScore(30000)
                .setResolucion("1440p")
                .setPsuWatts(450) // Fuente muy pequeña
                .build();

        simulatorService.simularRendimiento(request, responseObserver);

        ArgumentCaptor<SimulacionResponse> responseCaptor = ArgumentCaptor.forClass(SimulacionResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());

        SimulacionResponse response = responseCaptor.getValue();
        assertEquals("Incompatible", response.getEstado());
        assertEquals("El consumo estimado supera la capacidad de la fuente.", response.getMotivo());
    }

    @Test
    void testSimularRendimiento_CasoRiesgoCuelloBotella() {
        // Simulamos un PC con una GPU extremadamente potente pero un CPU débil en 1080p
        SimulacionRequest request = SimulacionRequest.newBuilder()
                .setCpuTdpWatts(65)
                .setCpuBenchmarkScore(5000)   // CPU débil
                .setGpuTdpWatts(200)
                .setGpuBenchmarkScore(20000)  // GPU fuerte
                .setResolucion("1080p")       // A 1080p esto generará un gran cuello de botella
                .setPsuWatts(600)
                .build();

        simulatorService.simularRendimiento(request, responseObserver);

        ArgumentCaptor<SimulacionResponse> responseCaptor = ArgumentCaptor.forClass(SimulacionResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());

        SimulacionResponse response = responseCaptor.getValue();
        assertEquals("En riesgo", response.getEstado());
        // El cuello de botella será alto (mayor al 10%)
        assertEquals(true, response.getCuelloBotellaPorcentaje() > 10); 
    }
}