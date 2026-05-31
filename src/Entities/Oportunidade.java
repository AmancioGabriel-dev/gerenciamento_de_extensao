package Entities;

import Model.Modalidade;
import Model.StatusInscricao;
import Model.StatusOportunidade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Oportunidade {

    private static int contador = 1;

    private final int id;
    private String titulo;
    private String descricao;
    private Modalidade modalidade;
    private int cargaHorariaPrevista;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int vagas;
    private StatusOportunidade status;
    private Usuario criador;
    private Docente docenteResponsavel;
    private final List<String> anexos;
    private final List<Inscricao> inscricoes;
    private final List<Discente> filaEspera;

    public Oportunidade(String titulo, String descricao,
                        Modalidade modalidade, int cargaHorariaPrevista,
                        LocalDate dataInicio, LocalDate dataFim,
                        int vagas, Usuario criador, Docente docenteResponsavel) {
        this.id = contador++;
        this.titulo = titulo;
        this.descricao = descricao;
        this.modalidade = modalidade;
        this.cargaHorariaPrevista = cargaHorariaPrevista;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.vagas = vagas;
        this.criador = criador;
        this.docenteResponsavel = docenteResponsavel;
        this.status = StatusOportunidade.RASCUNHO;
        this.anexos = new ArrayList<>();
        this.inscricoes = new ArrayList<>();
        this.filaEspera = new ArrayList<>();
    }

    public int getVagasDisponiveis() {
        int aprovadas = (int) inscricoes.stream()
                .filter(i -> i.getStatus() == StatusInscricao.APROVADA
                        || i.getStatus() == StatusInscricao.CONCLUIDA)
                .count();
        return vagas - aprovadas;
    }

    public void adicionarAnexo(String anexo) {
        anexos.add(anexo);
    }

    public void adicionarInscricao(Inscricao inscricao) {
        inscricoes.add(inscricao);
    }

    public void adicionarNaFila(Discente discente) {
        if (!filaEspera.contains(discente)) {
            filaEspera.add(discente);
        }
    }

    public Discente removerProximoDaFila() {
        if (filaEspera.isEmpty()) return null;
        return filaEspera.remove(0);
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Modalidade getModalidade() {
        return modalidade;
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

    public int getVagas() {
        return vagas;
    }

    public StatusOportunidade getStatus() {
        return status;
    }

    public void setStatus(StatusOportunidade status) {
        this.status = status;
    }

    public Usuario getCriador() {
        return criador;
    }

    public Docente getDocenteResponsavel() {
        return docenteResponsavel;
    }

    public void setDocenteResponsavel(Docente docenteResponsavel) {
        this.docenteResponsavel = docenteResponsavel;
    }

    public List<String> getAnexos() {
        return anexos;
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public List<Discente> getFilaEspera() {
        return filaEspera;
    }

    @Override
    public String toString() {
        return String.format("#%d %s [%s] - %dh - %s a %s - Vagas: %d/%d - %s",
                id, titulo, modalidade, cargaHorariaPrevista,
                dataInicio, dataFim,
                getVagasDisponiveis(), vagas, status);
    }
}
