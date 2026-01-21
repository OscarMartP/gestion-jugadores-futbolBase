export interface EstadisticasPartidoDTO {
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
  
  // Estadísticas de posesión y tiros
  tirosRecibidos: number;
  
  // Distribución temporal de eventos
  distribucionGoles: DistribucionTemporal;
  distribucionAsistencias: DistribucionTemporal;
  distribucionTarjetas: DistribucionTemporal;
  distribucionTirosRecibidos: DistribucionTemporal;
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
