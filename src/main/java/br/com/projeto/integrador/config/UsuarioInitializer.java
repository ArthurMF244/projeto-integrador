package br.com.projeto.integrador.config;

import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

@Component
public class UsuarioInitializer implements CommandLineRunner {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    @Value("${app.auth.default-user-password:alterar123}") private String defaultPassword;
    @Value("${app.auth.admin-username:admin}") private String adminUsername;
    @Value("${app.auth.admin-password:admin123}") private String adminPassword;
    @Value("${app.auth.admin-email:admin@localhost}") private String adminEmail;

    public UsuarioInitializer(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository; this.encoder = encoder;
    }
    @Override @Transactional public void run(String... args) {
        if (!repository.existsByNomeUsuarioIgnoreCase(adminUsername)) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador"); admin.setEmail(adminEmail.toLowerCase(Locale.ROOT));
            admin.setSetor("Administração"); admin.setPerfil("Administrador"); admin.setStatus("Ativo");
            admin.setNomeUsuario(adminUsername.trim().toLowerCase(Locale.ROOT));
            admin.setSenha(encoder.encode(adminPassword));
            repository.save(admin);
        }
        for (Usuario u : repository.findAll()) {
            boolean changed = false;
            if (u.getNomeUsuario() == null || u.getNomeUsuario().isBlank()) {
                String base = u.getEmail().contains("@") ? u.getEmail().substring(0, u.getEmail().indexOf('@')) : "usuario" + u.getId();
                u.setNomeUsuario(unique(base)); changed = true;
            }
            if (u.getSenha() == null || u.getSenha().isBlank()) {
                u.setSenha(encoder.encode(defaultPassword)); changed = true;
            }
            if (changed) repository.save(u);
        }
    }
    private String unique(String value) {
        String base = value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "");
        if (base.length() < 4) base = "usuario";
        if (base.length() > 45) base = base.substring(0, 45);
        String candidate = base;
        for (int suffix = 2; repository.existsByNomeUsuarioIgnoreCase(candidate); suffix++) candidate = base + suffix;
        return candidate;
    }
}
