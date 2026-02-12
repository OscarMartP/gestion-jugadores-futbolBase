export interface Partido {
  id: number;
  equipoId: number;
  fecha: string;
  duracion: number;
  titulo?: string;
  partidoActivo: boolean;
  resultado?: string;
  golesEquipo?: number;
  golesRival?: number;
  titulares: number[];
  suplentes: number[];
}

export interface EventoJugador {
  id?: number;
  jugadorId: number;
  partidoId: number;
  tipoEvento: string;
  minuto: number;
  jugadorSaleId?: number;
  jugadorEntraId?: number;
}

export enum TipoEvento {
  GOL = 'GOL',
  ASISTENCIA = 'ASISTENCIA',
  PASE_CLAVE = 'PASE_CLAVE',
  ROBO = 'ROBO',
  TIRO_PUERTA = 'TIRO_A_PUERTA',
  TARJETA_AMARILLA = 'TARJETA_AMARILLA',
  TARJETA_ROJA = 'TARJETA_ROJA',
  PARADA = 'PARADA',
  SUSTITUCION = 'SUSTITUCION',
  GOL_RIVAL = 'GOL_RIVAL',
  PERDIDA = 'PERDIDA'
}
