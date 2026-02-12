export interface Equipo {
  id: number;
  nombre: string;
  duracionPartido: number;
  tipoFutbol: string; // FUTBOL_7 o FUTBOL_11
  usuarioId: number;
}