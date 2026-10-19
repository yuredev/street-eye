import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePageToolbarComponent } from './home-page-toolbar.component';

describe('HomePageToolbarComponent', () => {
  let component: HomePageToolbarComponent;
  let fixture: ComponentFixture<HomePageToolbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomePageToolbarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomePageToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
