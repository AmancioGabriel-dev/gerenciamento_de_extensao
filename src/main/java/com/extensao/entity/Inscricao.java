package com.extensao.entity;

import com.extensao.model.StatusInscricao;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Inscricao de um discente em uma oportunidade (ENTIDADE).
 *
 * IMPORTANTE: no projeto original esta classe NAO tinha id e dependia da
 * identidade por referencia. No banco isso nao funciona, entao adicionamos
 * um @Id gerado -> agora ela tem identidade propria de verdade.
 */
@Entity
@Table(name = "inscricao")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discente_id")
    private Discente discente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "oportunidade_id")
    private Oportunidade oportunidade;

    private LocalDate dataInscricao;

    @Enumerated(EnumType.STRING)
    private StatusInscricao status;

    private String justificativaRemocao;
    private boolean concluinte;

    protected Inscricao() {
    }

    public Inscricao(Discente discente, Oportunidade oportunidade) {
        this.discente = discente;
        this.oportunidade = oportunidade;
        this.dataInscricao = LocalDate.now();
        this.status = StatusInscricao.PENDENTE;
        this.concluinte = false;
    }

    public Long getId() {
        return id;
    }

    public Discente getDiscente() {
        return discente;
    }

    public Oportunidade getOportunidade() {
        return oportunidade;
    }

    public void setOportunidade(Oportunidade oportunidade) {
        this.oportunidade = oportunidade;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inscricao other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s",
                discente.getNome(), oportunidade.getTitulo(), status);
    }
}
