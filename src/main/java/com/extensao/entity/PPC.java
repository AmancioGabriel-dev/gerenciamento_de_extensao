package com.extensao.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Projeto Pedagogico de Curso - versionado (ENTIDADE).
 */
@Entity
@Table(name = "ppc")
public class PPC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String versao;

    private int cargaHorariaMinima;
    private String autor;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;
    private LocalDate dataCriacao;

    protected PPC() {
    }

    public PPC(String versao, int cargaHorariaMinima, String autor,
               LocalDate vigenciaInicio, LocalDate vigenciaFim) {
        this.versao = versao;
        this.cargaHorariaMinima = cargaHorariaMinima;
        this.autor = autor;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFim = vigenciaFim;
        this.dataCriacao = LocalDate.now();
    }

    public boolean estaVigente(LocalDate dataReferencia) {
        if (dataReferencia.isBefore(vigenciaInicio)) return false;
        return vigenciaFim == null || !dataReferencia.isAfter(vigenciaFim);
    }

    public void encerrarVigencia(LocalDate fim) {
        this.vigenciaFim = fim;
    }

    public Long getId() {
        return id;
    }

    public String getVersao() {
        return versao;
    }

    public int getCargaHorariaMinima() {
        return cargaHorariaMinima;
    }

    public String getAutor() {
        return autor;
    }

    public LocalDate getVigenciaInicio() {
        return vigenciaInicio;
    }

    public LocalDate getVigenciaFim() {
        return vigenciaFim;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    @Override
    public String toString() {
        return String.format("PPC v%s | CH min: %dh | Autor: %s | Vigencia: %s a %s | Criado em: %s",
                versao, cargaHorariaMinima, autor, vigenciaInicio,
                vigenciaFim == null ? "atual" : vigenciaFim, dataCriacao);
    }
}
