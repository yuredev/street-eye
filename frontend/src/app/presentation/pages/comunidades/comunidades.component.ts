import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';
import { CadastroService, Comunidade } from '../../../logic/services/cadastro.service';

@Component({
  selector: 'app-comunidades', standalone: true,
  imports: [FormsModule, HomePageToolbarComponent],
  templateUrl: './comunidades.component.html',
  styleUrls: ['../cadastro-page.scss']
})
export class ComunidadesComponent implements OnInit {
  login = '';
  comunidades: Comunidade[] = [];
  editando?: Comunidade;
  nome = '';
  descricao = '';
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
    this.cadastro.comunidades().subscribe({ next: c => this.comunidades = c, error: () => this.erro = 'Não foi possível carregar as comunidades.' });
  }
  editar(comunidade: Comunidade): void {
    this.editando = comunidade; this.nome = comunidade.nome; this.descricao = comunidade.descricao;
    this.aviso = ''; this.erro = '';
  }
  limpar(): void { this.editando = undefined; this.nome = ''; this.descricao = ''; }
  salvar(): void {
    const nome = this.nome.trim();
    if (!nome) return;
    const request: Observable<unknown> = this.editando ?
      this.cadastro.atualizarComunidade(this.editando.id, nome, this.descricao) :
      this.cadastro.criarComunidade(nome, this.descricao);
    request.subscribe({
      next: () => { this.limpar(); this.carregar(); this.erro = ''; this.aviso = 'Comunidade salva.'; },
      error: () => this.erro = 'Não foi possível salvar a comunidade.'
    });
  }
  excluir(comunidade: Comunidade): void {
    if (!confirm(`Excluir a comunidade ${comunidade.nome} e suas câmeras?`)) return;
    this.cadastro.excluirComunidade(comunidade.id).subscribe({
      next: () => { if (this.editando?.id === comunidade.id) this.limpar(); this.carregar(); this.aviso = 'Comunidade excluída.'; this.erro = ''; },
      error: () => this.erro = 'Não foi possível excluir a comunidade.'
    });
  }
}
