package yuredev.street_eye_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import yuredev.street_eye_backend.config.TokenService;
import yuredev.street_eye_backend.models.Usuario;
import yuredev.street_eye_backend.models.PapelUsuario;
import yuredev.street_eye_backend.models.dto.RegisterDTO;
import yuredev.street_eye_backend.models.dto.auth.AuthDTO;
import yuredev.street_eye_backend.models.dto.auth.ResponseAuthDTO;
import yuredev.street_eye_backend.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    final private AuthenticationManager authenticationManager;
    final private UserRepository userRepository;
    final private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        var authToken = new UsernamePasswordAuthenticationToken(authDTO.login(), authDTO.password());
        var auth = authenticationManager.authenticate(authToken);
        var user = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken(user);
        return ResponseEntity.ok(new ResponseAuthDTO(user, token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        if (registerDTO.login() == null || registerDTO.login().isBlank()
                || registerDTO.password() == null || registerDTO.password().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (this.userRepository.findByLogin(registerDTO.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        var encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
        var user = userRepository.save(new Usuario(PapelUsuario.USER, registerDTO.login(), encryptedPassword));
        user.setSenha(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}
