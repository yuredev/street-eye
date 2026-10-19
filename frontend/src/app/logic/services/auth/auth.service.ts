import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Urls } from '../../../core/constants/urls';
import { AuthRequestDTO, AuthResponseDTO } from '../../../data/models/auth-dtos';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private httpClient: HttpClient) {}

  login(reqDTO: AuthRequestDTO): Observable<AuthResponseDTO> {
    return this.httpClient.post<AuthResponseDTO>(
      `${Urls.API_BASE_URL}/login`,
      reqDTO
    );
  }
}
