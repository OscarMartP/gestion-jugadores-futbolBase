import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then( m => m.LoginPage)
  },
  {
    path: 'home',
    loadComponent: () => import('./pages/home/home.page').then( m => m.HomePage)
  },
  {
    path: 'tabs',
    loadComponent: () => import('./pages/tabs/tabs.page').then( m => m.TabsPage),
    children: [
      {
        path: 'jugadores',
        loadComponent: () => import('./pages/jugadores/jugadores.page').then( m => m.JugadoresPage)
      },
      {
        path: 'equipos',
        loadComponent: () => import('./pages/equipos/equipos.page').then( m => m.EquiposPage)
      },
      {
        path: 'partidos',
        loadComponent: () => import('./pages/partidos/partidos.page').then( m => m.PartidosPage)
      },
      {
        path: 'estadisticas',
        loadComponent: () => import('./pages/estadisticas/estadisticas.page').then( m => m.EstadisticasPage)
      },
      {
        path: '',
        redirectTo: 'jugadores',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'jugadores',
    redirectTo: 'tabs/jugadores',
    pathMatch: 'full'
  },
  {
    path: 'equipos',
    redirectTo: 'tabs/equipos',
    pathMatch: 'full'
  },
  {
    path: 'partidos',
    redirectTo: 'tabs/partidos',
    pathMatch: 'full'
  },
  {
    path: 'estadisticas',
    redirectTo: 'tabs/estadisticas',
    pathMatch: 'full'
  },
  {
    path: 'jugador-form/:id',
    loadComponent: () => import('./pages/jugador-form/jugador-form.page').then( m => m.JugadorFormPage)
  },
  {
    path: 'equipo-form/:id',
    loadComponent: () => import('./pages/equipo-form/equipo-form.page').then( m => m.EquipoFormPage)
  }
];
