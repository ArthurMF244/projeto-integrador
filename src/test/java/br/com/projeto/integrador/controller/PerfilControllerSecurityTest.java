package br.com.projeto.integrador.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.projeto.integrador.config.SecurityConfig;
import br.com.projeto.integrador.service.PerfilFotoService;
import br.com.projeto.integrador.service.PerfilService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PerfilController.class)
@Import(SecurityConfig.class)
class PerfilControllerSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean PerfilService perfilService;
    @MockitoBean PerfilFotoService fotoService;

    @Test
    void usuarioNaoAutenticadoRecebeUnauthorizedNaApi() throws Exception {
        mvc.perform(get("/api/perfil")).andExpect(status().isUnauthorized());
    }

    @Test
    void alteracaoAutenticadaSemCsrfERejeitada() throws Exception {
        mvc.perform(put("/api/perfil")
                .with(user("arthur"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"Arthur","email":"arthur@example.com","tema":"dark"}
                    """))
            .andExpect(status().isForbidden());
    }
}
