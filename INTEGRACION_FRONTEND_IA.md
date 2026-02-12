# 📱 Integración del Módulo IA en Frontend Móvil (Ionic/Angular)

## 📋 Guía de Integración Frontend

Esta guía te muestra cómo consumir los endpoints de IA desde tu aplicación móvil Ionic/Angular.

---

## 1️⃣ Crear Servicio de IA

### Archivo: `src/app/services/ai-analysis.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface InformeJugador {
  jugadorId: number;
  nombreCompleto: string;
  posicion: string;
  temporada: string;
  analisisTecnico: string;
}

export interface InformePartido {
  partidoId: number;
  titulo: string;
  fecha: string;
  resultado: string;
  resumenTactico: string;
  puntosDestacados?: string;
  areasMejora?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AiAnalysisService {

  private apiUrl = `${environment.apiUrl}/ai`;

  constructor(private http: HttpClient) { }

  /**
   * Verifica que el módulo de IA esté operativo
   */
  healthCheck(): Observable<string> {
    return this.http.get(`${this.apiUrl}/health`, { responseType: 'text' });
  }

  /**
   * Genera informe técnico de un jugador con IA
   * @param jugadorId ID del jugador
   * @param temporada Temporada a analizar (default: 2024/2025)
   */
  generarInformeJugador(jugadorId: number, temporada: string = '2024/2025'): Observable<InformeJugador> {
    const params = new HttpParams().set('temporada', temporada);
    return this.http.post<InformeJugador>(
      `${this.apiUrl}/jugador/${jugadorId}/informe`,
      null,
      { params }
    );
  }

  /**
   * Genera informe técnico de un partido con IA
   * @param partidoId ID del partido
   */
  generarInformePartido(partidoId: number): Observable<InformePartido> {
    return this.http.post<InformePartido>(
      `${this.apiUrl}/partido/${partidoId}/informe`,
      null
    );
  }
}
```

---

## 2️⃣ Actualizar Environment

### Archivo: `src/environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

### Archivo: `src/environments/environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tu-dominio.com/api/v1'  // Cambiar por tu URL de producción
};
```

---

## 3️⃣ Integrar en Vista de Jugador

### Ejemplo de Componente

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LoadingController, ToastController, ModalController } from '@ionic/angular';
import { AiAnalysisService, InformeJugador } from '../../services/ai-analysis.service';
import { JugadorService } from '../../services/jugador.service';

@Component({
  selector: 'app-jugador-detalle',
  templateUrl: './jugador-detalle.page.html',
  styleUrls: ['./jugador-detalle.page.scss'],
})
export class JugadorDetallePage implements OnInit {

  jugadorId: number;
  jugador: any;
  informeIA: InformeJugador | null = null;
  cargandoInforme = false;

  constructor(
    private route: ActivatedRoute,
    private jugadorService: JugadorService,
    private aiService: AiAnalysisService,
    private loadingCtrl: LoadingController,
    private toastCtrl: ToastController
  ) {}

  ngOnInit() {
    this.jugadorId = +this.route.snapshot.paramMap.get('id');
    this.cargarJugador();
  }

  cargarJugador() {
    this.jugadorService.obtenerJugadorPorId(this.jugadorId).subscribe({
      next: (data) => {
        this.jugador = data;
      },
      error: (err) => {
        console.error('Error cargando jugador', err);
      }
    });
  }

  /**
   * Genera informe con IA
   */
  async generarInformeIA() {
    const loading = await this.loadingCtrl.create({
      message: 'Generando análisis con IA...',
      spinner: 'crescent'
    });
    await loading.present();

    this.cargandoInforme = true;

    this.aiService.generarInformeJugador(this.jugadorId).subscribe({
      next: async (informe) => {
        this.informeIA = informe;
        this.cargandoInforme = false;
        await loading.dismiss();
        
        // Mostrar toast de éxito
        const toast = await this.toastCtrl.create({
          message: '✅ Informe generado con IA',
          duration: 2000,
          color: 'success',
          position: 'top'
        });
        await toast.present();
      },
      error: async (err) => {
        console.error('Error generando informe', err);
        this.cargandoInforme = false;
        await loading.dismiss();
        
        // Mostrar error
        const toast = await this.toastCtrl.create({
          message: '❌ Error al generar informe',
          duration: 3000,
          color: 'danger',
          position: 'top'
        });
        await toast.present();
      }
    });
  }
}
```

---

## 4️⃣ Template HTML

### Archivo: `jugador-detalle.page.html`

```html
<ion-header>
  <ion-toolbar color="primary">
    <ion-buttons slot="start">
      <ion-back-button></ion-back-button>
    </ion-buttons>
    <ion-title>Detalle Jugador</ion-title>
  </ion-toolbar>
</ion-header>

<ion-content>
  <ion-card *ngIf="jugador">
    <ion-card-header>
      <ion-card-title>{{ jugador.nombre }} {{ jugador.apellido }}</ion-card-title>
      <ion-card-subtitle>{{ jugador.posicion }}</ion-card-subtitle>
    </ion-card-header>
    
    <ion-card-content>
      <!-- Datos del jugador -->
      <ion-list>
        <ion-item>
          <ion-label>Nombre</ion-label>
          <ion-text>{{ jugador.nombre }}</ion-text>
        </ion-item>
        <ion-item>
          <ion-label>Posición</ion-label>
          <ion-text>{{ jugador.posicion }}</ion-text>
        </ion-item>
      </ion-list>

      <!-- Botón para generar informe con IA -->
      <ion-button 
        expand="block" 
        color="secondary"
        (click)="generarInformeIA()"
        [disabled]="cargandoInforme">
        <ion-icon name="sparkles" slot="start"></ion-icon>
        Generar Análisis con IA
      </ion-button>
    </ion-card-content>
  </ion-card>

  <!-- Mostrar informe generado -->
  <ion-card *ngIf="informeIA" class="informe-ia-card">
    <ion-card-header>
      <ion-card-title>
        <ion-icon name="bulb" color="warning"></ion-icon>
        Análisis Técnico (IA)
      </ion-card-title>
      <ion-card-subtitle>Temporada {{ informeIA.temporada }}</ion-card-subtitle>
    </ion-card-header>
    
    <ion-card-content>
      <p class="analisis-texto">{{ informeIA.analisisTecnico }}</p>
      
      <ion-button 
        size="small" 
        fill="clear"
        (click)="informeIA = null">
        Cerrar
      </ion-button>
    </ion-card-content>
  </ion-card>
</ion-content>
```

---

## 5️⃣ Estilos CSS

### Archivo: `jugador-detalle.page.scss`

```scss
.informe-ia-card {
  margin: 16px;
  border-left: 4px solid var(--ion-color-warning);
  
  ion-card-header {
    background: var(--ion-color-light);
    
    ion-card-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
    }
  }
  
  .analisis-texto {
    white-space: pre-wrap;
    line-height: 1.6;
    font-size: 14px;
    color: var(--ion-color-dark);
  }
}

ion-button[disabled] {
  opacity: 0.5;
}
```

---

## 6️⃣ Modal para Informe (Opcional)

Si prefieres mostrar el informe en un modal en lugar de en la misma página:

### Archivo: `informe-modal.component.ts`

```typescript
import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { InformeJugador } from '../../services/ai-analysis.service';

@Component({
  selector: 'app-informe-modal',
  template: `
    <ion-header>
      <ion-toolbar color="secondary">
        <ion-title>
          <ion-icon name="sparkles"></ion-icon>
          Análisis con IA
        </ion-title>
        <ion-buttons slot="end">
          <ion-button (click)="cerrar()">
            <ion-icon name="close"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <h2>{{ informe.nombreCompleto }}</h2>
      <p><strong>Posición:</strong> {{ informe.posicion }}</p>
      <p><strong>Temporada:</strong> {{ informe.temporada }}</p>
      
      <ion-card>
        <ion-card-header>
          <ion-card-title>Análisis Técnico</ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <p class="analisis-texto">{{ informe.analisisTecnico }}</p>
        </ion-card-content>
      </ion-card>
      
      <ion-button expand="block" (click)="cerrar()">
        Cerrar
      </ion-button>
    </ion-content>
  `,
  styles: [`
    .analisis-texto {
      white-space: pre-wrap;
      line-height: 1.6;
    }
  `]
})
export class InformeModalComponent {
  @Input() informe: InformeJugador;

  constructor(private modalCtrl: ModalController) {}

  cerrar() {
    this.modalCtrl.dismiss();
  }
}
```

### Uso del Modal:

```typescript
async abrirModalInforme(informe: InformeJugador) {
  const modal = await this.modalCtrl.create({
    component: InformeModalComponent,
    componentProps: {
      informe: informe
    }
  });
  await modal.present();
}
```

---

## 7️⃣ Integrar en Vista de Partido

Similar al ejemplo de jugador, pero usando el endpoint de partido:

```typescript
async generarInformePartido() {
  const loading = await this.loadingCtrl.create({
    message: 'Generando análisis del partido...',
  });
  await loading.present();

  this.aiService.generarInformePartido(this.partidoId).subscribe({
    next: async (informe) => {
      this.informePartido = informe;
      await loading.dismiss();
      
      const toast = await this.toastCtrl.create({
        message: '✅ Análisis del partido generado',
        duration: 2000,
        color: 'success'
      });
      await toast.present();
    },
    error: async (err) => {
      await loading.dismiss();
      console.error('Error', err);
    }
  });
}
```

---

## 8️⃣ Manejo de Errores Avanzado

### Interceptor para manejar errores (opcional)

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastController } from '@ionic/angular';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private toastCtrl: ToastController) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let mensaje = 'Error desconocido';
        
        if (error.status === 404) {
          mensaje = 'Recurso no encontrado';
        } else if (error.status === 500) {
          mensaje = 'Error del servidor';
        } else if (error.error?.mensaje) {
          mensaje = error.error.mensaje;
        }
        
        this.mostrarError(mensaje);
        return throwError(() => error);
      })
    );
  }

  async mostrarError(mensaje: string) {
    const toast = await this.toastCtrl.create({
      message: mensaje,
      duration: 3000,
      color: 'danger',
      position: 'top'
    });
    await toast.present();
  }
}
```

Registrar en `app.module.ts`:

```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ErrorInterceptor } from './interceptors/error.interceptor';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

---

## 9️⃣ UI/UX Recomendaciones

### 💡 Indicadores de Carga

```html
<!-- Skeleton mientras carga -->
<ion-card *ngIf="cargandoInforme">
  <ion-card-content>
    <ion-skeleton-text animated style="width: 100%; height: 20px;"></ion-skeleton-text>
    <ion-skeleton-text animated style="width: 80%; height: 20px;"></ion-skeleton-text>
    <ion-skeleton-text animated style="width: 90%; height: 20px;"></ion-skeleton-text>
  </ion-card-content>
</ion-card>
```

### 💡 Animaciones

```scss
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.informe-ia-card {
  animation: fadeIn 0.5s ease-out;
}
```

### 💡 Empty State

```html
<div *ngIf="!informeIA && !cargandoInforme" class="empty-state">
  <ion-icon name="sparkles" color="medium"></ion-icon>
  <p>Genera un análisis técnico con IA</p>
  <ion-button size="small" (click)="generarInformeIA()">
    Generar ahora
  </ion-button>
</div>
```

---

## 🔟 Testing del Servicio

### Archivo: `ai-analysis.service.spec.ts`

```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AiAnalysisService } from './ai-analysis.service';

describe('AiAnalysisService', () => {
  let service: AiAnalysisService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AiAnalysisService]
    });
    service = TestBed.inject(AiAnalysisService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should generate player report', () => {
    const mockInforme = {
      jugadorId: 1,
      nombreCompleto: 'Test Player',
      posicion: 'MC',
      temporada: '2024/2025',
      analisisTecnico: 'Test analysis'
    };

    service.generarInformeJugador(1).subscribe(informe => {
      expect(informe).toEqual(mockInforme);
    });

    const req = httpMock.expectOne(req => req.url.includes('/ai/jugador/1/informe'));
    expect(req.request.method).toBe('POST');
    req.flush(mockInforme);
  });
});
```

---

## 📦 Resumen de Archivos Creados

```
src/app/
├── services/
│   └── ai-analysis.service.ts       ✅ Servicio de IA
├── pages/
│   └── jugador-detalle/
│       ├── jugador-detalle.page.ts   ✅ Lógica del componente
│       ├── jugador-detalle.page.html ✅ Template
│       └── jugador-detalle.page.scss ✅ Estilos
└── components/
    └── informe-modal/
        └── informe-modal.component.ts ✅ Modal (opcional)
```

---

## ✅ Checklist de Integración

- [ ] Crear servicio `ai-analysis.service.ts`
- [ ] Configurar `environment.ts` con API URL
- [ ] Añadir botón "Generar Análisis IA" en vista
- [ ] Implementar lógica de carga (loading)
- [ ] Mostrar informe generado
- [ ] Manejar errores con toast
- [ ] Añadir estilos CSS
- [ ] Probar con backend corriendo
- [ ] (Opcional) Crear modal para informe
- [ ] (Opcional) Añadir tests

---

## 🚀 Próximos Pasos

1. Implementa el servicio
2. Añade el botón en las vistas relevantes
3. Prueba con backend corriendo
4. Ajusta UI/UX según tu diseño
5. ¡Disfruta de IA en tu app móvil! 🎉

---

## 📚 Recursos

- [Ionic Components](https://ionicframework.com/docs/components)
- [Angular HttpClient](https://angular.io/guide/http)
- [RxJS Operators](https://rxjs.dev/guide/operators)

---

**¡Tu app móvil ya puede usar IA! 🧠📱**
