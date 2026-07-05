package br.com.projeto.integrador.exception;
import java.time.LocalDateTime;
import java.util.Map;
public record ApiError(int status, String erro, String mensagem, LocalDateTime timestamp,
                       Map<String, String> campos) {}
