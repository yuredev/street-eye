package yuredev.street_eye_authentication.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import yuredev.street_eye_authentication.repositories.UsuarioRepository;

@Service
@AllArgsConstructor
public class AuthorizationService implements UserDetailsService {
    private UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepo.findByLogin(username);
    }
}
