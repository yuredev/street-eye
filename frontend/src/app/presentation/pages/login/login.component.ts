import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../../logic/services/auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [ReactiveFormsModule],
})
export class LoginComponent {
  form: FormGroup;
  mensagemDeErro?: string;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.form.valid) {
      const { username, password } = this.form.value;
      console.log('Login:', username, password);
      this.authService
        .login({ login: username, password: password })
        .subscribe({
          next: (response) => {
            console.log('Login successful, token:', response.token);
            localStorage.setItem('authToken', response.token);
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
