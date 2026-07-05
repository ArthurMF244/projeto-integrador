package br.com.projeto.integrador.repository;

import br.com.projeto.integrador.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findAllByOrderByNomeAsc();
    List<Usuario> findByStatusOrderByNomeAsc(String status);
    boolean existsByEmailIgnoreCase(String email);
}
