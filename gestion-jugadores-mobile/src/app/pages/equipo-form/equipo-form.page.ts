import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonItem, 
  IonLabel, IonInput, IonSelect, IonSelectOption, IonTextarea,
  IonNote, IonSpinner, IonButtons, IonBackButton, AlertController
} from '@ionic/angular/standalone';
import { EquipoService } from '../../core/services/equipo.service';
import { RefreshService } from '../../core/services/refresh.service';
import { Equipo } from '../../core/models/equipo';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-equipo-form',
  templateUrl: './equipo-form.page.html',
  styleUrls: ['./equipo-form.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonItem, 
    IonLabel, IonInput, IonSelect, IonSelectOption, IonTextarea,
    IonNote, IonSpinner, IonButtons, IonBackButton,
    CommonModule, ReactiveFormsModule
  ]
})
export class EquipoFormPage implements OnInit {

  equipoForm!: FormGroup;
  esEdicion: boolean = false;
  equipoId: number | null = null;
  guardando: boolean = false;
  duracionSugerida: number | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private equipoService: EquipoService,
    private refreshService: RefreshService,
    private alertController: AlertController,
    private authService: AuthService
  ) {
    this.crearFormulario();
  }

  ngOnInit() {
    this.verificarEdicion();
  }

  crearFormulario() {
    this.equipoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      tipoFutbol: ['FUTBOL_11', [Validators.required]],
      duracionPartido: [90, [Validators.required, Validators.min(30), Validators.max(120)]]
    });
  }

  verificarEdicion() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== '0') {
      this.esEdicion = true;
      this.equipoId = parseInt(id);
      this.cargarEquipo();
    }
  }

  cargarEquipo() {
    if (this.equipoId) {
      this.equipoService.obtenerEquipoPorId(this.equipoId).subscribe({
        next: (equipo) => {
          this.equipoForm.patchValue({
            nombre: equipo.nombre,
            tipoFutbol: equipo.tipoFutbol,
            duracionPartido: equipo.duracionPartido
          });
          this.actualizarDuracionSugerida(equipo.tipoFutbol);
        },
        error: (error) => {
          console.error('Error cargando equipo:', error);
          this.alertController.create({
            header: 'Error',
            message: 'No se pudo cargar el equipo.',
            buttons: [{
              text: 'OK',
              handler: () => this.router.navigate(['/tabs/equipos'])
            }]
          }).then(alert => alert.present());
        }
      });
    }
  }

  onTipoFutbolChange(event: any) {
    const tipoFutbol = event.detail.value;
    this.actualizarDuracionSugerida(tipoFutbol);
    
    // Auto-completar duración si está vacía
    if (!this.equipoForm.get('duracionPartido')?.value) {
      this.equipoForm.patchValue({
        duracionPartido: this.duracionSugerida
      });
    }
  }

  actualizarDuracionSugerida(tipoFutbol: string) {
    switch (tipoFutbol) {
      case 'FUTBOL_11':
        this.duracionSugerida = 90;
        break;
      case 'FUTBOL_7':
        this.duracionSugerida = 70;
        break;
      case 'FUTBOL_5':
        this.duracionSugerida = 50;
        break;
      default:
        this.duracionSugerida = null;
    }
  }

  async guardarEquipo() {
    if (this.equipoForm.valid) {
      this.guardando = true;
      
      const usuario = this.authService.getUsuario();
      
      if (!usuario || !usuario.id) {
        const alert = await this.alertController.create({
          header: 'Error',
          message: 'No se pudo obtener el usuario autenticado. Por favor, inicia sesión de nuevo.',
          buttons: [{
            text: 'OK',
            handler: () => {
              this.router.navigate(['/login']);
            }
          }]
        });
        await alert.present();
        this.guardando = false;
        return;
      }
      
      const equipoData: any = {
        nombre: this.equipoForm.value.nombre,
        tipoFutbol: this.equipoForm.value.tipoFutbol,
        duracionPartido: parseInt(this.equipoForm.value.duracionPartido)
      };

      console.log('📤 Enviando equipo al backend:', equipoData);
      console.log('👤 Usuario ID:', usuario.id);

      try {
        let resultado;
        if (this.esEdicion && this.equipoId) {
          // TODO: Implementar actualización cuando el backend tenga el endpoint
          console.log('Actualización no implementada aún');
        } else {
          // Crear nuevo equipo
          resultado = await this.equipoService.crearEquipo(equipoData, usuario.id).toPromise();
        }
        
        console.log('✅ Equipo guardado exitosamente:', resultado);
        
        // Notificar que los equipos deben refrescarse
        this.refreshService.refreshEquipos();
        // También refrescar jugadores ya que puede afectar sus equipos
        this.refreshService.refreshJugadores();
        
        const alert = await this.alertController.create({
          header: '¡Éxito!',
          message: `Equipo ${this.esEdicion ? 'actualizado' : 'creado'} correctamente`,
          buttons: [{
            text: 'OK',
            handler: () => {
              // Redirigir a jugadores para que pueda empezar a añadir jugadores
              this.router.navigate(['/tabs/jugadores']);
            }
          }]
        });
        
        await alert.present();
        
      } catch (error: any) {
        console.error('❌ Error guardando equipo:', error);
        
        let mensaje = 'No se pudo guardar el equipo. Inténtalo de nuevo.';
        if (error.status === 401) {
          mensaje = 'Tu sesión ha expirado. Por favor, inicia sesión de nuevo.';
        } else if (error.error?.message) {
          mensaje = error.error.message;
        }
        
        const alert = await this.alertController.create({
          header: 'Error',
          message: mensaje,
          buttons: ['OK']
        });
        
        await alert.present();
      } finally {
        this.guardando = false;
      }
    } else {
      const alert = await this.alertController.create({
        header: 'Formulario incompleto',
        message: 'Por favor, completa todos los campos requeridos.',
        buttons: ['OK']
      });
      await alert.present();
    }
  }

  async eliminarEquipo() {
    const alert = await this.alertController.create({
      header: 'Confirmar eliminación',
      message: '¿Estás seguro de que deseas eliminar este equipo? Esta acción no se puede deshacer.',
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Eliminar',
          role: 'destructive',
          handler: () => {
            console.log('Eliminar equipo:', this.equipoId);
            this.router.navigate(['/tabs/equipos']);
          }
        }
      ]
    });

    await alert.present();
  }

  cancelar() {
    this.router.navigate(['/tabs/equipos']);
  }

  volver() {
    this.router.navigate(['/tabs/equipos']);
  }

}
