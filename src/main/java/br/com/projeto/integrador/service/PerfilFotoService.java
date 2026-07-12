package br.com.projeto.integrador.service;

import br.com.projeto.integrador.dto.PerfilResponse;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.exception.BusinessException;
import br.com.projeto.integrador.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PerfilFotoService {
    private static final long LIMITE_BYTES = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSOES = Map.of(
        MediaType.IMAGE_JPEG_VALUE, "jpg",
        MediaType.IMAGE_PNG_VALUE, "png",
        "image/webp", "webp"
    );
    private final Path pasta;
    private final UsuarioRepository repository;
    private final PerfilService perfilService;

    public PerfilFotoService(
        @Value("${app.storage.profile-photos:./data/profile-photos}") String pasta,
        UsuarioRepository repository,
        PerfilService perfilService
    ) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
        this.repository = repository;
        this.perfilService = perfilService;
    }

    @PostConstruct
    void prepararPasta() {
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível preparar a pasta de fotos.", e);
        }
    }

    @Transactional
    public PerfilResponse salvar(String nomeUsuario, MultipartFile arquivo) {
        String mime = validar(arquivo);
        Usuario usuario = perfilService.buscarUsuario(nomeUsuario);
        String nomeNovo = UUID.randomUUID() + "." + EXTENSOES.get(mime);
        Path destino = caminhoSeguro(nomeNovo);
        try {
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Não foi possível salvar a foto.");
        }

        String fotoAnterior = usuario.getFoto();
        usuario.setFoto(nomeNovo);
        try {
            repository.save(usuario);
        } catch (RuntimeException e) {
            excluirArquivo(nomeNovo);
            throw e;
        }
        excluirArquivo(fotoAnterior);
        return PerfilResponse.from(usuario);
    }

    @Transactional
    public PerfilResponse remover(String nomeUsuario) {
        Usuario usuario = perfilService.buscarUsuario(nomeUsuario);
        String fotoAnterior = usuario.getFoto();
        usuario.setFoto(null);
        repository.save(usuario);
        excluirArquivo(fotoAnterior);
        return PerfilResponse.from(usuario);
    }

    public FotoArmazenada carregar(String nome) {
        if (nome == null || !nome.matches("[0-9a-fA-F-]{36}\\.(jpg|png|webp)")) {
            throw new BusinessException("Foto inválida.");
        }
        Path arquivo = caminhoSeguro(nome);
        if (!Files.isRegularFile(arquivo)) {
            throw new br.com.projeto.integrador.exception.ResourceNotFoundException("Foto não encontrada.");
        }
        try {
            Resource recurso = new UrlResource(arquivo.toUri());
            String extensao = nome.substring(nome.lastIndexOf('.') + 1);
            String mime = switch (extensao) {
                case "jpg" -> MediaType.IMAGE_JPEG_VALUE;
                case "png" -> MediaType.IMAGE_PNG_VALUE;
                default -> "image/webp";
            };
            return new FotoArmazenada(recurso, mime);
        } catch (IOException e) {
            throw new BusinessException("Não foi possível carregar a foto.");
        }
    }

    private String validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Selecione uma imagem.");
        }
        if (arquivo.getSize() > LIMITE_BYTES) {
            throw new BusinessException("A foto deve possuir no máximo 5 MB.");
        }
        String declarado = arquivo.getContentType();
        try {
            byte[] cabecalho = arquivo.getInputStream().readNBytes(12);
            String real = detectarMime(cabecalho);
            if (real == null || !real.equals(declarado) || !EXTENSOES.containsKey(real)) {
                throw new BusinessException("Envie uma imagem JPG, PNG ou WEBP válida.");
            }
            return real;
        } catch (IOException e) {
            throw new BusinessException("Não foi possível validar a foto.");
        }
    }

    private String detectarMime(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xff) == 0xff && (b[1] & 0xff) == 0xd8 && (b[2] & 0xff) == 0xff) {
            return MediaType.IMAGE_JPEG_VALUE;
        }
        if (b.length >= 8 && (b[0] & 0xff) == 0x89 && b[1] == 0x50 && b[2] == 0x4e && b[3] == 0x47
            && b[4] == 0x0d && b[5] == 0x0a && b[6] == 0x1a && b[7] == 0x0a) {
            return MediaType.IMAGE_PNG_VALUE;
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
            && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "image/webp";
        }
        return null;
    }

    private Path caminhoSeguro(String nome) {
        Path caminho = pasta.resolve(nome).normalize();
        if (!caminho.startsWith(pasta)) {
            throw new BusinessException("Caminho de foto inválido.");
        }
        return caminho;
    }

    private void excluirArquivo(String nome) {
        if (nome == null) return;
        try {
            Files.deleteIfExists(caminhoSeguro(nome));
        } catch (IOException ignored) {
            // A referência no banco é removida mesmo se o arquivo já não existir.
        }
    }

    public record FotoArmazenada(Resource recurso, String mimeType) {}
}
