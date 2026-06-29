package com.extensao.dto;

import com.extensao.entity.Discente;
import com.extensao.entity.Oportunidade;
import com.extensao.model.Modalidade;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public final class OportunidadeDtos {

    private OportunidadeDtos() {
    }

    public record CriarOportunidadeRequest(
            Long criadorId, Long docenteResponsavelId,
            @NotBlank String titulo, String descricao, Modalidade modalidade,
            int cargaHorariaPrevista, LocalDate dataInicio, LocalDate dataFim, int vagas) {
    }

    public record AnexoRequest(@NotBlank String anexo) {
    }

    public record ResponsavelRequest(Long usuarioId) {
    }

    public record EncerrarRequest(Long docenteId, List<Long> concluintesIds) {
    }

    public record OportunidadeResponse(
            Long id, String titulo, String descricao, Modalidade modalidade,
            int cargaHorariaPrevista, LocalDate dataInicio, LocalDate dataFim,
            int vagas, int vagasDisponiveis, String status,
            String criador, String docenteResponsavel,
            List<String> anexos, int qtdInscricoes, List<String> filaEspera) {

        public static OportunidadeResponse from(Oportunidade o) {
            return new OportunidadeResponse(
                    o.getId(), o.getTitulo(), o.getDescricao(), o.getModalidade(),
                    o.getCargaHorariaPrevista(), o.getDataInicio(), o.getDataFim(),
                    o.getVagas(), o.getVagasDisponiveis(), o.getStatus().name(),
                    o.getCriador() == null ? null : o.getCriador().getNome(),
                    o.getDocenteResponsavel() == null ? null : o.getDocenteResponsavel().getNome(),
                    List.copyOf(o.getAnexos()),
                    o.getInscricoes().size(),
                    o.getFilaEspera().stream().map(Discente::getNome).toList());
        }
    }
}
