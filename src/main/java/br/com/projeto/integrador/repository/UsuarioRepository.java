package br.com.projeto.integrador.repository;

import br.com.projeto.integrador.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findAllByOrderByNomeAsc();
    List<Usuario> findByStatusOrderByNomeAsc(String status);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);
    Optional<Usuario> findByNomeUsuarioIgnoreCase(String nomeUsuario);
    boolean existsByNomeUsuarioIgnoreCase(String nomeUsuario);
}
