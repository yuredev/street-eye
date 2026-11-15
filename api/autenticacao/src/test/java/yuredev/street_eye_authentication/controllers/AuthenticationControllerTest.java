package yuredev.street_eye_authentication.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import yuredev.street_eye_authentication.config.TokenService;
import yuredev.street_eye_authentication.models.PapelUsuario;
import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_authentication.models.dto.RegisterDTO;
import yuredev.street_eye_authentication.models.dto.auth.AuthDTO;
import yuredev.street_eye_authentication.models.dto.auth.ResponseAuthDTO;
import yuredev.street_eye_authentication.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UsuarioRepository usuarioRepo;
    @Mock
    private TokenService tokenService;

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController(authenticationManager, usuarioRepo, tokenService);
    }

    @Test
    @DisplayName("Login válido retorna o usuário autenticado e seu token")
    void loginValidoRetornaUsuarioEToken() {
        Usuario usuario = new Usuario(PapelUsuario.USER, "ana", "hash-da-senha");
        var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(autenticacao);
        when(tokenService.generateToken(usuario)).thenReturn("token-de-teste");

        var response = controller.login(new AuthDTO("ana", "senha123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ResponseAuthDTO.class);
        ResponseAuthDTO body = (ResponseAuthDTO) response.getBody();
        assertThat(body.user()).isSameAs(usuario);
        assertThat(body.token()).isEqualTo("token-de-teste");

        var credentials = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(credentials.capture());
        assertThat(credentials.getValue().getPrincipal()).isEqualTo("ana");
        assertThat(credentials.getValue().getCredentials()).isEqualTo("senha123");
    }

    @Test
    @DisplayName("Login inválido propaga a falha de autenticação sem gerar token")
    void loginInvalidoNaoGeraToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> controller.login(new AuthDTO("ana", "senha-errada")))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(tokenService, usuarioRepo);
    }

    @Test
    @DisplayName("Cadastro salva senha com BCrypt, atribui USER e omite a senha da resposta")
    void cadastroValidoCriaUsuarioComSenhaCriptografada() {
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            assertThat(usuario.getSenha()).isNotEqualTo("senha123");
            assertThat(new BCryptPasswordEncoder().matches("senha123", usuario.getSenha())).isTrue();
            assertThat(usuario.getPapel()).isEqualTo(PapelUsuario.USER);
            usuario.setId("usuario-id");
            return usuario;
        });

        var response = controller.register(new RegisterDTO("ana", "senha123", PapelUsuario.ADMIN));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(Usuario.class);
        Usuario body = (Usuario) response.getBody();
        assertThat(body.getId()).isEqualTo("usuario-id");
        assertThat(body.getLogin()).isEqualTo("ana");
        assertThat(body.getPapel()).isEqualTo(PapelUsuario.USER);
        assertThat(body.getSenha()).isNull();
        verify(usuarioRepo).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Cadastro rejeita login já cadastrado sem salvar outro usuário")
    void cadastroRejeitaLoginDuplicado() {
        when(usuarioRepo.findByLogin("ana"))
                .thenReturn(new Usuario(PapelUsuario.USER, "ana", "hash-da-senha"));

        var response = controller.register(new RegisterDTO("ana", "senha123", null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }

    @ParameterizedTest
    @MethodSource("cadastrosInvalidos")
    @DisplayName("Cadastro rejeita login ou senha nulos, vazios ou em branco")
    void cadastroRejeitaCamposInvalidos(String login, String senha) {
        var response = controller.register(new RegisterDTO(login, senha, null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verifyNoInteractions(usuarioRepo);
    }

    static Stream<Arguments> cadastrosInvalidos() {
        return Stream.of(
                Arguments.of(null, "senha123"),
                Arguments.of("", "senha123"),
                Arguments.of("   ", "senha123"),
                Arguments.of("ana", null),
                Arguments.of("ana", ""),
                Arguments.of("ana", "   "));
    }
}
