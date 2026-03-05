package com.gestion.jugadores.servicios;

import com.gestion.jugadores.dto.EstadisticasPartidoDTO;
import com.gestion.jugadores.modelo.*;
import com.gestion.jugadores.repositorio.*;
import com.gestion.jugadores.servicios.impl.EstadisticasServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para EstadisticasServiceImpl
 * Verifican el cálculo correcto de estadísticas a partir de eventos
 */
public class EstadisticasServiceImplTest {

    @Mock
    private EstadisticasJugadorRepository estadisticasJugadorRepository;
    
    @Mock
    private EstadisticasEquipoRepository estadisticasEquipoRepository;
    
    @Mock
    private EventoJugadorRepository eventoJugadorRepository;
    
    @Mock
    private PartidoRepository partidoRepository;
    
    @Mock
    private JugadorRepositorio jugadorRepository;
    
    @Mock
    private EquipoRepository equipoRepository;
    
    @InjectMocks
    private EstadisticasServiceImpl estadisticasService;
    
    private Jugador jugador;
    private Equipo equipo;
    private Partido partido;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Crear equipo de prueba
        equipo = new Equipo();
        equipo.setId(1L);
        equipo.setNombre("Equipo Test");
        
        // Crear jugador de prueba
        jugador = new Jugador();
        jugador.setId(1L);
        jugador.setNombre("Juan");
        jugador.setApellido("Pérez");
        jugador.setEquipo(equipo);
        
        // Crear partido de prueba
        partido = new Partido();
        partido.setId(1L);
        partido.setEquipo(equipo);
        partido.setTitulo("Partido Test");
        partido.setFecha(LocalDateTime.now());
        partido.setResultado("Victoria");
        partido.setGolesEquipo(3);
        partido.setGolesRival(1);
        partido.setDuracion(90);
    }
    
    // ========== TESTS DE CÁLCULO DE GOLES ==========
    
    @Test
    void testCalculoGolesCorrectamente() {
        // Crear 3 goles en diferentes intervalos
        EventoJugador gol1 = crearEvento(1L, jugador, partido, "GOL", 10); // 0-15
        EventoJugador gol2 = crearEvento(2L, jugador, partido, "GOL", 25); // 16-30
        EventoJugador gol3 = crearEvento(3L, jugador, partido, "GOL", 85); // 76-90
        
        List<EventoJugador> eventos = Arrays.asList(gol1, gol2, gol3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        
        // Ejecutar
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        // Capturar el objeto guardado
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalGoles(), "Debe tener 3 goles en total");
            // Los goles también cuentan como tiros a puerta
            assertEquals(3, s.getTotalTirosAPuerta(), "Los goles deben contar como tiros a puerta");
            return true;
        }));
    }
    
    @Test
    void testDistribucionTemporalGoles() {
        // Goles en diferentes intervalos de tiempo
        EventoJugador gol_0_15 = crearEvento(1L, jugador, partido, "GOL", 5);
        EventoJugador gol_16_30 = crearEvento(2L, jugador, partido, "GOL", 20);
        EventoJugador gol_31_45 = crearEvento(3L, jugador, partido, "GOL", 40);
        EventoJugador gol_46_60 = crearEvento(4L, jugador, partido, "GOL", 55);
        EventoJugador gol_61_75 = crearEvento(5L, jugador, partido, "GOL", 70);
        EventoJugador gol_76_90 = crearEvento(6L, jugador, partido, "GOL", 88);
        
        List<EventoJugador> eventos = Arrays.asList(gol_0_15, gol_16_30, gol_31_45, 
                                                     gol_46_60, gol_61_75, gol_76_90);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(6, s.getTotalGoles(), "Total de goles");
            // Verificar distribución temporal (goles también son tiros a puerta)
            assertEquals(1, s.getTirosAPuerta0_15(), "Tiros 0-15 min");
            assertEquals(1, s.getTirosAPuerta16_30(), "Tiros 16-30 min");
            assertEquals(1, s.getTirosAPuerta31_45(), "Tiros 31-45 min");
            assertEquals(1, s.getTirosAPuerta46_60(), "Tiros 46-60 min");
            assertEquals(1, s.getTirosAPuerta61_75(), "Tiros 61-75 min");
            assertEquals(1, s.getTirosAPuerta76_90(), "Tiros 76-90 min");
            return true;
        }));
    }
    
    @Test
    void testDistribucionGolesEnLimitesIntervalos() {
        // Probar goles exactamente en los límites de los intervalos
        EventoJugador gol_0 = crearEvento(1L, jugador, partido, "GOL", 0);   // Inicio 0-15
        EventoJugador gol_15 = crearEvento(2L, jugador, partido, "GOL", 15);  // Fin 0-15
        EventoJugador gol_16 = crearEvento(3L, jugador, partido, "GOL", 16);  // Inicio 16-30
        EventoJugador gol_45 = crearEvento(4L, jugador, partido, "GOL", 45);  // Fin 31-45
        EventoJugador gol_90 = crearEvento(5L, jugador, partido, "GOL", 90);  // Fin 76-90
        
        List<EventoJugador> eventos = Arrays.asList(gol_0, gol_15, gol_16, gol_45, gol_90);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(5, s.getTotalGoles(), "Total de goles");
            assertEquals(2, s.getTirosAPuerta0_15(), "Min 0 y 15 están en 0-15");
            assertEquals(1, s.getTirosAPuerta16_30(), "Min 16 está en 16-30");
            assertEquals(1, s.getTirosAPuerta31_45(), "Min 45 está en 31-45");
            assertEquals(1, s.getTirosAPuerta76_90(), "Min 90 está en 76-90");
            return true;
        }));
    }
    
    // ========== TESTS DE ASISTENCIAS ==========
    
    @Test
    void testCalculoAsistenciasCorrectamente() {
        EventoJugador asist1 = crearEvento(1L, jugador, partido, "ASISTENCIA", 15);
        EventoJugador asist2 = crearEvento(2L, jugador, partido, "ASISTENCIA", 45);
        EventoJugador asist3 = crearEvento(3L, jugador, partido, "ASISTENCIAS", 70); // Variante plural
        
        List<EventoJugador> eventos = Arrays.asList(asist1, asist2, asist3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalAsistencias(), "Debe tener 3 asistencias");
            return true;
        }));
    }
    
    // ========== TESTS DE TARJETAS ==========
    
    @Test
    void testCalculoTarjetasAmarillasYRojas() {
        EventoJugador amarilla1 = crearEvento(1L, jugador, partido, "TARJETA_AMARILLA", 20);
        EventoJugador amarilla2 = crearEvento(2L, jugador, partido, "AMARILLA", 45);
        EventoJugador roja = crearEvento(3L, jugador, partido, "TARJETA_ROJA", 80);
        
        List<EventoJugador> eventos = Arrays.asList(amarilla1, amarilla2, roja);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(2, s.getTarjetasAmarillas(), "Debe tener 2 tarjetas amarillas");
            assertEquals(1, s.getTarjetasRojas(), "Debe tener 1 tarjeta roja");
            return true;
        }));
    }
    
    // ========== TESTS DE PASES CLAVE ==========
    
    @Test
    void testCalculoPasesClave() {
        EventoJugador pase1 = crearEvento(1L, jugador, partido, "PASE_CLAVE", 10);
        EventoJugador pase2 = crearEvento(2L, jugador, partido, "PASE CLAVE", 30); // Variante con espacio
        EventoJugador pase3 = crearEvento(3L, jugador, partido, "PASE_CLAVE", 60);
        
        List<EventoJugador> eventos = Arrays.asList(pase1, pase2, pase3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalPasesClave(), "Debe tener 3 pases clave");
            return true;
        }));
    }
    
    @Test
    void testDistribucionTemporalPasesClave() {
        // Pases clave en cada intervalo
        EventoJugador pase_0_15 = crearEvento(1L, jugador, partido, "PASE_CLAVE", 10);
        EventoJugador pase_16_30 = crearEvento(2L, jugador, partido, "PASE_CLAVE", 25);
        EventoJugador pase_31_45 = crearEvento(3L, jugador, partido, "PASE_CLAVE", 40);
        EventoJugador pase_46_60 = crearEvento(4L, jugador, partido, "PASE_CLAVE", 55);
        EventoJugador pase_61_75 = crearEvento(5L, jugador, partido, "PASE_CLAVE", 70);
        EventoJugador pase_76_90 = crearEvento(6L, jugador, partido, "PASE_CLAVE", 85);
        EventoJugador pase_76_90_b = crearEvento(7L, jugador, partido, "PASE_CLAVE", 90); // Otro en mismo intervalo
        
        List<EventoJugador> eventos = Arrays.asList(pase_0_15, pase_16_30, pase_31_45, 
                                                     pase_46_60, pase_61_75, pase_76_90, pase_76_90_b);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(7, s.getTotalPasesClave(), "Total pases clave");
            assertEquals(1, s.getPasesClave0_15(), "Pases 0-15 min");
            assertEquals(1, s.getPasesClave16_30(), "Pases 16-30 min");
            assertEquals(1, s.getPasesClave31_45(), "Pases 31-45 min");
            assertEquals(1, s.getPasesClave46_60(), "Pases 46-60 min");
            assertEquals(1, s.getPasesClave61_75(), "Pases 61-75 min");
            assertEquals(2, s.getPasesClave76_90(), "Pases 76-90 min (2)");
            return true;
        }));
    }
    
    // ========== TESTS DE TIROS A PUERTA ==========
    
    @Test
    void testCalculoTirosAPuerta() {
        EventoJugador tiro1 = crearEvento(1L, jugador, partido, "TIRO_A_PUERTA", 20);
        EventoJugador tiro2 = crearEvento(2L, jugador, partido, "TIRO A PUERTA", 40); // Variante con espacio
        EventoJugador tiro3 = crearEvento(3L, jugador, partido, "TIRO_A_PUERTA", 75);
        
        List<EventoJugador> eventos = Arrays.asList(tiro1, tiro2, tiro3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalTirosAPuerta(), "Debe tener 3 tiros a puerta");
            return true;
        }));
    }
    
    @Test
    void testGolesYTirosAPuertaSeSumanCorrectamente() {
        // Los goles también deben contar como tiros a puerta
        EventoJugador gol = crearEvento(1L, jugador, partido, "GOL", 20);
        EventoJugador tiro1 = crearEvento(2L, jugador, partido, "TIRO_A_PUERTA", 30);
        EventoJugador tiro2 = crearEvento(3L, jugador, partido, "TIRO_A_PUERTA", 50);
        
        List<EventoJugador> eventos = Arrays.asList(gol, tiro1, tiro2);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(1, s.getTotalGoles(), "1 gol");
            assertEquals(3, s.getTotalTirosAPuerta(), "1 gol + 2 tiros = 3 tiros totales");
            return true;
        }));
    }
    
    @Test
    void testDistribucionTemporalTirosAPuerta() {
        EventoJugador tiro1 = crearEvento(1L, jugador, partido, "TIRO_A_PUERTA", 5);
        EventoJugador tiro2 = crearEvento(2L, jugador, partido, "TIRO_A_PUERTA", 20);
        EventoJugador tiro3 = crearEvento(3L, jugador, partido, "TIRO_A_PUERTA", 35);
        
        List<EventoJugador> eventos = Arrays.asList(tiro1, tiro2, tiro3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalTirosAPuerta(), "Total tiros");
            assertEquals(1, s.getTirosAPuerta0_15(), "Tiro en min 5");
            assertEquals(1, s.getTirosAPuerta16_30(), "Tiro en min 20");
            assertEquals(1, s.getTirosAPuerta31_45(), "Tiro en min 35");
            return true;
        }));
    }
    
    @Test
    void testObtenerEstadisticasPartido() {
        // Crear evento de gol para el partido
        EventoJugador gol = new EventoJugador();
        gol.setId(1L);
        gol.setJugador(jugador);
        gol.setPartido(partido);
        gol.setTipoEvento("GOL");
        gol.setMinuto(25);
        
        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partido));
        when(eventoJugadorRepository.findByPartido_Id(1L)).thenReturn(Arrays.asList(gol));
        
        EstadisticasPartidoDTO resultado = estadisticasService.obtenerEstadisticasPartido(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Equipo Test", resultado.getEquipoNombre());
        assertEquals(3, resultado.getGolesEquipo());
        assertEquals(1, resultado.getGolesRival());
    }
    
    @Test
    void testCrearEstadisticasNuevasCuandoNoExisten() {
        // Configurar mocks para jugador sin estadísticas previas
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
            .thenReturn(Optional.empty());
        when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(new ArrayList<>());
        when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        // Verificar que se creó una nueva estadística
        verify(estadisticasJugadorRepository).save(any(EstadisticasJugador.class));
    }
    
    // ========== TESTS DE ROBOS Y PÉRDIDAS ==========
    
    @Test
    void testCalculoRobos() {
        EventoJugador robo1 = crearEvento(1L, jugador, partido, "ROBO", 15);
        EventoJugador robo2 = crearEvento(2L, jugador, partido, "ROBOS", 45); // Variante plural
        EventoJugador robo3 = crearEvento(3L, jugador, partido, "ROBO", 75);
        
        List<EventoJugador> eventos = Arrays.asList(robo1, robo2, robo3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalRobos(), "Debe tener 3 robos");
            return true;
        }));
    }
    
    @Test
    void testDistribucionTemporalRobos() {
        EventoJugador robo1 = crearEvento(1L, jugador, partido, "ROBO", 10);
        EventoJugador robo2 = crearEvento(2L, jugador, partido, "ROBO", 25);
        EventoJugador robo3 = crearEvento(3L, jugador, partido, "ROBO", 80);
        
        List<EventoJugador> eventos = Arrays.asList(robo1, robo2, robo3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalRobos(), "Total robos");
            assertEquals(1, s.getRobos0_15(), "Robo en min 10");
            assertEquals(1, s.getRobos16_30(), "Robo en min 25");
            assertEquals(1, s.getRobos76_90(), "Robo en min 80");
            return true;
        }));
    }
    
    @Test
    void testCalculoPerdidas() {
        EventoJugador perdida1 = crearEvento(1L, jugador, partido, "PERDIDA", 20);
        EventoJugador perdida2 = crearEvento(2L, jugador, partido, "PERDIDAS", 50); // Variante plural
        EventoJugador perdida3 = crearEvento(3L, jugador, partido, "PERDIDA", 85);
        
        List<EventoJugador> eventos = Arrays.asList(perdida1, perdida2, perdida3);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalPerdidas(), "Debe tener 3 pérdidas");
            return true;
        }));
    }
    
    // ========== TESTS DE ESCENARIOS COMPLETOS ==========
    
    @Test
    void testEscenarioPartidoCompleto() {
        // Simular un partido completo con diversos eventos
        List<EventoJugador> eventos = Arrays.asList(
            crearEvento(1L, jugador, partido, "GOL", 15),
            crearEvento(2L, jugador, partido, "GOL", 45),
            crearEvento(3L, jugador, partido, "ASISTENCIA", 30),
            crearEvento(4L, jugador, partido, "ASISTENCIA", 60),
            crearEvento(5L, jugador, partido, "TIRO_A_PUERTA", 10),
            crearEvento(6L, jugador, partido, "TIRO_A_PUERTA", 25),
            crearEvento(7L, jugador, partido, "TIRO_A_PUERTA", 75),
            crearEvento(8L, jugador, partido, "PASE_CLAVE", 5),
            crearEvento(9L, jugador, partido, "PASE_CLAVE", 35),
            crearEvento(10L, jugador, partido, "PASE_CLAVE", 55),
            crearEvento(11L, jugador, partido, "ROBO", 20),
            crearEvento(12L, jugador, partido, "ROBO", 70),
            crearEvento(13L, jugador, partido, "PERDIDA", 40),
            crearEvento(14L, jugador, partido, "TARJETA_AMARILLA", 65)
        );
        
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            // Verificar todos los totales
            assertEquals(2, s.getTotalGoles(), "2 goles");
            assertEquals(2, s.getTotalAsistencias(), "2 asistencias");
            assertEquals(5, s.getTotalTirosAPuerta(), "2 goles + 3 tiros = 5 tiros totales");
            assertEquals(3, s.getTotalPasesClave(), "3 pases clave");
            assertEquals(2, s.getTotalRobos(), "2 robos");
            assertEquals(1, s.getTotalPerdidas(), "1 pérdida");
            assertEquals(1, s.getTarjetasAmarillas(), "1 tarjeta amarilla");
            assertEquals(0, s.getTarjetasRojas(), "0 tarjetas rojas");
            assertEquals(1, s.getPartidosJugados(), "1 partido jugado");
            return true;
        }));
    }
    
    @Test
    void testMultiplesPartidos() {
        // Crear segundo partido
        Partido partido2 = new Partido();
        partido2.setId(2L);
        partido2.setEquipo(equipo);
        partido2.setTitulo("Partido 2");
        partido2.setFecha(LocalDateTime.now());
        partido2.setResultado("Empate");
        partido2.setGolesEquipo(1);
        partido2.setGolesRival(1);
        partido2.setDuracion(90);
        
        List<EventoJugador> eventos = Arrays.asList(
            crearEvento(1L, jugador, partido, "GOL", 20),
            crearEvento(2L, jugador, partido, "GOL", 50),
            crearEvento(3L, jugador, partido2, "GOL", 30),
            crearEvento(4L, jugador, partido2, "ASISTENCIA", 60)
        );
        
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(3, s.getTotalGoles(), "3 goles en 2 partidos");
            assertEquals(1, s.getTotalAsistencias(), "1 asistencia");
            assertEquals(2, s.getPartidosJugados(), "2 partidos jugados");
            return true;
        }));
    }
    
    @Test
    void testJugadorSinEventos() {
        // Jugador que no tiene eventos (estadísticas en 0)
        List<EventoJugador> eventos = new ArrayList<>();
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        configurarMocks(stats, eventos);
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(argThat(s -> {
            assertEquals(0, s.getTotalGoles(), "0 goles");
            assertEquals(0, s.getTotalAsistencias(), "0 asistencias");
            assertEquals(0, s.getTotalTirosAPuerta(), "0 tiros");
            assertEquals(0, s.getPartidosJugados(), "0 partidos");
            return true;
        }));
    }
    
    // ========== MÉTODOS HELPER ==========
    
    /**
     * Método helper para crear eventos más fácilmente
     */
    private EventoJugador crearEvento(Long id, Jugador jugador, Partido partido, String tipo, Integer minuto) {
        EventoJugador evento = new EventoJugador();
        evento.setId(id);
        evento.setJugador(jugador);
        evento.setPartido(partido);
        evento.setTipoEvento(tipo);
        evento.setMinuto(minuto);
        return evento;
    }
    
    /**
     * Método helper para configurar mocks comunes
     */
    private void configurarMocks(EstadisticasJugador stats, List<EventoJugador> eventos) {
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
            .thenReturn(Optional.of(stats));
        when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(eventos);
        when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
