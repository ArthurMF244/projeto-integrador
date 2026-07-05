package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.*;
import br.com.projeto.integrador.entity.*;
import br.com.projeto.integrador.exception.*;
import br.com.projeto.integrador.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChamadoService {
    static final Set<String> STATUS = Set.of("Novo", "Em atendimento", "Aguardando retorno", "Finalizado");
    static final Set<String> PRIORIDADES = Set.of("Baixa", "Média", "Alta", "Crítica");
    static final Set<String> IMPACTOS = Set.of("Baixo", "Médio", "Alto", "Geral");
    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;

    public ChamadoService(ChamadoRepository chamadoRepository, UsuarioRepository usuarioRepository) {
        this.chamadoRepository = chamadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponse> listar(String status, String prioridade, String area,
            String responsavel, String solicitante, String q) {
        Specification<Chamado> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            addEqual(p, cb, root.get("status"), status);
            addEqual(p, cb, root.get("prioridade"), prioridade);
            addEqual(p, cb, root.get("areaResponsavel"), area);
            addEqual(p, cb, root.get("responsavelNome"), responsavel);
            addEqual(p, cb, root.get("solicitanteNome"), solicitante);
            if (hasText(q)) {
                String like = "%" + q.trim().toLowerCase() + "%";
                p.add(cb.or(cb.like(cb.lower(root.get("titulo")), like),
                    cb.like(cb.lower(root.get("descricao")), like),
                    cb.like(cb.lower(root.get("solicitanteNome")), like),
                    cb.like(cb.lower(root.get("responsavelNome")), like)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
        return chamadoRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"))
            .stream().map(ChamadoResponse::from).toList();
    }

    private static void addEqual(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Path<String> path, String value) {
        if (hasText(value)) p.add(cb.equal(path, value.trim()));
    }
    private static boolean hasText(String value) { return value != null && !value.isBlank(); }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Integer id) { return ChamadoResponse.from(find(id)); }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest r) {
        Chamado c = new Chamado();
        preencher(c, r);
        registrarMovimentacao(c, "Chamado criado.");
        return ChamadoResponse.from(chamadoRepository.save(c));
    }

    @Transactional
    public ChamadoResponse atualizar(Integer id, ChamadoRequest r) {
        Chamado c = find(id);
        preencher(c, r);
        String descricao = hasText(r.descricaoMovimentacao())
            ? r.descricaoMovimentacao().trim() : "Movimentação registrada.";
        registrarMovimentacao(c, descricao);
        return ChamadoResponse.from(chamadoRepository.save(c));
    }

    @Transactional
    public void excluir(Integer id) {
        Chamado c = find(id);
        chamadoRepository.delete(c);
    }

    private Chamado find(Integer id) {
        return chamadoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado."));
    }

    private void preencher(Chamado c, ChamadoRequest r) {
        validarOpcao("status", r.status() == null ? "Novo" : r.status(), STATUS);
        validarOpcao("prioridade", r.prioridade(), PRIORIDADES);
        validarOpcao("impacto", r.impacto() == null ? "Baixo" : r.impacto(), IMPACTOS);
        Usuario solicitante = usuario(r.solicitanteId(), "Solicitante");
        Usuario responsavel = usuario(r.responsavelId(), "Responsável");
        c.setTitulo(r.titulo().trim());
        c.setSolicitante(solicitante);
        c.setSolicitanteNome(solicitante != null ? solicitante.getNome() :
            (hasText(r.solicitanteNome()) ? r.solicitanteNome().trim() : "Não informado"));
        c.setAreaSolicitante(r.areaSolicitante().trim());
        c.setAreaResponsavel(r.areaResponsavel().trim());
        c.setResponsavel(responsavel);
        c.setResponsavelNome(responsavel != null ? responsavel.getNome() :
            (hasText(r.responsavelNome()) ? r.responsavelNome().trim() : null));
        c.setCategoria(r.categoria().trim());
        c.setPrioridade(r.prioridade());
        c.setImpacto(r.impacto() == null ? "Baixo" : r.impacto());
        c.setStatus(r.status() == null ? "Novo" : r.status());
        c.setDescricao(r.descricao().trim());
        if ("Finalizado".equals(c.getStatus())) {
            if (c.getFinalizadoEm() == null) c.setFinalizadoEm(LocalDateTime.now());
        } else c.setFinalizadoEm(null);
    }

    private Usuario usuario(Integer id, String tipo) {
        if (id == null) return null;
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(tipo + " não encontrado."));
    }
    private static void validarOpcao(String campo, String valor, Set<String> opcoes) {
        if (!opcoes.contains(valor)) throw new BusinessException("Valor inválido para " + campo + ".");
    }
    private static void registrarMovimentacao(Chamado c, String descricao) {
        Movimentacao m = new Movimentacao();
        m.setChamado(c);
        m.setStatus(c.getStatus());
        m.setPrioridade(c.getPrioridade());
        m.setAreaResponsavel(c.getAreaResponsavel());
        m.setResponsavel(c.getResponsavel());
        m.setResponsavelNome(c.getResponsavelNome());
        m.setDescricao(descricao);
        c.getMovimentacoes().add(m);
    }
}
