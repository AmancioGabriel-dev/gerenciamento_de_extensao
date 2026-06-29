package com.extensao.entity;

import com.extensao.model.TipoCargo;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Cargo ATIVO ocupado por um discente dentro de um grupo (ENTIDADE).
 * Tem ciclo de vida: nasce ativo e pode ser "encerrado".
 * Quando encerrado, vira um HistoricoCargo (value object) no grupo.
 */
@Entity
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoCargo tipo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ocupante_id")
    private Discente ocupante;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    protected Cargo() {
    }

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

    public Long getId() {
        return id;
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
