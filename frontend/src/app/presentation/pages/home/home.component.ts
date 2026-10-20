import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { MapComponent } from '../../components/home/map/map.component';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';
import { CadastroService, CameraCadastro, Comunidade } from '../../../logic/services/cadastro.service';
import { CameraCreateDialogComponent, CameraDraft } from '../../components/home/camera-create-dialog/camera-create-dialog.component';
import { CameraViewModalComponent } from '../../components/home/camera-view-modal/camera-view-modal.component';

@Component({
  selector: 'app-home',
  imports: [MapComponent, HomePageToolbarComponent, RouterLink],
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  login = '';
  cameras: CameraCadastro[] = [];
  comunidades: Comunidade[] = [];
  comunidadesCarregadas = false;
  erro = '';
  aviso = '';
  modoAdicionar = false;

  constructor(private cadastro: CadastroService, private router: Router, private route: ActivatedRoute, private dialog: MatDialog) {}

  ngOnInit(): void {
    if (!localStorage.getItem('authToken')) { this.router.navigateByUrl('/login'); return; }
    this.modoAdicionar = this.route.snapshot.queryParamMap.get('adicionar') === '1';
    this.cadastro.eu().subscribe({
      next: pessoa => this.login = pessoa.login,
      error: () => this.sair()
    });
    this.carregarCameras();
  }

  carregarCameras(): void {
    this.cadastro.comunidades().subscribe({
      next: comunidades => {
        this.comunidades = comunidades;
        this.comunidadesCarregadas = true;
        if (!comunidades.length) { this.cameras = []; return; }
        forkJoin(comunidades.map(c => this.cadastro.cameras(c.id))).subscribe({
          next: grupos => this.cameras = grupos.flat(),
          error: () => this.erro = 'Não foi possível carregar as câmeras.'
        });
      },
      error: () => this.erro = 'Não foi possível carregar as comunidades.'
    });
  }

  adicionarNoLocal(local: { latitude: number; longitude: number }): void {
    if (!this.modoAdicionar) return;
    this.erro = ''; this.aviso = '';
    if (!this.comunidadesCarregadas) return;
    if (!this.comunidades.length) {
      this.erro = 'Crie uma comunidade antes de adicionar uma câmera.';
      return;
    }
    this.dialog.open<CameraCreateDialogComponent, { comunidades: Comunidade[] }, CameraDraft>(
      CameraCreateDialogComponent, { data: { comunidades: this.comunidades }, width: '420px', maxWidth: '95vw' }
    ).afterClosed().subscribe(draft => {
      if (!draft) return;
      this.cadastro.criarCamera(draft.comunidadeId, { nome: draft.nome, ...local }).subscribe({
        next: () => { this.encerrarAdicao(); this.aviso = 'Câmera adicionada ao mapa.'; this.carregarCameras(); },
        error: () => this.erro = 'Não foi possível adicionar a câmera.'
      });
    });
  }

  iniciarAdicao(): void {
    this.erro = ''; this.aviso = '';
    if (!this.comunidades.length) {
      this.erro = 'Crie uma comunidade antes de adicionar uma câmera.';
      return;
    }
    this.modoAdicionar = true;
  }

  encerrarAdicao(): void {
    this.modoAdicionar = false;
    this.router.navigate([], { relativeTo: this.route, queryParams: { adicionar: null }, queryParamsHandling: 'merge' });
  }

  visualizar(camera: CameraCadastro): void {
    if (this.modoAdicionar) return;
    this.dialog.open(CameraViewModalComponent, {
      data: camera, width: '900px', maxWidth: '95vw'
    });
  }

  sair(): void { localStorage.removeItem('authToken'); this.router.navigateByUrl('/login'); }
}
