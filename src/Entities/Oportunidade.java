package Entities;

import Model.Modalidade;
import Model.StatusOportunidade;

import java.time.LocalDate;

public class Oportunidade {
    private String titulo;
    private String descricao;
    private Modalidade modalidade;
    private int cargaHorariaPrevista;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int vagas;
    private StatusOportunidade status;

    public Oportunidade(String titulo , String descricao ,
                        Modalidade modalidade , int cargaHorariaPrevista,
                        LocalDate dataInicio , LocalDate dataFim,
                        int vagas , StatusOportunidade status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.modalidade = modalidade;
        this.cargaHorariaPrevista = cargaHorariaPrevista;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.vagas = vagas;
        this.status = status;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCargaHorariaPrevista() {
        return cargaHorariaPrevista;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public int getVagas() {
        return vagas;
    }

    public StatusOportunidade getStatus() {
        return status;
    }
}
