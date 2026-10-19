import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CameraViewModalComponent } from './camera-view-modal.component';

describe('CameraViewModalComponent', () => {
  let component: CameraViewModalComponent;
  let fixture: ComponentFixture<CameraViewModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CameraViewModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CameraViewModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
