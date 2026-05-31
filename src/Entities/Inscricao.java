package Entities;

import Model.StatusInscricao;

import java.time.LocalDate;

public class Inscricao {

    private final Discente discente;
    private final Oportunidade oportunidade;
    private final LocalDate dataInscricao;
    private StatusInscricao status;
    private String justificativaRemocao;
    private boolean concluinte;

    public Inscricao(Discente discente, Oportunidade oportunidade) {
        this.discente = discente;
        this.oportunidade = oportunidade;
        this.dataInscricao = LocalDate.now();
        this.status = StatusInscricao.PENDENTE;
        this.concluinte = false;
    }

    public Discente getDiscente() {
        return discente;
    }

    public Oportunidade getOportunidade() {
        return oportunidade;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public StatusInscricao getStatus() {
        return status;
    }

    public void setStatus(StatusInscricao status) {
        this.status = status;
    }

    public String getJustificativaRemocao() {
        return justificativaRemocao;
    }

    public void setJustificativaRemocao(String justificativaRemocao) {
        this.justificativaRemocao = justificativaRemocao;
    }

    public boolean isConcluinte() {
        return concluinte;
    }

    public void setConcluinte(boolean concluinte) {
        this.concluinte = concluinte;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s",
                discente.getNome(), oportunidade.getTitulo(), status);
    }
}
