import { Component } from '@angular/core';
import { ToolbarComponent } from '../../shared/toolbar/toolbar.component';
import { BotaoPerfilCircularComponent } from '../../shared/botao-perfil-circular/botao-perfil-circular.component';

@Component({
  selector: 'app-home-page-toolbar',
  imports: [ToolbarComponent, BotaoPerfilCircularComponent],
  templateUrl: './home-page-toolbar.component.html',
  styleUrl: './home-page-toolbar.component.scss',
})
export class HomePageToolbarComponent {}
