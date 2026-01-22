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
  GOL = 'gol',
  ASISTENCIA = 'asistencia',
  PASE_CLAVE = 'pase_clave',
  ROBO = 'robo',
  TIRO_PUERTA = 'tiro_puerta',
  TARJETA_AMARILLA = 'tarjeta_amarilla',
  TARJETA_ROJA = 'tarjeta_roja',
  PARADA = 'parada',
  SUSTITUCION = 'sustitucion',
  GOL_RIVAL = 'gol_rival'
}
