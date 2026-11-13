import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Urls } from '../../core/constants/urls';

export interface Usuario { id: string; login: string }
export type PapelComunidade = 'ADMINISTRADOR' | 'MEMBRO';
export interface Membro extends Usuario { papel: PapelComunidade }
export interface Comunidade { id: string; nome: string; descricao: string; papel: PapelComunidade }
export interface CameraCadastro { id: string; nome: string; latitude: number; longitude: number }

@Injectable({ providedIn: 'root' })
export class CadastroService {
  private readonly base = `${Urls.API_BASE_URL}/api`;
  constructor(private http: HttpClient) {}

  private options() {
    return { headers: new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('authToken') ?? ''}` }) };
  }

  eu() { return this.http.get<Usuario>(`${this.base}/usuarios/eu`, this.options()); }
  atualizarUsuario(id: string, login: string, senha: string) {
    return this.http.put<Usuario>(`${this.base}/usuarios/${id}`, { login, senha }, this.options());
  }
  excluirUsuario(id: string) { return this.http.delete<void>(`${this.base}/usuarios/${id}`, this.options()); }

  comunidades() { return this.http.get<Comunidade[]>(`${this.base}/comunidades`, this.options()); }
  criarComunidade(nome: string, descricao: string) {
    return this.http.post<Comunidade>(`${this.base}/comunidades`, { nome, descricao }, this.options());
  }
  atualizarComunidade(id: string, nome: string, descricao: string) {
    return this.http.put<void>(`${this.base}/comunidades/${id}`, { nome, descricao }, this.options());
  }
  excluirComunidade(id: string) { return this.http.delete<void>(`${this.base}/comunidades/${id}`, this.options()); }
  membros(id: string) { return this.http.get<Membro[]>(`${this.base}/comunidades/${id}/usuarios`, this.options()); }
  adicionarMembro(id: string, login: string) {
    return this.http.post<void>(`${this.base}/comunidades/${id}/usuarios`, { login }, this.options());
  }
  removerMembro(id: string, usuarioId: string) {
    return this.http.delete<void>(`${this.base}/comunidades/${id}/usuarios/${usuarioId}`, this.options());
  }

  alterarPapel(id: string, usuarioId: string, papel: PapelComunidade) {
    return this.http.patch<void>(`${this.base}/comunidades/${id}/usuarios/${usuarioId}/papel`, { papel }, this.options());
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
