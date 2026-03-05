import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActualizarJugadorComponent } from './actualizar-jugador.component';

describe('ActualizarJugadorComponent', () => {
  let component: ActualizarJugadorComponent;
  let fixture: ComponentFixture<ActualizarJugadorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActualizarJugadorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ActualizarJugadorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
