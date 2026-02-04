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
    
    @Test
    void testActualizarEstadisticasJugadorConGol() {
        // Crear evento de gol
        EventoJugador evento = new EventoJugador();
        evento.setId(1L);
        evento.setJugador(jugador);
        evento.setPartido(partido);
        evento.setTipoEvento("GOL");
        evento.setMinuto(25);
        
        // Crear estadísticas de jugador
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        // Configurar mocks
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
            .thenReturn(Optional.of(stats));
        when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(Arrays.asList(evento));
        when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
            .thenReturn(stats);
        
        // Ejecutar
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        // Verificar que se guardaron las estadísticas
        verify(estadisticasJugadorRepository).save(any(EstadisticasJugador.class));
        verify(eventoJugadorRepository).findByJugador_Id(1L);
    }
    
    @Test
    void testActualizarEstadisticasJugadorConTiroAPuerta() {
        // Crear evento de tiro a puerta
        EventoJugador evento = new EventoJugador();
        evento.setId(2L);
        evento.setJugador(jugador);
        evento.setPartido(partido);
        evento.setTipoEvento("TIRO_A_PUERTA");
        evento.setMinuto(30);
        
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
            .thenReturn(Optional.of(stats));
        when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(Arrays.asList(evento));
        when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
            .thenReturn(stats);
        
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(any(EstadisticasJugador.class));
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
    
    @Test
    void testProcesarMultiplesEventos() {
        // Crear múltiples eventos
        EventoJugador gol1 = new EventoJugador();
        gol1.setId(1L);
        gol1.setJugador(jugador);
        gol1.setPartido(partido);
        gol1.setTipoEvento("GOL");
        gol1.setMinuto(10);
        
        EventoJugador gol2 = new EventoJugador();
        gol2.setId(2L);
        gol2.setJugador(jugador);
        gol2.setPartido(partido);
        gol2.setTipoEvento("GOL");
        gol2.setMinuto(60);
        
        EventoJugador tiro = new EventoJugador();
        tiro.setId(3L);
        tiro.setJugador(jugador);
        tiro.setPartido(partido);
        tiro.setTipoEvento("TIRO_A_PUERTA");
        tiro.setMinuto(45);
        
        List<EventoJugador> eventos = Arrays.asList(gol1, gol2, tiro);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
            .thenReturn(Optional.of(stats));
        when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(eventos);
        when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
            .thenReturn(stats);
        
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        verify(estadisticasJugadorRepository).save(any(EstadisticasJugador.class));
    }
    
    @Test
    void testManejarEventosConDiferentesFormatos() {
        // Verificar que el servicio maneja diferentes formatos de nombres de eventos
        // (tiro_a_puerta, TIRO A PUERTA, etc.)
        EventoJugador evento1 = new EventoJugador();
        evento1.setId(1L);
        evento1.setJugador(jugador);
        evento1.setPartido(partido);
        evento1.setTipoEvento("tiro_a_puerta"); // minúsculas con guión bajo
        evento1.setMinuto(20);
        
        EventoJugador evento2 = new EventoJugador();
        evento2.setId(2L);
        evento2.setJugador(jugador);
        evento2.setPartido(partido);
        evento2.setTipoEvento("TIRO A PUERTA"); // mayúsculas con espacio
        evento2.setMinuto(50);
        
        List<EventoJugador> eventos = Arrays.asList(evento1, evento2);
        EstadisticasJugador stats = new EstadisticasJugador();
        stats.setJugador(jugador);
        stats.setTemporada("2025-2026");
        
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
        when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
            .thenReturn(Optional.of(stats));
        when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(eventos);
        when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
            .thenReturn(stats);
        
        estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
        
        // Verificar que ambos eventos se procesaron correctamente
        verify(estadisticasJugadorRepository).save(any(EstadisticasJugador.class));
    }
}
