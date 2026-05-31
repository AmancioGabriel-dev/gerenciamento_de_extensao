package Entities;

import Model.TipoCargo;

import java.time.LocalDate;

public class Cargo {

    private TipoCargo tipo;
    private Discente ocupante;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public Cargo(TipoCargo tipo, Discente ocupante, LocalDate dataInicio) {
        this.tipo = tipo;
        this.ocupante = ocupante;
        this.dataInicio = dataInicio;
    }

    public void encerrar(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public boolean estaAtivo() {
        return dataFim == null;
    }

    public TipoCargo getTipo() {
        return tipo;
    }

    public Discente getOcupante() {
        return ocupante;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (desde %s%s)",
                tipo, ocupante.getNome(), dataInicio,
                dataFim == null ? "" : " ate " + dataFim);
    }
}
