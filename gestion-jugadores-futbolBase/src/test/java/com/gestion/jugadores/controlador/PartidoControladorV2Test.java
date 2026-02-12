package com.gestion.jugadores.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.jugadores.dto.PartidoDTO;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.EquipoService;
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

import java.time.LocalDateTime;
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
class PartidoControladorV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartidoService partidoService;

    @MockBean
    private EquipoService equipoService;

    private Partido partido1;
    private Equipo equipo;
    private PartidoDTO partidoDTO;

    @BeforeEach
    void setUp() {
        equipo = new Equipo();
        equipo.setId(1L);
        equipo.setNombre("Equipo A");

        partido1 = new Partido();
        partido1.setId(1L);
        partido1.setFecha(LocalDateTime.of(2024, 1, 15, 10, 0));
        partido1.setDuracion(90);
        partido1.setPartidoActivo(true);
        partido1.setEquipo(equipo);

        partidoDTO = new PartidoDTO();
        partidoDTO.setId(1L);
        partidoDTO.setFecha(LocalDateTime.of(2024, 1, 15, 10, 0));
        partidoDTO.setDuracion(90);
        partidoDTO.setPartidoActivo(true);
    }

    @Test
    void testObtenerPartidoPorId() throws Exception {
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(partido1);

        mockMvc.perform(get("/api/v1/partidos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(partidoService, times(1)).obtenerPartidoPorId(1L);
    }

    @Test
    void testCrearPartido() throws Exception {
        when(equipoService.obtenerEquipoPorId(1L)).thenReturn(equipo);
        when(partidoService.crearPartido(any(Partido.class))).thenReturn(partido1);

        String partidoJson = objectMapper.writeValueAsString(partidoDTO);

        mockMvc.perform(post("/api/v1/partidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partidoJson))
                .andExpect(status().isOk());

        verify(equipoService, times(1)).obtenerEquipoPorId(anyLong());
    }

    @Test
    void testActivarPartido() throws Exception {
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(partido1);
        when(partidoService.activarPartido(1L)).thenReturn(partido1);

        mockMvc.perform(put("/api/v1/partidos/1/activar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(partidoService, times(1)).activarPartido(1L);
    }

    @Test
    void testDesactivarPartido() throws Exception {
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(partido1);
        when(partidoService.desactivarPartido(1L)).thenReturn(partido1);

        mockMvc.perform(put("/api/v1/partidos/1/desactivar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(partidoService, times(1)).desactivarPartido(1L);
    }

    @Test
    void testObtenerPartidosActivosPorEquipo() throws Exception {
        List<Partido> partidos = Arrays.asList(partido1);
        when(partidoService.obtenerPartidosActivosPorEquipo(1L)).thenReturn(partidos);

        mockMvc.perform(get("/api/v1/partidos/activos/equipo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(partidoService, times(1)).obtenerPartidosActivosPorEquipo(1L);
    }
}
