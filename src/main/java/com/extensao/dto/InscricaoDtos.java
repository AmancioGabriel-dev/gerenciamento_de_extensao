package com.extensao.dto;

import com.extensao.entity.Inscricao;

import java.time.LocalDate;

public final class InscricaoDtos {

    private InscricaoDtos() {
    }

    public record InscreverRequest(Long discenteId, Long oportunidadeId) {
    }

    public record RejeitarRequest(Long docenteId, String motivo) {
    }

    public record SubstituirRequest(Long docenteId, String justificativa) {
    }

    public record ResponsavelRequest(Long usuarioId) {
    }

    public record InscricaoResponse(
            Long id, Long discenteId, String discente,
            Long oportunidadeId, String oportunidade,
            LocalDate dataInscricao, String status,
            String justificativaRemocao, boolean concluinte) {

        public static InscricaoResponse from(Inscricao i) {
            return new InscricaoResponse(
                    i.getId(),
                    i.getDiscente().getId(), i.getDiscente().getNome(),
                    i.getOportunidade().getId(), i.getOportunidade().getTitulo(),
                    i.getDataInscricao(), i.getStatus().name(),
                    i.getJustificativaRemocao(), i.isConcluinte());
        }
    }
}
