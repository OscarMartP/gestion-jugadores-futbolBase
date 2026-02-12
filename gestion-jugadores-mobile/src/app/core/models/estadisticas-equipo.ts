// Modelos para estadísticas de equipo

export interface EstadisticasEquipo {
  equipoId: number;
  equipoNombre: string;
  temporada: string;
  
  // Totales generales
  totalPartidos: number;
  partidosGanados: number;
  partidosEmpatados: number;
  partidosPerdidos: number;
  totalGoles: number;
  totalGolesRecibidos: number;
  totalAsistencias: number;
  totalPasesClave: number;
  totalTarjetasAmarillas: number;
  totalTarjetasRojas: number;
  totalRobos: number;
  totalTirosAPuerta: number;
  
  // Promedios por partido
  promedioPasesClave: number;
  promedioRobos: number;
  promedioTirosAPuerta: number;
  
  // Distribución por resultado (ganando/empatando/perdiendo)
  pasesClave_ganando: number;
  pasesClave_empatando: number;
  pasesClave_perdiendo: number;
  
  tirosAPuerta_ganando: number;
  tirosAPuerta_empatando: number;
  tirosAPuerta_perdiendo: number;
  
  robos_ganando: number;
  robos_empatando: number;
  robos_perdiendo: number;
  
  // Distribución por tiempo (6 intervalos de 15 minutos)
  distribucionPasesClave: DistribucionTemporal;
  distribucionTirosAPuerta: DistribucionTemporal;
  distribucionRobos: DistribucionTemporal;
}

export interface DistribucionTemporal {
  intervalo0_15: number;
  intervalo16_30: number;
  intervalo31_45: number;
  intervalo46_60: number;
  intervalo61_75: number;
  intervalo76_90: number;
}

export interface EstadisticasJugadorEquipo {
  jugadorId: number;
  jugadorNombre: string;
  jugadorApellido: string;
  posicion: string;
  numeroCamiseta: number;
  
  // Estadísticas
  partidosJugados: number;
  goles: number;
  asistencias: number;
  pasesClave: number;
  robos: number;
  tirosAPuerta: number;
  tarjetasAmarillas: number;
  tarjetasRojas: number;
  
  // Promedios
  promedioGoles: number;
  promedioAsistencias: number;
  promedioPasesClave: number;
  promedioRobos: number;
  promedioTirosAPuerta: number;
}
