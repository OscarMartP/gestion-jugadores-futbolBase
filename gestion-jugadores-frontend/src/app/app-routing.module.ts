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
import { SelectorJugadoresComponent } from './graficos/selector-jugadores/selector-jugadores.component';



//RUTAS APLICACION
const routes: Routes = [
  { path: 'jugadores', component: ListaJugadoresComponent },
  {
    path: '',
    component: HomeComponent,
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
    path:'admin',
    component:DashboardComponent,
    
    children:[
      {
        path:'profile',
        component:ProfileComponent
      }
    ]
  },
  {
    path:'user-dashboard',
    component:UserDashboardComponent,
    pathMatch:'full',
    
  },
  { path: 'crear-equipo', component: CrearEquipoComponent },
  { path: 'crear-partido', component: PartidoCrearComponent },
  { path: 'modo-partido/:equipoId', component: PartidoModoComponent },
  { path: 'selector-jugadores/:equipoId', component: SelectorJugadoresComponent }


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
