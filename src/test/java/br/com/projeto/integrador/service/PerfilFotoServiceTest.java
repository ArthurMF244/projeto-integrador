package br.com.projeto.integrador.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import br.com.projeto.integrador.exception.BusinessException;
import br.com.projeto.integrador.repository.UsuarioRepository;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class PerfilFotoServiceTest {
    @TempDir Path pasta;

    private PerfilFotoService service() {
        PerfilFotoService service = new PerfilFotoService(
            pasta.toString(), mock(UsuarioRepository.class), mock(PerfilService.class));
        service.prepararPasta();
        return service;
    }

    @Test
    void rejeitaConteudoQueNaoEImagemMesmoComMimePermitido() {
        MockMultipartFile arquivo = new MockMultipartFile(
            "foto", "foto.jpg", "image/jpeg", "nao-e-imagem".getBytes());
        assertThrows(BusinessException.class, () -> service().salvar("arthur", arquivo));
    }

    @Test
    void rejeitaArquivoAcimaDeCincoMegabytes() {
        byte[] conteudo = new byte[5 * 1024 * 1024 + 1];
        conteudo[0] = (byte) 0xff;
        conteudo[1] = (byte) 0xd8;
        conteudo[2] = (byte) 0xff;
        MockMultipartFile arquivo = new MockMultipartFile("foto", "foto.jpg", "image/jpeg", conteudo);
        assertThrows(BusinessException.class, () -> service().salvar("arthur", arquivo));
    }
}
