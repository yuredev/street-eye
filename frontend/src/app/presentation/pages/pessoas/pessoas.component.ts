import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';
import { CadastroService, Comunidade, Pessoa } from '../../../logic/services/cadastro.service';

@Component({
  selector: 'app-pessoas', standalone: true,
  imports: [FormsModule, RouterLink, HomePageToolbarComponent],
  templateUrl: './pessoas.component.html',
  styleUrls: ['../cadastro-page.scss']
})
export class PessoasComponent implements OnInit {
  login = '';
  comunidades: Comunidade[] = [];
  comunidadeId = '';
  membros: Pessoa[] = [];
  loginExistente = '';
  novoLogin = '';
  novaSenha = '';
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
    this.cadastro.membros(this.comunidadeId).subscribe({ next: p => this.membros = p, error: () => this.erro = 'Não foi possível carregar as pessoas.' });
  }
  adicionarExistente(): void {
    if (!this.comunidadeId || !this.loginExistente.trim()) return;
    this.cadastro.adicionarMembro(this.comunidadeId, this.loginExistente.trim()).subscribe({
      next: () => { this.loginExistente = ''; this.carregarMembros(); this.erro = ''; this.aviso = 'Pessoa adicionada à comunidade.'; },
      error: () => this.erro = 'Não foi possível adicionar. Verifique o login ou se a pessoa já participa.'
    });
  }
  cadastrar(): void {
    if (!this.comunidadeId || !this.novoLogin.trim() || !this.novaSenha) return;
    const login = this.novoLogin.trim();
    this.cadastro.criarPessoa(login, this.novaSenha).subscribe({
      next: () => this.cadastro.adicionarMembro(this.comunidadeId, login).subscribe({
        next: () => { this.novoLogin = ''; this.novaSenha = ''; this.carregarMembros(); this.erro = ''; this.aviso = 'Pessoa cadastrada e adicionada à comunidade.'; },
        error: () => this.erro = 'A conta foi criada, mas não entrou na comunidade. Use “Adicionar pessoa existente”.'
      }),
      error: () => this.erro = 'Não foi possível cadastrar. Esse login pode já estar em uso.'
    });
  }
  remover(pessoa: Pessoa): void {
    if (!this.comunidadeId || !confirm(`Remover ${pessoa.login} desta comunidade?`)) return;
    this.cadastro.removerMembro(this.comunidadeId, pessoa.id).subscribe({
      next: () => { this.carregarComunidades(); this.erro = ''; this.aviso = 'Pessoa removida da comunidade.'; },
      error: () => this.erro = 'Não foi possível remover a pessoa.'
    });
  }
}
