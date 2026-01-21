import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorJugadoresComponent } from './selector-jugadores.component';

describe('SelectorJugadoresComponent', () => {
  let component: SelectorJugadoresComponent;
  let fixture: ComponentFixture<SelectorJugadoresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectorJugadoresComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelectorJugadoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
