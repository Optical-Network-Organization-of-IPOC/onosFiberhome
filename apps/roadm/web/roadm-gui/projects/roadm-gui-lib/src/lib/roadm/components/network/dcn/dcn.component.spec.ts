import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DcnComponent } from './dcn.component';

describe('DcnComponent', () => {
  let component: DcnComponent;
  let fixture: ComponentFixture<DcnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DcnComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DcnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
