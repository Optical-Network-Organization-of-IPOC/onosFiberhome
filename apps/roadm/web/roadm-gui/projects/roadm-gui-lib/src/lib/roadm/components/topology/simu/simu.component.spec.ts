import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SimuComponent } from './simu.component';

describe('SimuComponent', () => {
  let component: SimuComponent;
  let fixture: ComponentFixture<SimuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SimuComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SimuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
