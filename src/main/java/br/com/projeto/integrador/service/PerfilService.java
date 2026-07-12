package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.PerfilResponse;
import br.com.projeto.integrador.dto.PerfilSenhaRequest;
import br.com.projeto.integrador.dto.PerfilUpdateRequest;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.exception.BusinessException;
import br.com.projeto.integrador.exception.ResourceNotFoundException;
import br.com.projeto.integrador.repository.UsuarioRepository;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilService {
    private static final Set<String> TEMAS = Set.of("light", "dark", "system");
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PerfilService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PerfilResponse buscar(String nomeUsuario) {
        return PerfilResponse.from(buscarUsuario(nomeUsuario));
    }

    @Transactional
    public PerfilResponse atualizar(String nomeUsuario, PerfilUpdateRequest request) {
        Usuario usuario = buscarUsuario(nomeUsuario);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, usuario.getId())) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }
        if (!TEMAS.contains(request.tema())) {
            throw new BusinessException("Tema inválido.");
        }
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setTema(request.tema());
        return PerfilResponse.from(repository.save(usuario));
    }

    @Transactional
    public void alterarSenha(String nomeUsuario, PerfilSenhaRequest request) {
        Usuario usuario = buscarUsuario(nomeUsuario);
        if (usuario.getSenha() == null || !passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new BusinessException("A senha atual está incorreta.");
        }
        if (!request.novaSenha().equals(request.confirmacaoSenha())) {
            throw new BusinessException("A confirmação da nova senha não confere.");
        }
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        repository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(String nomeUsuario) {
        return repository.findByNomeUsuarioIgnoreCase(nomeUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }
}
