import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EstadisticasEquipoPage } from './estadisticas-equipo.page';

describe('EstadisticasEquipoPage', () => {
  let component: EstadisticasEquipoPage;
  let fixture: ComponentFixture<EstadisticasEquipoPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(EstadisticasEquipoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
