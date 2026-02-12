import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  
  if (token) {
    // Clonar la petición y agregar el header de autorización
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    console.log('🔒 Token agregado a la petición:', req.url);
    return next(clonedReq);
  }
  
  return next(req);
};
