import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, map, switchMap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse, Usuario } from '../models/auth';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authUrl = baserUrl;
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Verificar si hay token guardado al inicializar
    const token = this.getToken();
    const usuario = this.getUsuario();
    if (token && usuario) {
      this.currentUserSubject.next(usuario);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    console.log('🔐 Intentando login con:', credentials.email);
    // Adaptar formato para el backend real
    const backendCredentials = {
      username: credentials.email,
      password: credentials.password
    };
    return this.http.post<{token: string}>(`${this.authUrl}/generate-token`, backendCredentials).pipe(
      switchMap(tokenResponse => {
        // Guardar token temporalmente para la siguiente petición
        const token = tokenResponse.token;
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        
        // Obtener datos completos del usuario
        return this.http.get<any>(`${this.authUrl}/actual-usuario`, { headers }).pipe(
          map(usuario => {
            const authResponse: AuthResponse = {
              token: token,
              usuario: {
                id: usuario.id,
                email: usuario.username || usuario.email || '',
                nombre: usuario.nombre || '',
                apellido: usuario.apellido,
                telefono: usuario.telefono,
                rol: 'USER'
              }
            };
            return authResponse;
          })
        );
      }),
      tap(authResponse => {
        console.log('✅ Login exitoso:', authResponse);
        this.setSession(authResponse);
      })
    );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    console.log('📝 Registrando usuario:', userData);
    // El backend espera Usuario con username, no email
    const backendUserData = {
      username: userData.email,
      nombre: userData.nombre,
      apellido: userData.apellido || '',
      telefono: userData.telefono || '',
      password: userData.password,
      email: userData.email
    };
    
    return this.http.post<{token: string}>(`${this.authUrl}/api/v1/register`, backendUserData).pipe(
      switchMap(tokenResponse => {
        // Guardar token temporalmente para la siguiente petición
        const token = tokenResponse.token;
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        
        // Obtener datos completos del usuario recién registrado
        return this.http.get<any>(`${this.authUrl}/actual-usuario`, { headers }).pipe(
          map(usuario => {
            const authResponse: AuthResponse = {
              token: token,
              usuario: {
                id: usuario.id,
                email: usuario.username || usuario.email || '',
                nombre: usuario.nombre || '',
                apellido: usuario.apellido,
                telefono: usuario.telefono,
                rol: 'USER'
              }
            };
            return authResponse;
          })
        );
      }),
      tap(response => {
        console.log('✅ Registro exitoso:', response);
        this.setSession(response);
      })
    );
  }

  logout(): void {
    console.log('🚪 Cerrando sesión...');
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    // Verificar si el token no está expirado
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000; // Convertir a milliseconds
      return Date.now() < expiry;
    } catch (error) {
      console.error('Error verificando token:', error);
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUsuario(): Usuario | null {
    const usuario = localStorage.getItem('usuario');
    if (!usuario || usuario === 'undefined' || usuario === 'null') {
      return null;
    }
    try {
      return JSON.parse(usuario);
    } catch (error) {
      console.error('Error parsing usuario from localStorage:', error);
      return null;
    }
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private setSession(authResponse: AuthResponse): void {
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('usuario', JSON.stringify(authResponse.usuario));
    this.currentUserSubject.next(authResponse.usuario);
  }
}