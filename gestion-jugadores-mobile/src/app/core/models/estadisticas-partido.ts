export interface EstadisticasPartido {
  // Información básica del partido
  id: number;
  equipoId: number;
  equipoNombre: string;
  fecha: string;
  titulo: string;
  duracion: number;
  
  // Resultado
  resultado: string; // "VICTORIA", "EMPATE", "DERROTA"
  golesEquipo: number;
  golesRival: number;
  
  // Eventos del partido por jugador
  eventosPorJugador: EventoJugadorResumen[];
  
  // Totales del partido
  totalGoles: number;
  totalAsistencias: number;
  totalPasesClave: number;
  totalTarjetasAmarillas: number;
  totalTarjetasRojas: number;
  totalRobos: number;
  totalTirosAPuerta: number;
  
  // Distribución temporal de eventos principales
  distribucionPasesClave: DistribucionTemporal;
  distribucionTirosAPuerta: DistribucionTemporal;
  distribucionRobos: DistribucionTemporal;
  
  // Por resultado (ganando, empatando, perdiendo)
  pasesClave_ganando: number;
  pasesClave_empatando: number;
  pasesClave_perdiendo: number;
  
  tirosAPuerta_ganando: number;
  tirosAPuerta_empatando: number;
  tirosAPuerta_perdiendo: number;
  
  robos_ganando: number;
  robos_empatando: number;
  robos_perdiendo: number;
}

export interface EventoJugadorResumen {
  jugadorId: number;
  jugadorNombre: string;
  goles: number;
  asistencias: number;
  pasesClave: number;
  tarjetasAmarillas: number;
  tarjetasRojas: number;
  robos: number;
  tirosAPuerta: number;
}

export interface DistribucionTemporal {
  intervalo0_15: number;
  intervalo16_30: number;
  intervalo31_45: number;
  intervalo46_60: number;
  intervalo61_75: number;
  intervalo76_90: number;
}

export interface TopJugador {
  jugadorNombre: string;
  cantidad: number;
}
