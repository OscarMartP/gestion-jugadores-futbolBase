import { NgModule } from '@angular/core';
import { RouterModule, Routes, PreloadAllModules } from '@angular/router';
import { AuthGuard, GuestGuard } from './services/auth.guard';

// RUTAS PRINCIPALES DE LA APLICACIÓN
const routes: Routes = [
  // Ruta inicial - redirige según autenticación
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  
  // Login - solo para usuarios no autenticados
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then(m => m.LoginPageModule),
    canActivate: [GuestGuard]
  },
  
  // Tabs principales - solo para usuarios autenticados
  {
    path: 'tabs',
    loadChildren: () => import('./tabs/tabs.module').then(m => m.TabsPageModule),
    canActivate: [AuthGuard]
  },
  
  // Perfil - solo para usuarios autenticados
  {
    path: 'perfil',
    loadChildren: () => import('./pages/perfil/perfil.module').then(m => m.PerfilPageModule),
    canActivate: [AuthGuard]
  },

  
  // Formularios - solo para usuarios autenticados
  {
    path: 'jugador-form',
    loadChildren: () => import('./pages/jugador-form/jugador-form.module').then(m => m.JugadorFormPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'jugador-form/:id',
    loadChildren: () => import('./pages/jugador-form/jugador-form.module').then(m => m.JugadorFormPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'equipo-form',
    loadChildren: () => import('./pages/equipo-form/equipo-form.module').then(m => m.EquipoFormPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'equipo-form/:id',
    loadChildren: () => import('./pages/equipo-form/equipo-form.module').then(m => m.EquipoFormPageModule),
    canActivate: [AuthGuard]
  },

  // Páginas no autorizadas
  {
    path: 'unauthorized',
    loadChildren: () => import('./pages/unauthorized/unauthorized.module').then(m => m.UnauthorizedPageModule)
  },

  // Wildcard route - redirigir a login si no se encuentra la ruta
  {
    path: '**',
    redirectTo: '/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    preloadingStrategy: PreloadAllModules,
    enableTracing: false // Cambiar a true solo para debugging
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
