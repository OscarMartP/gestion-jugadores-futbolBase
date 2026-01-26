import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EstadisticasPartidoPage } from './estadisticas-partido.page';

describe('EstadisticasPartidoPage', () => {
  let component: EstadisticasPartidoPage;
  let fixture: ComponentFixture<EstadisticasPartidoPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(EstadisticasPartidoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
