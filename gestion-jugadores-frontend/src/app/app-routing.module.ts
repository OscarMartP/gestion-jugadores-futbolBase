import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegistrarJugadorComponent } from './registrar-jugador/registrar-jugador.component';
import { ActualizarJugadorComponent } from './actualizar-jugador/actualizar-jugador.component';
import { JugadorDetallesComponent } from './jugador-detalles/jugador-detalles.component';
import { ListaJugadoresComponent } from './lista-jugadores/lista-jugadores.component';
import { SignupComponent } from './login/pages/signup/signup.component';
import { HomeComponent } from './login/pages/home/home.component';
import { RegistroComponent } from './login/pages/registro/registro.component';
import { DashboardComponent } from './login/pages/admin/dashboard/dashboard.component';
import { UserDashboardComponent } from './login/pages/user/user-dashboard/user-dashboard.component';
import { ProfileComponent } from './login/pages/profile/profile.component';
import { CrearEquipoComponent } from './components/crear-equipo/crear-equipo.component';
import { PartidoCrearComponent } from './partido-crear.component';
import { PartidoModoComponent } from './partido-modo.component';
import { GestionarPartidosComponent } from './components/gestionar-partidos/gestionar-partidos.component';
import { SelectorJugadoresComponent } from './graficos/selector-jugadores/selector-jugadores.component';
import { EstadisticasEquipoComponent } from './components/estadisticas-equipo/estadisticas-equipo.component';
import { EstadisticasGeneralesComponent } from './components/estadisticas-generales/estadisticas-generales.component';
import { SeleccionAlineacionComponent } from './components/seleccion-alineacion/seleccion-alineacion.component';

// RUTAS APLICACION
const routes: Routes = [
  { path: 'jugadores', component: ListaJugadoresComponent },
  {
    path: '',
    redirectTo: 'admin',
    pathMatch: 'full'
  },
  {
    path: 'signup',
    component: SignupComponent,
    pathMatch: 'full'
  },
  {
    path: 'registro',
    component: RegistroComponent,
    pathMatch: 'full'
  },
  { path: 'registrar-jugador', component: RegistrarJugadorComponent },
  { path: 'actualizar-jugador/:id', component: ActualizarJugadorComponent },
  { path: 'jugador-detalles/:id', component: JugadorDetallesComponent },
  {
    path: 'admin',
    component: DashboardComponent,
    children: [
      {
        path: 'profile',
        component: ProfileComponent
      }
    ]
  },
  {
    path: 'user-dashboard',
    component: UserDashboardComponent,
    pathMatch: 'full'
  },
  
  // ===== RUTAS DE PARTIDOS =====
  { path: 'crear-equipo', component: CrearEquipoComponent },
  { path: 'crear-partido', component: PartidoCrearComponent },
  { path: 'seleccion-alineacion/:partidoId/:equipoId', component: SeleccionAlineacionComponent },
  { path: 'iniciar-partido', component: PartidoModoComponent },
  { path: 'gestionar-partidos', component: GestionarPartidosComponent },
  { path: 'selector-jugadores/:equipoId', component: SelectorJugadoresComponent },
  
  // ===== RUTAS DE ESTADÍSTICAS =====
  { path: 'estadisticas/:id', component: EstadisticasEquipoComponent },
  { path: 'estadisticas-generales', component: EstadisticasGeneralesComponent },
  
  // Rutas antiguas (mantener para compatibilidad)
  { path: 'modo-partido/:equipoId', component: PartidoModoComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
