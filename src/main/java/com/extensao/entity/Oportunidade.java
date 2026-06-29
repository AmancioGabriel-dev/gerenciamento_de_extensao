package com.extensao.entity;

import com.extensao.model.Modalidade;
import com.extensao.model.StatusInscricao;
import com.extensao.model.StatusOportunidade;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Oportunidade de extensao (ENTIDADE / raiz de agregado).
 *
 * Mostra os principais RELACIONAMENTOS do JPA:
 *  - @ManyToOne  : muitas oportunidades para um criador / um docente responsavel
 *  - @OneToMany  : uma oportunidade tem varias inscricoes
 *  - @ManyToMany : a fila de espera liga oportunidade a varios discentes (com ordem)
 *  - @ElementCollection : lista simples de anexos (Strings) numa tabela auxiliar
 *
 * O "static contador" foi removido: agora o id e gerado pelo banco.
 */
@Entity
@Table(name = "oportunidade")
public class Oportunidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    private int cargaHorariaPrevista;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int vagas;

    @Enumerated(EnumType.STRING)
    private StatusOportunidade status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criador_id")
    private Usuario criador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_responsavel_id")
    private Docente docenteResponsavel;

    @ElementCollection
    @CollectionTable(name = "oportunidade_anexos", joinColumns = @JoinColumn(name = "oportunidade_id"))
    @Column(name = "anexo")
    private List<String> anexos = new ArrayList<>();

    @OneToMany(mappedBy = "oportunidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscricao> inscricoes = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "oportunidade_fila_espera",
            joinColumns = @JoinColumn(name = "oportunidade_id"),
            inverseJoinColumns = @JoinColumn(name = "discente_id"))
    @OrderColumn(name = "posicao")
    private List<Discente> filaEspera = new ArrayList<>();

    protected Oportunidade() {
    }

    public Oportunidade(String titulo, String descricao,
                        Modalidade modalidade, int cargaHorariaPrevista,
                        LocalDate dataInicio, LocalDate dataFim,
                        int vagas, Usuario criador, Docente docenteResponsavel) {
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
    }

    /** Regra de dominio: vagas livres = total - (aprovadas + concluidas). */
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
        inscricao.setOportunidade(this);
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

    public Long getId() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oportunidade other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("#%d %s [%s] - %dh - %s a %s - Vagas: %d/%d - %s",
                id, titulo, modalidade, cargaHorariaPrevista,
                dataInicio, dataFim,
                getVagasDisponiveis(), vagas, status);
    }
}
