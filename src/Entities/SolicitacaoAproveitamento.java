package Entities;

import Model.StatusSolicitacao;

import java.time.LocalDate;

public class SolicitacaoAproveitamento {

    private static int contador = 1;

    private final int id;
    private final Discente alunoSolicitante;
    private String descricao;
    private int cargaHorariaPleiteada;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String documentoComprobatorio;
    private String parecer;
    private StatusSolicitacao status;
    private LocalDate dataSubmissao;
    private LocalDate dataDecisao;
    private Usuario avaliadorResponsavel;
    private boolean delegadaParaComissao;
    private int contadorReenvios;

    public SolicitacaoAproveitamento(Discente alunoSolicitante, String descricao,
                                     int cargaHorariaPleiteada, LocalDate dataInicio,
                                     LocalDate dataFim, String documentoComprobatorio) {
        this.id = contador++;
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

    public void registrarReenvio(String novoDocumento) {
        this.documentoComprobatorio = novoDocumento;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataSubmissao = LocalDate.now();
        this.parecer = "";
        this.contadorReenvios++;
    }

    public int getId() {
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
    public String toString() {
        return String.format("#%d %s | %dh | submetida em %s | status: %s%s",
                id, descricao, cargaHorariaPleiteada, dataSubmissao, status,
                delegadaParaComissao ? " (delegada a comissao)" : "");
    }
}
