package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.*;
import br.com.projeto.integrador.entity.Chamado;
import br.com.projeto.integrador.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {
    @Mock ChamadoRepository chamadoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @InjectMocks ChamadoService service;

    private ChamadoRequest request() {
        return new ChamadoRequest("Erro no sistema", null, "Arthur", "Financeiro",
            "TI - Sistemas", null, null, "Sistema", "Média", "Baixo", "Novo",
            "Sistema não abre", null);
    }

    @Test
    void criarChamadoPersisteDadosERegistraMovimentacaoInicial() {
        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChamadoResponse response = service.criar(request());

        ArgumentCaptor<Chamado> captor = ArgumentCaptor.forClass(Chamado.class);
        verify(chamadoRepository).save(captor.capture());
        Chamado salvo = captor.getValue();
        assertEquals("Erro no sistema", response.titulo());
        assertEquals("Arthur", salvo.getSolicitanteNome());
        assertEquals("Novo", salvo.getStatus());
        assertEquals(1, salvo.getMovimentacoes().size());
        assertEquals("Chamado criado.", salvo.getMovimentacoes().getFirst().getDescricao());
    }

    @Test
    void excluirChamadoExistenteUsaRepositorio() {
        Chamado chamado = new Chamado();
        when(chamadoRepository.findById(10)).thenReturn(Optional.of(chamado));

        service.excluir(10);

        verify(chamadoRepository).delete(chamado);
    }

    @Test
    void buscarChamadoPorIdRetornaRegistroPersistido() {
        Chamado chamado = chamadoExistente();
        when(chamadoRepository.findById(7)).thenReturn(Optional.of(chamado));

        ChamadoResponse response = service.buscar(7);

        assertEquals("Erro no sistema", response.titulo());
        assertEquals("Novo", response.status());
        verify(chamadoRepository).findById(7);
    }

    @Test
    void atualizarChamadoAlteraCamposSemCriarNovoRegistro() {
        Chamado chamado = chamadoExistente();
        when(chamadoRepository.findById(7)).thenReturn(Optional.of(chamado));
        when(chamadoRepository.save(chamado)).thenReturn(chamado);
        ChamadoRequest atualizacao = new ChamadoRequest("Erro corrigido", null, "Arthur",
            "Financeiro", "TI - Sistemas", null, null, "Sistema", "Alta", "Alto",
            "Em atendimento", "Diagnóstico atualizado", "Atendimento iniciado");

        ChamadoResponse response = service.atualizar(7, atualizacao);

        assertEquals("Erro corrigido", response.titulo());
        assertEquals("Em atendimento", response.status());
        assertEquals("Alta", response.prioridade());
        assertEquals(1, chamado.getMovimentacoes().size());
        verify(chamadoRepository).save(chamado);
    }

    private Chamado chamadoExistente() {
        Chamado chamado = new Chamado();
        chamado.setTitulo("Erro no sistema");
        chamado.setSolicitanteNome("Arthur");
        chamado.setAreaSolicitante("Financeiro");
        chamado.setAreaResponsavel("TI - Sistemas");
        chamado.setCategoria("Sistema");
        chamado.setPrioridade("Média");
        chamado.setImpacto("Baixo");
        chamado.setStatus("Novo");
        chamado.setDescricao("Sistema não abre");
        return chamado;
    }
}
