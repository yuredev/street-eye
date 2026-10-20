import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ToolbarComponent } from '../../shared/toolbar/toolbar.component';
import { BotaoPerfilCircularComponent } from '../../shared/botao-perfil-circular/botao-perfil-circular.component';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-home-page-toolbar',
  imports: [ToolbarComponent, BotaoPerfilCircularComponent, RouterLink, RouterLinkActive],
  templateUrl: './home-page-toolbar.component.html',
  styleUrl: './home-page-toolbar.component.scss',
})
export class HomePageToolbarComponent {
  @Input() login = '';
  @Output() sair = new EventEmitter<void>();
}
