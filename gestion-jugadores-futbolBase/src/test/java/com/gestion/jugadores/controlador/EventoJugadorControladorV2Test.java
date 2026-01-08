package com.gestion.jugadores.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.jugadores.dto.EventoJugadorDTO;
import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.EventoJugadorService;
import com.gestion.jugadores.servicios.JugadorService;
import com.gestion.jugadores.servicios.PartidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER"})
class EventoJugadorControladorV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventoJugadorService eventoJugadorService;

    @MockBean
    private JugadorService jugadorService;

    @MockBean
    private PartidoService partidoService;

    private EventoJugador evento1;
    private EventoJugador evento2;
    private Jugador jugador;
    private Partido partido;
    private EventoJugadorDTO eventoDTO;

    @BeforeEach
    void setUp() {
        jugador = new Jugador();
        jugador.setId(1L);
        jugador.setNombre("Juan");
        jugador.setApellido("Pérez");

        partido = new Partido();
        partido.setId(1L);

        evento1 = new EventoJugador();
        evento1.setId(1L);
        evento1.setTipoEvento("GOL");
        evento1.setMinuto(15);
        evento1.setJugador(jugador);
        evento1.setPartido(partido);

        evento2 = new EventoJugador();
        evento2.setId(2L);
        evento2.setTipoEvento("TARJETA_AMARILLA");
        evento2.setMinuto(30);
        evento2.setJugador(jugador);
        evento2.setPartido(partido);

        eventoDTO = new EventoJugadorDTO();
        eventoDTO.setTipoEvento("GOL");
        eventoDTO.setMinuto(15);
        eventoDTO.setJugadorId(1L);
        eventoDTO.setPartidoId(1L);
    }

    @Test
    void testObtenerEventoPorId() throws Exception {
        when(eventoJugadorService.obtenerEventoPorId(1L)).thenReturn(evento1);

        mockMvc.perform(get("/api/v1/eventos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEvento").value("GOL"))
                .andExpect(jsonPath("$.minuto").value(15));

        verify(eventoJugadorService, times(1)).obtenerEventoPorId(1L);
    }

    @Test
    void testCrearEvento() throws Exception {
        when(jugadorService.obtenerJugadorPorId(1L)).thenReturn(jugador);
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(partido);
        when(eventoJugadorService.registrarEvento(any(EventoJugador.class))).thenReturn(evento1);

        String eventoJson = objectMapper.writeValueAsString(eventoDTO);

        mockMvc.perform(post("/api/v1/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEvento").value("GOL"));

        verify(jugadorService, times(1)).obtenerJugadorPorId(1L);
        verify(partidoService, times(1)).obtenerPartidoPorId(1L);
    }

    @Test
    void testObtenerEventosPorJugador() throws Exception {
        List<EventoJugador> eventos = Arrays.asList(evento1, evento2);
        when(eventoJugadorService.obtenerEventosPorJugador(1L)).thenReturn(eventos);

        mockMvc.perform(get("/api/v1/eventos/jugador/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(eventoJugadorService, times(1)).obtenerEventosPorJugador(1L);
    }

    @Test
    void testObtenerEventosPorPartido() throws Exception {
        List<EventoJugador> eventos = Arrays.asList(evento1);
        when(eventoJugadorService.obtenerEventosPorPartido(1L)).thenReturn(eventos);

        mockMvc.perform(get("/api/v1/eventos/partido/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(eventoJugadorService, times(1)).obtenerEventosPorPartido(1L);
    }
}
