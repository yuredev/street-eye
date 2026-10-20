import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Comunidade } from '../../../../logic/services/cadastro.service';

export interface CameraDraft { nome: string; comunidadeId: string }

@Component({
  selector: 'app-camera-create-dialog',
  standalone: true,
  imports: [FormsModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>Adicionar câmera</h2>
    <mat-dialog-content>
      <p>Local escolhido no mapa. Dê um nome à câmera e escolha quem pode vê-la.</p>
      <form id="camera-form" (ngSubmit)="salvar()">
        <label for="camera-name">Nome da câmera</label>
        <input id="camera-name" name="nome" [(ngModel)]="nome" required maxlength="255" autofocus />
        <label for="community">Comunidade</label>
        <select id="community" name="comunidade" [(ngModel)]="comunidadeId" required>
          @for (comunidade of data.comunidades; track comunidade.id) {
            <option [value]="comunidade.id">{{ comunidade.nome }}</option>
          }
        </select>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button type="button" (click)="dialog.close()">Cancelar</button>
      <button type="submit" form="camera-form" class="primary">Adicionar câmera</button>
    </mat-dialog-actions>
  `,
  styleUrl: './camera-create-dialog.component.scss'
})
export class CameraCreateDialogComponent {
  nome = '';
  comunidadeId: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { comunidades: Comunidade[] },
    public dialog: MatDialogRef<CameraCreateDialogComponent, CameraDraft>
  ) {
    this.comunidadeId = data.comunidades[0].id;
  }

  salvar(): void {
    const nome = this.nome.trim();
    if (nome && this.comunidadeId) this.dialog.close({ nome, comunidadeId: this.comunidadeId });
  }
}
