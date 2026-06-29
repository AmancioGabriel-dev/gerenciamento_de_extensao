package com.extensao.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento CENTRALIZADO de erros.
 *
 * @RestControllerAdvice -> intercepta excecoes lancadas por QUALQUER controller
 * e as converte em respostas HTTP padronizadas. E aqui que as excecoes de regra
 * de negocio (que no console viravam System.out "Erro: ...") viram status HTTP:
 *
 *   IllegalStateException     -> 409 CONFLICT      (violou uma regra/estado)
 *   IllegalArgumentException  -> 400 BAD REQUEST   (dado invalido, ex.: email duplicado)
 *   RecursoNaoEncontrado      -> 404 NOT FOUND     (id inexistente)
 *   Validacao (@Valid)        -> 400 BAD REQUEST   (campos obrigatorios)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(LocalDateTime timestamp, int status, String erro, Object detalhe) {
        public static ApiError of(HttpStatus s, Object detalhe) {
            return new ApiError(LocalDateTime.now(), s.value(), s.getReasonPhrase(), detalhe);
        }
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> regraDeNegocio(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> argumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> naoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Reformata QUALQUER ResponseStatusException (ex.: o 401 do login) para o
     * mesmo padrao ApiError, em vez do JSON padrao do Spring.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> statusException(ResponseStatusException ex) {
        HttpStatus s = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(s).body(ApiError.of(s, ex.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(HttpStatus.BAD_REQUEST, campos));
    }
}
