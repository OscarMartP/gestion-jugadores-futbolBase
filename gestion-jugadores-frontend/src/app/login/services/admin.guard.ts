import { Injectable } from "@angular/core";
import { LoginService } from './login.service';
import { Observable } from 'rxjs';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

@Injectable({
    providedIn: 'root'
  })
  export class AdminGuard implements CanActivate {
  
    constructor(private loginService:LoginService,private router:Router){
  
    }
  
    canActivate(
      route: ActivatedRouteSnapshot,
      state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      if(this.loginService.isLoggedIn() && this.loginService.getUserRole() == 'ADMIN'){
        return true;
      }
  
      this.router.navigate(['registro']);
      return false;
    }
  
  }