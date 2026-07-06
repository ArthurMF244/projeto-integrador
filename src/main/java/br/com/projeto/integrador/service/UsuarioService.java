package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.*;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.exception.*;
import br.com.projeto.integrador.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class UsuarioService {
    private static final Set<String> PERFIS = Set.of("Administrador", "Atendente", "Solicitante");
    private static final Set<String> STATUS = Set.of("Ativo", "Inativo");
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository; this.passwordEncoder = passwordEncoder;
    }
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(boolean ativos) {
        return (ativos ? repository.findByStatusOrderByNomeAsc("Ativo") : repository.findAllByOrderByNomeAsc())
            .stream().map(UsuarioResponse::from).toList();
    }
    @Transactional public UsuarioResponse criar(UsuarioCreateRequest r) {
        validarUnicos(null, r.email(), r.nomeUsuario());
        Usuario u = new Usuario();
        preencher(u, r.nome(), r.email(), r.setor(), r.perfil(), r.status(), r.nomeUsuario());
        u.setSenha(passwordEncoder.encode(r.senha()));
        return UsuarioResponse.from(repository.save(u));
    }
    @Transactional public UsuarioResponse atualizar(Integer id, UsuarioUpdateRequest r) {
        Usuario u = buscarEntidade(id);
        validarUnicos(u, r.email(), r.nomeUsuario());
        preencher(u, r.nome(), r.email(), r.setor(), r.perfil(), r.status(), r.nomeUsuario());
        if (r.senha() != null && !r.senha().isBlank()) {
            if (r.senha().length() < 6) throw new BusinessException("A senha deve possuir pelo menos 6 caracteres.");
            u.setSenha(passwordEncoder.encode(r.senha()));
        }
        return UsuarioResponse.from(repository.save(u));
    }
    @Transactional(readOnly = true) public UsuarioResponse buscar(Integer id) {
        return UsuarioResponse.from(buscarEntidade(id));
    }
    @Transactional(readOnly = true) public Usuario buscarEntidade(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }
    @Transactional public void excluir(Integer id) { repository.delete(buscarEntidade(id)); }
    private void validarUnicos(Usuario atual, String email, String username) {
        if ((atual == null || !atual.getEmail().equalsIgnoreCase(email)) && repository.existsByEmailIgnoreCase(email))
            throw new BusinessException("Já existe um usuário com este e-mail.");
        if ((atual == null || !atual.getNomeUsuario().equalsIgnoreCase(username)) &&
            repository.existsByNomeUsuarioIgnoreCase(username))
            throw new BusinessException("Já existe um usuário com este nome de usuário.");
    }
    private void preencher(Usuario u, String nome, String email, String setor, String perfil, String status, String username) {
        if (!PERFIS.contains(perfil) || !STATUS.contains(status))
            throw new BusinessException("Perfil ou status inválido.");
        u.setNome(nome.trim()); u.setEmail(email.trim().toLowerCase(Locale.ROOT)); u.setSetor(setor.trim());
        u.setPerfil(perfil); u.setStatus(status); u.setNomeUsuario(username.trim().toLowerCase(Locale.ROOT));
    }
}
