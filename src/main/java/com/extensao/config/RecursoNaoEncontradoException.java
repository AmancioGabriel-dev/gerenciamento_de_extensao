package com.extensao.config;

/**
 * Lancada quando um id pedido na URL nao existe. O GlobalExceptionHandler
 * a traduz para HTTP 404 (Not Found).
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
