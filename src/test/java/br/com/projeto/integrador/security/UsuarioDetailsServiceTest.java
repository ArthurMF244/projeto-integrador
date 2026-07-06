package br.com.projeto.integrador.security;

import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioDetailsServiceTest {
    @Test void usuarioInativoNaoPodeAutenticar() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        Usuario u = new Usuario(); u.setNomeUsuario("ana"); u.setSenha("hash"); u.setPerfil("Solicitante"); u.setStatus("Inativo");
        when(repository.findByNomeUsuarioIgnoreCase("ana")).thenReturn(Optional.of(u));
        UserDetails details = new UsuarioDetailsService(repository).loadUserByUsername("ana");
        assertFalse(details.isEnabled());
    }
}
