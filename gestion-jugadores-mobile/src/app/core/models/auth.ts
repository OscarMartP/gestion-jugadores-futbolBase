export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido?: string;
  email: string;
  password: string;
  telefono?: string;
}

export interface AuthResponse {
  token: string;
  usuario: Usuario;
}

export interface Usuario {
  id: number;
  nombre: string;
  apellido?: string;
  email: string;
  telefono?: string;
  rol: string;
  username?: string;
}