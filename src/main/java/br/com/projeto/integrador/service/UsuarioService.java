package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.UsuarioRequest;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.exception.*;
import br.com.projeto.integrador.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class UsuarioService {
    private static final Set<String> PERFIS = Set.of("Administrador", "Atendente", "Solicitante");
    private static final Set<String> STATUS = Set.of("Ativo", "Inativo");
    private final UsuarioRepository repository;
    public UsuarioService(UsuarioRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public List<Usuario> listar(boolean ativos) {
        return ativos ? repository.findByStatusOrderByNomeAsc("Ativo") : repository.findAllByOrderByNomeAsc();
    }
    @Transactional public Usuario criar(UsuarioRequest r) {
        if (repository.existsByEmailIgnoreCase(r.email())) throw new BusinessException("Já existe um usuário com este e-mail.");
        Usuario u = new Usuario(); preencher(u, r); return repository.save(u);
    }
    @Transactional public Usuario atualizar(Integer id, UsuarioRequest r) {
        Usuario u = buscar(id);
        if (!u.getEmail().equalsIgnoreCase(r.email()) && repository.existsByEmailIgnoreCase(r.email()))
            throw new BusinessException("Já existe um usuário com este e-mail.");
        preencher(u, r); return repository.save(u);
    }
    @Transactional(readOnly = true) public Usuario buscar(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }
    @Transactional public void excluir(Integer id) { repository.delete(buscar(id)); }
    private void preencher(Usuario u, UsuarioRequest r) {
        String perfil = r.perfil() == null ? "Solicitante" : r.perfil();
        String status = r.status() == null ? "Ativo" : r.status();
        if (!PERFIS.contains(perfil) || !STATUS.contains(status)) throw new BusinessException("Perfil ou status inválido.");
        u.setNome(r.nome().trim()); u.setEmail(r.email().trim().toLowerCase());
        u.setSetor(r.setor().trim()); u.setPerfil(perfil); u.setStatus(status);
    }
}
