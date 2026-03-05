import { Component, OnInit } from '@angular/core';
import { Jugador } from '../jugador';
import { ActivatedRoute } from '@angular/router';
import { JugadorService } from '../jugador.service';

@Component({
  selector: 'app-jugador-detalles',
  templateUrl: './jugador-detalles.component.html',
  styleUrl: './jugador-detalles.component.css'
})
export class JugadorDetallesComponent implements OnInit {

  id:number;
  jugador:Jugador;
  constructor(private route:ActivatedRoute,private jugadorServicio:JugadorService) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.jugador = new Jugador();
    this.jugadorServicio.obtenerJugadorPorId(this.id).subscribe(dato => {
      this.jugador = dato;
      //swal(`Detalles del empleado ${this.empleado.nombre}`);
    });
  }

}
