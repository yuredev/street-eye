import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BotaoPerfilCircularComponent } from './botao-perfil-circular.component';

describe('BotaoPerfilCircularComponent', () => {
  let component: BotaoPerfilCircularComponent;
  let fixture: ComponentFixture<BotaoPerfilCircularComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BotaoPerfilCircularComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BotaoPerfilCircularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
