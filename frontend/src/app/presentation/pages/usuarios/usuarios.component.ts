import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';
import { CadastroService, Comunidade, Membro, PapelComunidade } from '../../../logic/services/cadastro.service';

@Component({
  selector: 'app-usuarios', standalone: true,
  imports: [FormsModule, RouterLink, HomePageToolbarComponent],
  templateUrl: './usuarios.component.html',
  styleUrls: ['../cadastro-page.scss']
})
export class UsuariosComponent implements OnInit {
  login = '';
  comunidades: Comunidade[] = [];
  comunidadeId = '';
  membros: Membro[] = [];
  loginExistente = '';
  aviso = '';
  erro = '';

  constructor(private cadastro: CadastroService, private router: Router) {}
  ngOnInit(): void {
    if (!localStorage.getItem('authToken')) { this.sair(); return; }
    this.cadastro.eu().subscribe({ next: p => this.login = p.login, error: () => this.sair() });
    this.carregarComunidades();
  }
  sair(): void { localStorage.removeItem('authToken'); this.router.navigateByUrl('/login'); }
  carregarComunidades(): void {
    this.cadastro.comunidades().subscribe({
      next: c => { this.comunidades = c; this.comunidadeId = c.find(x => x.id === this.comunidadeId)?.id ?? c[0]?.id ?? ''; this.carregarMembros(); },
      error: () => this.erro = 'Não foi possível carregar as comunidades.'
    });
  }
  carregarMembros(): void {
    this.membros = [];
    if (!this.comunidadeId) return;
    const id = this.comunidadeId;
    this.cadastro.membros(id).subscribe({
      next: membros => { if (this.comunidadeId === id) this.membros = membros; },
      error: () => { if (this.comunidadeId === id) this.erro = 'Não foi possível carregar os usuários.'; }
    });
  }
  adicionarExistente(): void {
    if (!this.administrador || !this.comunidadeId || !this.loginExistente.trim()) return;
    this.cadastro.adicionarMembro(this.comunidadeId, this.loginExistente.trim()).subscribe({
      next: () => { this.loginExistente = ''; this.carregarMembros(); this.erro = ''; this.aviso = 'Usuário adicionado à comunidade.'; },
      error: erro => this.erro = erro.status === 409 ? 'O usuário já participa da comunidade.' : this.erroGestao(erro.status)
    });
  }
  get administrador(): boolean {
    return this.comunidades.find(c => c.id === this.comunidadeId)?.papel === 'ADMINISTRADOR';
  }
  alterarPapel(membro: Membro): void {
    if (!this.administrador) return;
    const papel: PapelComunidade = membro.papel === 'ADMINISTRADOR' ? 'MEMBRO' : 'ADMINISTRADOR';
    this.cadastro.alterarPapel(this.comunidadeId, membro.id, papel).subscribe({
      next: () => { this.carregarComunidades(); this.erro = ''; this.aviso = 'Papel atualizado.'; },
      error: erro => this.erro = this.erroGestao(erro.status)
    });
  }
  private erroGestao(status: number): string {
    if (status === 403) return 'Somente administradores podem gerenciar esta comunidade.';
    if (status === 404) return 'O usuário ou a comunidade não foi encontrado.';
    if (status === 409) return 'A comunidade precisa de pelo menos um administrador.';
    return 'Não foi possível concluir a operação.';
  }
  remover(usuario: Membro): void {
    if (!this.administrador || !this.comunidadeId || !confirm(`Remover ${usuario.login} desta comunidade?`)) return;
    this.cadastro.removerMembro(this.comunidadeId, usuario.id).subscribe({
      next: () => { this.carregarComunidades(); this.erro = ''; this.aviso = 'Usuário removido da comunidade.'; },
      error: erro => this.erro = this.erroGestao(erro.status)
    });
  }
}
