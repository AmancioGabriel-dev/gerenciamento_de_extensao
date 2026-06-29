package com.extensao.entity;

import com.extensao.model.TipoCargo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

/**
 * VALUE OBJECT: registro historico (uma "foto") de um cargo ja encerrado.
 *
 * @Embeddable -> e embutido na colecao de historico do GrupoDiscente,
 * sem virar tabela com identidade propria.
 *
 * Repare que ele COPIA nome e matricula do discente no momento da criacao
 * (snapshot). Se o aluno mudar de nome depois, o historico preserva o nome
 * da epoca -- exatamente o comportamento desejado num registro historico.
 */
@Embeddable
public class HistoricoCargo {

    @Enumerated(EnumType.STRING)
    private TipoCargo tipo;

    private String nomeOcupante;
    private int matriculaOcupante;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    protected HistoricoCargo() {
    }

    public HistoricoCargo(Cargo cargo) {
        this.tipo = cargo.getTipo();
        this.nomeOcupante = cargo.getOcupante().getNome();
        this.matriculaOcupante = cargo.getOcupante().getMatricula();
        this.dataInicio = cargo.getDataInicio();
        this.dataFim = cargo.getDataFim();
    }

    public TipoCargo getTipo() {
        return tipo;
    }

    public String getNomeOcupante() {
        return nomeOcupante;
    }

    public int getMatriculaOcupante() {
        return matriculaOcupante;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    @Override
    public String toString() {
        return String.format("%s | %s (matr. %d) | %s a %s",
                tipo, nomeOcupante, matriculaOcupante, dataInicio,
                dataFim == null ? "atual" : dataFim);
    }
}
