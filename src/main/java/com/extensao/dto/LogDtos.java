package com.extensao.dto;

import com.extensao.entity.LogAlteracao;

import java.time.LocalDateTime;

public final class LogDtos {

    private LogDtos() {
    }

    public record LogResponse(Long id, String usuarioResponsavel, LocalDateTime dataHora, String operacao) {
        public static LogResponse from(LogAlteracao l) {
            return new LogResponse(l.getId(), l.getUsuarioResponsavel(), l.getDataHora(), l.getOperacao());
        }
    }
}
