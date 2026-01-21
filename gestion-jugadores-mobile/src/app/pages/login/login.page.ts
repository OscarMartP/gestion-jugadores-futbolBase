import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { 
  IonContent, IonButton, IonIcon, IonInput, IonItem, IonSpinner, 
  ToastController, LoadingController 
} from '@ionic/angular/standalone';
import { AuthService } from '../../core/services/auth.service';
import { LoginRequest, RegisterRequest } from '../../core/models/auth';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonButton, IonIcon, IonInput, IonItem, IonSpinner,
    CommonModule, ReactiveFormsModule
  ]
})
export class LoginPage implements OnInit {

  loginForm!: FormGroup;
  registerForm!: FormGroup;
  
  showRegister = false;
  showPassword = false;
  showRegisterPassword = false;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastController: ToastController,
    private loadingController: LoadingController
  ) {}

  ngOnInit() {
    this.initForms();
  }

  initForms() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      apellido: [''],
      email: ['', [Validators.required, Validators.email]],
      telefono: [''],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  async onLogin() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      const loading = await this.loadingController.create({
        message: 'Iniciando sesión...'
      });
      await loading.present();

      const credentials: LoginRequest = this.loginForm.value;
      
      this.authService.login(credentials).subscribe({
        next: async (response) => {
          console.log('✅ Login exitoso:', response);
          await loading.dismiss();
          this.isLoading = false;
          
          const toast = await this.toastController.create({
            message: `¡Bienvenido ${response.usuario.nombre}!`,
            duration: 2000,
            color: 'success'
          });
          await toast.present();
          
          this.router.navigate(['/home']);
        },
        error: async (error) => {
          console.error('❌ Error en login:', error);
          await loading.dismiss();
          this.isLoading = false;
          
          const toast = await this.toastController.create({
            message: 'Error de autenticación. Verifica tus credenciales.',
            duration: 3000,
            color: 'danger'
          });
          await toast.present();
        }
      });
    }
  }

  async onRegister() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      const loading = await this.loadingController.create({
        message: 'Creando cuenta...'
      });
      await loading.present();

      const userData: RegisterRequest = this.registerForm.value;
      
      this.authService.register(userData).subscribe({
        next: async (response) => {
          console.log('✅ Registro exitoso:', response);
          await loading.dismiss();
          this.isLoading = false;
          
          const toast = await this.toastController.create({
            message: `¡Cuenta creada! Bienvenido ${response.usuario.nombre}`,
            duration: 2000,
            color: 'success'
          });
          await toast.present();
          
          this.router.navigate(['/home']);
        },
        error: async (error) => {
          console.error('❌ Error en registro:', error);
          await loading.dismiss();
          this.isLoading = false;
          
          const toast = await this.toastController.create({
            message: 'Error al crear la cuenta. Intenta nuevamente.',
            duration: 3000,
            color: 'danger'
          });
          await toast.present();
        }
      });
    }
  }

  async loginDemo() {
    console.log('🎮 Login demo activado');
    const loading = await this.loadingController.create({
      message: 'Conectando con datos demo...'
    });
    await loading.present();

    // Simular usuario demo
    const demoUser = {
      id: 1,
      nombre: 'Usuario Demo',
      email: 'demo@futbolbase.com',
      rol: 'ENTRENADOR'
    };
    
    const demoToken = 'demo-token-' + Date.now();
    
    // Guardar datos demo
    localStorage.setItem('token', demoToken);
    localStorage.setItem('usuario', JSON.stringify(demoUser));
    
    setTimeout(async () => {
      await loading.dismiss();
      
      const toast = await this.toastController.create({
        message: '¡Demo activado! Navegando a la app...',
        duration: 2000,
        color: 'success'
      });
      await toast.present();
      
      this.router.navigate(['/home']);
    }, 1500);
  }

  toggleForm() {
    this.showRegister = !this.showRegister;
    this.loginForm.reset();
    this.registerForm.reset();
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleRegisterPassword() {
    this.showRegisterPassword = !this.showRegisterPassword;
  }

  isFieldInvalid(field: string): boolean {
    const control = this.loginForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  isRegisterFieldInvalid(field: string): boolean {
    const control = this.registerForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
