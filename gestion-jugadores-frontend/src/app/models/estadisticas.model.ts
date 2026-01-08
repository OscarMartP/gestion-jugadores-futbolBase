// Interfaces para el sistema de estadísticas

export interface EstadisticasJugadorDTO {
  id: number;
  jugadorId: number;
  jugadorNombre: string;
  jugadorApellido: string;
  posicion: string;
  temporada: string;
  totalGoles: number;
  golesEnCasa: number;
  golesFuera: number;
  totalAsistencias: number;
  tarjetasAmarillas: number;
  tarjetasRojas: number;
  paradas: number; // Específico para porteros
  partidosJugados: number;
  partidosTitular: number;
  minutosJugados: number;
  totalPasesClave: number;
  pasesClave0_15: number;
  pasesClave16_30: number;
  pasesClave31_45: number;
  pasesClave46_60: number;
  pasesClave61_75: number;
  pasesClave76_90: number;
  pasesClaveGanando: number;
  pasesClaveEmpatando: number;
  pasesClavePerdiendo: number;
  pasesClaveP90: number;
  promedioGoles: number;
  promedioAsistencias: number;
  rating: number;
}

export interface EstadisticasEquipoDTO {
  id: number;
  equipoId: number;
  equipoNombre: string;
  temporada: string;
  partidosJugados: number;
  partidosGanados: number;
  partidosEmpatados: number;
  partidosPerdidos: number;
  puntos: number;
  golesFavor: number;
  golesContra: number;
  diferenciaGoles: number;
  tarjetasAmarillas: number;
  tarjetasRojas: number;
  totalPasesClave: number;
  pasesClave0_15: number;
  pasesClave16_30: number;
  pasesClave31_45: number;
  pasesClave46_60: number;
  pasesClave61_75: number;
  pasesClave76_90: number;
  pasesClaveGanando: number;
  pasesClaveEmpatando: number;
  pasesClavePerdiendo: number;
  pasesClaveP90: number;
  mayorPasador: string;
  promedioGolesFavor: number;
  promedioGolesContra: number;
  efectividad: number;
}

export interface ResumenEstadisticasDTO {
  estadisticasEquipo: EstadisticasEquipoDTO;
  topGoleadores: EstadisticasJugadorDTO[];
  topAsistentes: EstadisticasJugadorDTO[];
  menosTargetas: EstadisticasJugadorDTO[];
  totalJugadores: number;
}
