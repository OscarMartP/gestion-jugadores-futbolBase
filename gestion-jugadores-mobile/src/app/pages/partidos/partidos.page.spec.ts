import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PartidosPage } from './partidos.page';

describe('PartidosPage', () => {
  let component: PartidosPage;
  let fixture: ComponentFixture<PartidosPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(PartidosPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
