package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.*;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.exception.BusinessException;
import br.com.projeto.integrador.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository repository;
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UsuarioService service;
    @BeforeEach void setup() {
        service = new UsuarioService(repository, encoder);
        lenient().when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
    }
    @Test void criacaoCodificaSenha() {
        UsuarioCreateRequest r = new UsuarioCreateRequest("Ana","ana@x.com","TI","Atendente","Ativo","anax","segredo");
        service.criar(r);
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertNotEquals("segredo", captor.getValue().getSenha());
        assertTrue(encoder.matches("segredo", captor.getValue().getSenha()));
    }
    @Test void rejeitaNomeUsuarioDuplicado() {
        when(repository.existsByNomeUsuarioIgnoreCase("anax")).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.criar(
            new UsuarioCreateRequest("Ana","ana@x.com","TI","Atendente","Ativo","anax","segredo")));
    }
    @Test void atualizacaoSemSenhaMantemHash() {
        Usuario u = usuario(); String hash = u.getSenha();
        when(repository.findById(1)).thenReturn(Optional.of(u));
        service.atualizar(1, new UsuarioUpdateRequest("Ana","ana@x.com","TI","Atendente","Ativo","anax"," "));
        assertEquals(hash, u.getSenha());
    }
    @Test void atualizacaoComSenhaAlteraHash() {
        Usuario u = usuario(); String hash = u.getSenha();
        when(repository.findById(1)).thenReturn(Optional.of(u));
        service.atualizar(1, new UsuarioUpdateRequest("Ana","ana@x.com","TI","Atendente","Ativo","anax","novasenha"));
        assertNotEquals(hash, u.getSenha()); assertTrue(encoder.matches("novasenha", u.getSenha()));
    }
    private Usuario usuario() {
        Usuario u = new Usuario(); u.setId(1); u.setNome("Ana"); u.setEmail("ana@x.com"); u.setSetor("TI");
        u.setPerfil("Atendente"); u.setStatus("Ativo"); u.setNomeUsuario("anax"); u.setSenha(encoder.encode("antiga"));
        return u;
    }
}
