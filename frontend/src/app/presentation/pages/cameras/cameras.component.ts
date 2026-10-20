import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';
import { CadastroService, CameraCadastro, Comunidade } from '../../../logic/services/cadastro.service';

interface CameraItem { camera: CameraCadastro; comunidade: Comunidade }

@Component({
  selector: 'app-cameras', standalone: true,
  imports: [FormsModule, RouterLink, HomePageToolbarComponent],
  templateUrl: './cameras.component.html',
  styleUrls: ['../cadastro-page.scss']
})
export class CamerasComponent implements OnInit {
  login = '';
  items: CameraItem[] = [];
  temComunidades = false;
  editandoId = '';
  nomeEdicao = '';
  aviso = '';
  erro = '';

  constructor(private cadastro: CadastroService, private router: Router) {}
  ngOnInit(): void {
    if (!localStorage.getItem('authToken')) { this.sair(); return; }
    this.cadastro.eu().subscribe({ next: p => this.login = p.login, error: () => this.sair() });
    this.carregar();
  }
  sair(): void { localStorage.removeItem('authToken'); this.router.navigateByUrl('/login'); }
  carregar(): void {
    this.cadastro.comunidades().subscribe({
      next: comunidades => {
        this.temComunidades = comunidades.length > 0;
        if (!comunidades.length) { this.items = []; return; }
        forkJoin(comunidades.map(c => this.cadastro.cameras(c.id))).subscribe({
          next: grupos => this.items = grupos.flatMap((grupo, index) => grupo.map(camera => ({ camera, comunidade: comunidades[index] }))),
          error: () => this.erro = 'Não foi possível carregar as câmeras.'
        });
      },
      error: () => this.erro = 'Não foi possível carregar as comunidades.'
    });
  }
  editar(item: CameraItem): void { this.editandoId = item.camera.id; this.nomeEdicao = item.camera.nome; }
  cancelar(): void { this.editandoId = ''; this.nomeEdicao = ''; }
  salvar(item: CameraItem): void {
    const nome = this.nomeEdicao.trim();
    if (!nome) return;
    this.cadastro.atualizarCamera(item.comunidade.id, { ...item.camera, nome }).subscribe({
      next: () => { this.cancelar(); this.carregar(); this.erro = ''; this.aviso = 'Câmera atualizada.'; },
      error: () => this.erro = 'Não foi possível atualizar a câmera.'
    });
  }
  excluir(item: CameraItem): void {
    if (!confirm(`Excluir a câmera ${item.camera.nome}?`)) return;
    this.cadastro.excluirCamera(item.comunidade.id, item.camera.id).subscribe({
      next: () => { this.carregar(); this.erro = ''; this.aviso = 'Câmera excluída.'; },
      error: () => this.erro = 'Não foi possível excluir a câmera.'
    });
  }
}
