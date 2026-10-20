import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../../data/models/usuario';
import { StringUtils } from '../../../../logic/utils/string-utils';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-botao-perfil-circular',
  imports: [CommonModule, MatButtonModule, MatMenuModule, RouterLink],
  templateUrl: './botao-perfil-circular.component.html',
  styleUrl: './botao-perfil-circular.component.scss',
})
export class BotaoPerfilCircularComponent implements OnInit {
  @Input() largura: number = 30;
  @Input() altura: number = 30;
  @Input() usuario!: User;
  @Input() cor?: string;
  @Input() corTexto?: string;
  @Output() sair = new EventEmitter<void>();

  iniciaisNomeUsuario!: string;

  ngOnInit(): void {
    this.iniciaisNomeUsuario = StringUtils.getInitials(
      this.usuario.name
    );
  }
}
