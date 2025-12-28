import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Componentes
import { ListaJugadoresComponent } from './lista-jugadores/lista-jugadores.component';
import { RegistrarJugadorComponent } from './registrar-jugador/registrar-jugador.component';
import { ActualizarJugadorComponent } from './actualizar-jugador/actualizar-jugador.component';
import { JugadorDetallesComponent } from './jugador-detalles/jugador-detalles.component';
import { NavbarComponent } from './login/components/navbar/navbar.component';
import { SignupComponent } from './login/pages/signup/signup.component';
import { HomeComponent } from './login/pages/home/home.component';
import { RegistroComponent } from './login/pages/registro/registro.component';
import { DashboardComponent } from './login/pages/admin/dashboard/dashboard.component';
import { UserDashboardComponent } from './login/pages/user/user-dashboard/user-dashboard.component';
import { SidebarComponent } from './login/pages/admin/sidebar/sidebar.component';
import { ProfileComponent } from './login/pages/profile/profile.component';
import { CrearEquipoComponent } from './components/crear-equipo/crear-equipo.component';



// Angular Material
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


// Servicios
import { authInterceptorProviders } from './login/services/auth.interceptor';
import { EquipoService } from './equipo.service';
import { PartidoCrearComponent } from './partido-crear.component';
import { PartidoModoComponent } from './partido-modo.component';
import { SelectorJugadoresComponent } from './graficos/selector-jugadores/selector-jugadores.component';
import { GestionarPartidosComponent } from './components/gestionar-partidos/gestionar-partidos.component';
import { HistorialPartidosComponent } from './historial-partidos/historial-partidos.component';
import { EstadisticasEquipoComponent } from './components/estadisticas-equipo/estadisticas-equipo.component';
import { EstadisticasGeneralesComponent } from './components/estadisticas-generales/estadisticas-generales.component';

@NgModule({
  declarations: [
    AppComponent,
    ListaJugadoresComponent,
    RegistrarJugadorComponent,
    ActualizarJugadorComponent,
    JugadorDetallesComponent,
    NavbarComponent,
    SignupComponent,
    HomeComponent,
    RegistroComponent,
    DashboardComponent,
    UserDashboardComponent,
    SidebarComponent,
    ProfileComponent,
    CrearEquipoComponent,
    PartidoCrearComponent,
    PartidoModoComponent,
    SelectorJugadoresComponent,
    GestionarPartidosComponent,
    HistorialPartidosComponent,
    EstadisticasEquipoComponent,
    EstadisticasGeneralesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    RouterModule.forRoot([]),

    // Angular Material
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  providers: [
    provideAnimationsAsync(),
    authInterceptorProviders,
    EquipoService,
    provideClientHydration()
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
