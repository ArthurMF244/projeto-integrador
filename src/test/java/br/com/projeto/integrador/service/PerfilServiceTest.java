package br.com.projeto.integrador.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.projeto.integrador.dto.PerfilSenhaRequest;
import br.com.projeto.integrador.dto.PerfilUpdateRequest;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.exception.BusinessException;
import br.com.projeto.integrador.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PerfilServiceTest {
    private UsuarioRepository repository;
    private PerfilService service;
    private Usuario usuario;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void preparar() {
        repository = mock(UsuarioRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new PerfilService(repository, encoder);
        usuario = new Usuario();
        usuario.setId(7);
        usuario.setNome("Arthur Teste");
        usuario.setEmail("arthur@teste.com");
        usuario.setSetor("TI");
        usuario.setPerfil("Solicitante");
        usuario.setStatus("Ativo");
        usuario.setNomeUsuario("arthur");
        usuario.setTema("dark");
        usuario.setSenha(encoder.encode("senhaAtual"));
        when(repository.findByNomeUsuarioIgnoreCase("arthur")).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void consultaSomenteOProprioPerfilSemExporSenha() {
        var resposta = service.buscar("arthur");
        assertEquals(7, resposta.id());
        assertEquals("arthur", resposta.nomeUsuario());
        assertTrue(java.util.Arrays.stream(resposta.getClass().getRecordComponents())
            .noneMatch(c -> c.getName().toLowerCase().contains("senha")));
        verify(repository).findByNomeUsuarioIgnoreCase("arthur");
        verify(repository, never()).findById(anyInt());
    }

    @Test
    void atualizaDadosETemaDoUsuarioDaSessao() {
        var resposta = service.atualizar("arthur", new PerfilUpdateRequest("Novo Nome", "NOVO@EXEMPLO.COM", "system"));
        assertEquals("Novo Nome", resposta.nome());
        assertEquals("novo@exemplo.com", resposta.email());
        assertEquals("system", resposta.tema());
        verify(repository).save(usuario);
    }

    @Test
    void rejeitaTemaInvalidoMesmoSemConfiarApenasNaValidacaoDoController() {
        assertThrows(BusinessException.class,
            () -> service.atualizar("arthur", new PerfilUpdateRequest("Nome", "a@b.com", "blue")));
    }

    @Test
    void senhaExigeSenhaAtualCorretaEPermaneceBcrypt() {
        assertThrows(BusinessException.class,
            () -> service.alterarSenha("arthur", new PerfilSenhaRequest("errada", "novaSenha", "novaSenha")));

        service.alterarSenha("arthur", new PerfilSenhaRequest("senhaAtual", "novaSenha", "novaSenha"));
        assertTrue(encoder.matches("novaSenha", usuario.getSenha()));
        assertFalse(usuario.getSenha().contains("novaSenha"));
    }
}
