package com.extensao.dto;

import com.extensao.entity.SolicitacaoAproveitamento;

import java.time.LocalDate;

public final class AproveitamentoDtos {

    private AproveitamentoDtos() {
    }

    public record CriarSolicitacaoRequest(
            Long discenteId, String descricao, int cargaHorariaPleiteada,
            LocalDate dataInicio, LocalDate dataFim, String documentoComprobatorio) {
    }

    public record ParecerRequest(Long avaliadorId, String parecer) {
    }

    public record DelegarRequest(Long coordenadorId, Long comissaoId) {
    }

    public record DiscenteRequest(Long discenteId) {
    }

    public record ReenvioRequest(Long discenteId, String novoDocumento) {
    }

    public record SolicitacaoResponse(
            Long id, Long alunoId, String aluno, String descricao,
            int cargaHorariaPleiteada, LocalDate dataInicio, LocalDate dataFim,
            String documentoComprobatorio, String parecer, String status,
            LocalDate dataSubmissao, LocalDate dataDecisao,
            String avaliador, boolean delegadaParaComissao, int contadorReenvios) {

        public static SolicitacaoResponse from(SolicitacaoAproveitamento s) {
            return new SolicitacaoResponse(
                    s.getId(),
                    s.getAlunoSolicitante().getId(), s.getAlunoSolicitante().getNome(),
                    s.getDescricao(), s.getCargaHorariaPleiteada(),
                    s.getDataInicio(), s.getDataFim(),
                    s.getDocumentoComprobatorio(), s.getParecer(), s.getStatus().name(),
                    s.getDataSubmissao(), s.getDataDecisao(),
                    s.getAvaliadorResponsavel() == null ? null : s.getAvaliadorResponsavel().getNome(),
                    s.isDelegadaParaComissao(), s.getContadorReenvios());
        }
    }
}
