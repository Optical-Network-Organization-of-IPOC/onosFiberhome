import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Fiberhome1Component } from './fiberhome1.component';

describe('Fiberhome1Component', () => {
  let component: Fiberhome1Component;
  let fixture: ComponentFixture<Fiberhome1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Fiberhome1Component ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Fiberhome1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
