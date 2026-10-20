import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../../logic/services/auth/auth.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [ReactiveFormsModule],
})
export class LoginComponent {
  form: FormGroup;
  mensagemDeErro?: string;
  mensagem?: string;
  cadastro = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, route: ActivatedRoute) {
    if (route.snapshot.queryParamMap.get('atualizado') === '1') this.mensagem = 'Perfil atualizado. Entre novamente.';
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.form.valid) {
      const { username, password } = this.form.value;
      if (this.cadastro) {
        this.authService.register(username, password).subscribe({
          next: () => { this.cadastro = false; this.mensagemDeErro = undefined; this.mensagem = 'Conta criada. Entre com seu login.'; },
          error: () => this.mensagemDeErro = 'Não foi possível criar a conta. Verifique se o login já existe.'
        });
        return;
      }
      this.authService
        .login({ login: username, password: password })
        .subscribe({
          next: (response) => {
            localStorage.setItem('authToken', response.token);
            this.router.navigateByUrl('/');
          },
          error: (err) => {
            this.mensagemDeErro =
              'Erro ao efetuar login. Verifique suas credenciais.';
          },
        });
    } else {
      this.form.markAllAsTouched();
    }
  }
}
