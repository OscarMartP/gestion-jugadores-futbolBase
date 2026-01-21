package com.gestion.jugadores.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.jugadores.dto.JugadorDTO;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.servicios.EquipoService;
import com.gestion.jugadores.servicios.JugadorService;
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
class JugadorControladorV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JugadorService jugadorService;

    @MockBean
    private EquipoService equipoService;

    private Jugador jugador1;
    private Jugador jugador2;
    private Equipo equipo;
    private JugadorDTO jugadorDTO;

    @BeforeEach
    void setUp() {
        equipo = new Equipo();
        equipo.setId(1L);
        equipo.setNombre("Equipo A");

        jugador1 = new Jugador();
        jugador1.setId(1L);
        jugador1.setNombre("Juan");
        jugador1.setApellido("Pérez");
        jugador1.setPosicion("Delantero");
        jugador1.setEquipo(equipo);

        jugador2 = new Jugador();
        jugador2.setId(2L);
        jugador2.setNombre("Carlos");
        jugador2.setApellido("López");
        jugador2.setPosicion("Defensa");
        jugador2.setEquipo(equipo);

        jugadorDTO = new JugadorDTO();
        jugadorDTO.setId(1L);
        jugadorDTO.setNombre("Juan");
        jugadorDTO.setApellido("Pérez");
        jugadorDTO.setPosicion("Delantero");
        jugadorDTO.setEquipoId(1L);
    }

    @Test
    void testObtenerJugadorPorId() throws Exception {
        when(jugadorService.obtenerJugadorPorId(1L)).thenReturn(jugador1);

        mockMvc.perform(get("/api/v1/jugadores/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(jugadorService, times(1)).obtenerJugadorPorId(1L);
    }

    @Test
    void testCrearJugador() throws Exception {
        when(equipoService.obtenerEquipoPorId(1L)).thenReturn(equipo);
        when(jugadorService.saveJugador(any(Jugador.class), eq(1L))).thenReturn(jugador1);

        String jugadorJson = objectMapper.writeValueAsString(jugadorDTO);

        mockMvc.perform(post("/api/v1/jugadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jugadorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(equipoService, times(1)).obtenerEquipoPorId(1L);
    }

    @Test
    void testCrearJugadorConEquipoInexistente() throws Exception {
        when(equipoService.obtenerEquipoPorId(999L)).thenReturn(null);

        jugadorDTO.setEquipoId(999L);
        String jugadorJson = objectMapper.writeValueAsString(jugadorDTO);

        mockMvc.perform(post("/api/v1/jugadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jugadorJson))
                .andExpect(status().isBadRequest());

        verify(equipoService, times(1)).obtenerEquipoPorId(999L);
    }

    @Test
    void testObtenerJugadoresPorEquipo() throws Exception {
        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);
        when(jugadorService.obtenerPorEquipo(1L)).thenReturn(jugadores);

        mockMvc.perform(get("/api/v1/jugadores/equipo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(jugadorService, times(1)).obtenerPorEquipo(1L);
    }
}
