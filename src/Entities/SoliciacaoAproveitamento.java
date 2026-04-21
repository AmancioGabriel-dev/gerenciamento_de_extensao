package Entities;

import Model.StatusSolicitacao;

import java.time.LocalDate;

public class SoliciacaoAproveitamento {
    private Aluno alunoSolicitante;
    private String descricao;
    private int cargaHorarioPleiteada;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String documentoComprobatorio;
    private String justificativa; //caso as horas sejam negadas
    private StatusSolicitacao status;
    private LocalDate dataSubmissao;

    public SoliciacaoAproveitamento(Aluno alunoSolicitante , String descricao,
                                    int cargaHorarioPleiteada , LocalDate dataInicio,
                                    LocalDate dataFim , String documentoComprobatorio,
                                    String justificativa , StatusSolicitacao status,
                                    LocalDate dataSubmissao) {
        this.alunoSolicitante = alunoSolicitante;
        this.descricao = descricao;
        this.cargaHorarioPleiteada = cargaHorarioPleiteada;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.documentoComprobatorio = documentoComprobatorio;
        this.justificativa = justificativa;
        this.status = status;
        this.dataSubmissao = dataSubmissao;
    }

    public Aluno getAlunoSolicitante() {
        return alunoSolicitante;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCargaHorarioPleiteada() {
        return cargaHorarioPleiteada;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public String getDocumentoComprobatorio() {
        return documentoComprobatorio;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public LocalDate getDataSubmissao() {
        return dataSubmissao;
    }
}
