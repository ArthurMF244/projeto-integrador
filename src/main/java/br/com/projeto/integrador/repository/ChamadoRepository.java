package br.com.projeto.integrador.repository;

import br.com.projeto.integrador.entity.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChamadoRepository extends JpaRepository<Chamado, Integer>, JpaSpecificationExecutor<Chamado> {}
