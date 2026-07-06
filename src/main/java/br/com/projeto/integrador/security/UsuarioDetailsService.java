package br.com.projeto.integrador.security;

import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repository;
    public UsuarioDetailsService(UsuarioRepository repository) { this.repository = repository; }

    @Override public UserDetails loadUserByUsername(String username) {
        Usuario u = repository.findByNomeUsuarioIgnoreCase(username.trim())
            .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
        return User.withUsername(u.getNomeUsuario()).password(u.getSenha())
            .roles(u.getPerfil().toUpperCase()).disabled(!"Ativo".equalsIgnoreCase(u.getStatus())).build();
    }
}
