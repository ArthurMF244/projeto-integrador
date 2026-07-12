package br.com.projeto.integrador.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrf = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrf.setCookiePath("/");
        http
            .csrf(c -> c.csrfTokenRepository(csrf))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/login", "/login.html", "/api/auth/csrf", "/css/**", "/js/login.js", "/assets/**", "/favicon.ico",
                    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/usuarios.html", "/api/usuarios/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated())
            .exceptionHandling(e -> e.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                request -> request.getRequestURI().startsWith("/api/")))
            .formLogin(f -> f.loginPage("/login.html").loginProcessingUrl("/login")
                .usernameParameter("nomeUsuario").passwordParameter("senha")
                .defaultSuccessUrl("/index.html", true)
                .failureUrl("/login.html?erro=true").permitAll())
            .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login.html?logout=true")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID"));
        return http.build();
    }
}
