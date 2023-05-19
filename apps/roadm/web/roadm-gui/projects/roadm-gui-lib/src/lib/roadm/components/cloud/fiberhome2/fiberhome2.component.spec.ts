import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Fiberhome2Component } from './fiberhome2.component';

describe('Fiberhome2Component', () => {
  let component: Fiberhome2Component;
  let fixture: ComponentFixture<Fiberhome2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Fiberhome2Component ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Fiberhome2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
