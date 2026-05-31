package Entities;

import Model.TipoCargo;

import java.time.LocalDate;

public class HistoricoCargo {

    private final TipoCargo tipo;
    private final String nomeOcupante;
    private final int matriculaOcupante;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;

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
