export interface AuthRequestDTO {
  login: string;
  password: string;
}

export interface AuthResponseDTO {
  token: string;
}
