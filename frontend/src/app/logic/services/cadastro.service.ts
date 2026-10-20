import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Urls } from '../../core/constants/urls';

export interface Pessoa { id: string; login: string }
export interface Comunidade { id: string; nome: string; descricao: string }
export interface CameraCadastro { id: string; nome: string; latitude: number; longitude: number }

@Injectable({ providedIn: 'root' })
export class CadastroService {
  private readonly base = `${Urls.API_BASE_URL}/api`;
  constructor(private http: HttpClient) {}

  private options() {
    return { headers: new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('authToken') ?? ''}` }) };
  }

  eu() { return this.http.get<Pessoa>(`${this.base}/pessoas/eu`, this.options()); }
  criarPessoa(login: string, senha: string) {
    return this.http.post<Pessoa>(`${this.base}/pessoas`, { login, senha }, this.options());
  }
  atualizarPessoa(id: string, login: string, senha: string) {
    return this.http.put<Pessoa>(`${this.base}/pessoas/${id}`, { login, senha }, this.options());
  }
  excluirPessoa(id: string) { return this.http.delete<void>(`${this.base}/pessoas/${id}`, this.options()); }

  comunidades() { return this.http.get<Comunidade[]>(`${this.base}/comunidades`, this.options()); }
  criarComunidade(nome: string, descricao: string) {
    return this.http.post<Comunidade>(`${this.base}/comunidades`, { nome, descricao }, this.options());
  }
  atualizarComunidade(id: string, nome: string, descricao: string) {
    return this.http.put<void>(`${this.base}/comunidades/${id}`, { nome, descricao }, this.options());
  }
  excluirComunidade(id: string) { return this.http.delete<void>(`${this.base}/comunidades/${id}`, this.options()); }
  membros(id: string) { return this.http.get<Pessoa[]>(`${this.base}/comunidades/${id}/pessoas`, this.options()); }
  adicionarMembro(id: string, login: string) {
    return this.http.post<void>(`${this.base}/comunidades/${id}/pessoas`, { login }, this.options());
  }
  removerMembro(id: string, pessoaId: string) {
    return this.http.delete<void>(`${this.base}/comunidades/${id}/pessoas/${pessoaId}`, this.options());
  }

  cameras(id: string) { return this.http.get<CameraCadastro[]>(`${this.base}/comunidades/${id}/cameras`, this.options()); }
  criarCamera(id: string, camera: Omit<CameraCadastro, 'id'>) {
    return this.http.post<CameraCadastro>(`${this.base}/comunidades/${id}/cameras`, camera, this.options());
  }
  atualizarCamera(id: string, camera: CameraCadastro) {
    return this.http.put<void>(`${this.base}/comunidades/${id}/cameras/${camera.id}`, camera, this.options());
  }
  excluirCamera(id: string, cameraId: string) {
    return this.http.delete<void>(`${this.base}/comunidades/${id}/cameras/${cameraId}`, this.options());
  }
}
