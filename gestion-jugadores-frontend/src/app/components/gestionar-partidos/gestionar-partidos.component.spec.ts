import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionarPartidosComponent } from './gestionar-partidos.component';

describe('GestionarPartidosComponent', () => {
  let component: GestionarPartidosComponent;
  let fixture: ComponentFixture<GestionarPartidosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionarPartidosComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionarPartidosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
