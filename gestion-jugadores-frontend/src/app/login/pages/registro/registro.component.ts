import { Component, OnInit } from '@angular/core';
import { LoginService } from './../../services/login.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.css']
})
export class RegistroComponent implements OnInit {

  loginData = {
    "username": '',
    "password": '',
  }

  constructor(private snack: MatSnackBar, private loginService: LoginService, private router: Router) { }

  ngOnInit(): void {
  }

  formSubmit() {
    if (this.loginData.username.trim() == '' || this.loginData.username.trim() == null) {
      this.snack.open('El nombre de usuario es requerido !!', 'Aceptar', {
        duration: 3000
      })
      return;
    }
    if (this.loginData.password.trim() == '' || this.loginData.password.trim() == null) {
      this.snack.open('La contrase&ntilde;a es requerida !!', 'Aceptar', {
        duration: 3000
      })
      return;
    }
    this.loginService.generateToken(this.loginData).subscribe(
      (data: any) => {
        console.log(data);
        this.loginService.loginUser(data.token);
        this.loginService.getCurrentUser().subscribe((user: any) => {
          this.loginService.setUser(user);
          console.log(user);

          if (this.loginService.getUserRole() == 'ADMIN') {
            //dashboard admin
            //window.location.href = '/admin';
            this.router.navigate(['admin']);
            this.loginService.loginStatusSubjec.next(true);
          }
          else if (this.loginService.getUserRole() == 'NORMAL') {
            //user dashboard
            //window.location.href = '/user-dashboard';
            this.router.navigate(['jugadores']);
            this.loginService.loginStatusSubjec.next(true);
          }
          else {
            this.loginService.logout();
          }
        })
      }, (error) => {
        console.log(error);
        this.snack.open('Detalles inválidos , vuelva a intentar !!', 'Aceptar', {
          duration: 3000
        })
      }
    )
  }
}
