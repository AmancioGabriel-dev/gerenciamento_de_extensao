package com.extensao.entity;

import com.extensao.model.StatusSolicitacao;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Solicitacao de aproveitamento de horas de extensao (ENTIDADE).
 * id agora gerado pelo banco (sem "static contador").
 */
@Entity
@Table(name = "solicitacao_aproveitamento")
public class SolicitacaoAproveitamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_solicitante_id")
    private Discente alunoSolicitante;

    @Column(length = 2000)
    private String descricao;

    private int cargaHorariaPleiteada;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String documentoComprobatorio;

    @Column(length = 2000)
    private String parecer;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    private LocalDate dataSubmissao;
    private LocalDate dataDecisao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "avaliador_responsavel_id")
    private Usuario avaliadorResponsavel;

    private boolean delegadaParaComissao;
    private int contadorReenvios;

    protected SolicitacaoAproveitamento() {
    }

    public SolicitacaoAproveitamento(Discente alunoSolicitante, String descricao,
                                     int cargaHorariaPleiteada, LocalDate dataInicio,
                                     LocalDate dataFim, String documentoComprobatorio) {
        this.alunoSolicitante = alunoSolicitante;
        this.descricao = descricao;
        this.cargaHorariaPleiteada = cargaHorariaPleiteada;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.documentoComprobatorio = documentoComprobatorio;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataSubmissao = LocalDate.now();
        this.parecer = "";
        this.delegadaParaComissao = false;
        this.contadorReenvios = 0;
    }

    /** Regra: reenviar reabre como PENDENTE, troca o documento e zera o parecer. */
    public void registrarReenvio(String novoDocumento) {
        this.documentoComprobatorio = novoDocumento;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataSubmissao = LocalDate.now();
        this.parecer = "";
        this.contadorReenvios++;
    }

    public Long getId() {
        return id;
    }

    public Discente getAlunoSolicitante() {
        return alunoSolicitante;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCargaHorariaPleiteada() {
        return cargaHorariaPleiteada;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public String getDocumentoComprobatorio() {
        return documentoComprobatorio;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public LocalDate getDataSubmissao() {
        return dataSubmissao;
    }

    public LocalDate getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(LocalDate dataDecisao) {
        this.dataDecisao = dataDecisao;
    }

    public Usuario getAvaliadorResponsavel() {
        return avaliadorResponsavel;
    }

    public void setAvaliadorResponsavel(Usuario avaliadorResponsavel) {
        this.avaliadorResponsavel = avaliadorResponsavel;
    }

    public boolean isDelegadaParaComissao() {
        return delegadaParaComissao;
    }

    public void setDelegadaParaComissao(boolean delegadaParaComissao) {
        this.delegadaParaComissao = delegadaParaComissao;
    }

    public int getContadorReenvios() {
        return contadorReenvios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitacaoAproveitamento other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("#%d %s | %dh | submetida em %s | status: %s%s",
                id, descricao, cargaHorariaPleiteada, dataSubmissao, status,
                delegadaParaComissao ? " (delegada a comissao)" : "");
    }
}
