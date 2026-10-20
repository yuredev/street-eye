import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HomePageToolbarComponent } from '../../components/home/home-page-toolbar/home-page-toolbar.component';
import { CadastroService, Pessoa } from '../../../logic/services/cadastro.service';

@Component({
  selector: 'app-perfil', standalone: true,
  imports: [FormsModule, HomePageToolbarComponent],
  templateUrl: './perfil.component.html',
  styleUrls: ['../cadastro-page.scss']
})
export class PerfilComponent implements OnInit {
  eu?: Pessoa;
  login = '';
  senha = '';
  confirmarSenha = '';
  erro = '';

  constructor(private cadastro: CadastroService, private router: Router) {}
  ngOnInit(): void {
    if (!localStorage.getItem('authToken')) { this.sair(); return; }
    this.cadastro.eu().subscribe({
      next: p => { this.eu = p; this.login = p.login; },
      error: () => this.sair()
    });
  }
  sair(): void { localStorage.removeItem('authToken'); this.router.navigateByUrl('/login'); }
  salvar(): void {
    if (!this.eu || !this.login.trim()) return;
    if (this.senha !== this.confirmarSenha) { this.erro = 'As senhas não coincidem.'; return; }
    this.cadastro.atualizarPessoa(this.eu.id, this.login.trim(), this.senha).subscribe({
      next: () => {
        localStorage.removeItem('authToken');
        this.router.navigateByUrl('/login?atualizado=1');
      },
      error: () => this.erro = 'Não foi possível atualizar o perfil. O login pode já estar em uso.'
    });
  }
}
