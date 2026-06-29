package com.extensao.dto;

import com.extensao.entity.PPC;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public final class PpcDtos {

    private PpcDtos() {
    }

    public record CadastrarPpcRequest(
            String responsavelLog, @NotBlank String versao, int cargaHorariaMinima,
            String autor, LocalDate vigenciaInicio, LocalDate vigenciaFim) {
    }

    public record PpcResponse(
            Long id, String versao, int cargaHorariaMinima, String autor,
            LocalDate vigenciaInicio, LocalDate vigenciaFim, LocalDate dataCriacao) {

        public static PpcResponse from(PPC p) {
            return new PpcResponse(p.getId(), p.getVersao(), p.getCargaHorariaMinima(),
                    p.getAutor(), p.getVigenciaInicio(), p.getVigenciaFim(), p.getDataCriacao());
        }
    }
}
