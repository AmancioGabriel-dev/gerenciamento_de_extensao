package Services;

import Entities.Comissao;
import Entities.Coordenador;
import Entities.Discente;
import Entities.SolicitacaoAproveitamento;
import Entities.Usuario;
import Model.StatusSolicitacao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AproveitamentoService {

    private final List<SolicitacaoAproveitamento> solicitacoes = new ArrayList<>();
    private final RegraNegocioService regraService;
    private final LogService logService;

    public AproveitamentoService(RegraNegocioService regraService, LogService logService) {
        this.regraService = regraService;
        this.logService = logService;
    }

    public SolicitacaoAproveitamento criarSolicitacao(Discente discente, String descricao,
                                                      int carga, LocalDate inicio,
                                                      LocalDate fim, String documento) {
        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento(
                discente, descricao, carga, inicio, fim, documento);
        solicitacoes.add(s);
        logService.registrar(discente.getNome(),
                "Submeteu solicitacao de aproveitamento #" + s.getId() + " (" + descricao + ", " + carga + "h)");
        return s;
    }

    public void deferir(Coordenador coordenador, SolicitacaoAproveitamento s, String parecer) {
        validarAvaliador(s);
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Solicitacao nao esta pendente.");
        }
        if (s.getAlunoSolicitante().getHorasDeExtensaoAcumuladas()
                + s.getCargaHorariaPleiteada() > regraService.getLimiteHoras()) {
            throw new IllegalStateException("Aprovacao excederia o limite de horas.");
        }
        s.setStatus(StatusSolicitacao.APROVADO);
        s.setParecer(parecer);
        s.setDataDecisao(LocalDate.now());
        s.setAvaliadorResponsavel(coordenador);
        s.getAlunoSolicitante().adicionarHoras(s.getCargaHorariaPleiteada());
        logService.registrar(coordenador.getNome(),
                "Deferiu solicitacao #" + s.getId() + " (parecer: " + parecer + ")");
    }

    public void indeferir(Usuario avaliador, SolicitacaoAproveitamento s, String parecer) {
        validarAvaliador(s);
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Solicitacao nao esta pendente.");
        }
        s.setStatus(StatusSolicitacao.INDEFERIDO);
        s.setParecer(parecer);
        s.setDataDecisao(LocalDate.now());
        s.setAvaliadorResponsavel(avaliador);
        logService.registrar(avaliador.getNome(),
                "Indeferiu solicitacao #" + s.getId() + " (parecer: " + parecer + ")");
    }

    public void delegarParaComissao(Coordenador coord, SolicitacaoAproveitamento s, Comissao comissao) {
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Apenas solicitacoes pendentes podem ser delegadas.");
        }
        s.setDelegadaParaComissao(true);
        s.setAvaliadorResponsavel(comissao);
        logService.registrar(coord.getNome(),
                "Delegou solicitacao #" + s.getId() + " para a comissao " + comissao.getNome());
    }

    public void deferirPelaComissao(Comissao comissao, SolicitacaoAproveitamento s, String parecer) {
        if (!s.isDelegadaParaComissao()) {
            throw new IllegalStateException("Solicitacao nao foi delegada a comissao.");
        }
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Solicitacao nao esta pendente.");
        }
        if (s.getAlunoSolicitante().getHorasDeExtensaoAcumuladas()
                + s.getCargaHorariaPleiteada() > regraService.getLimiteHoras()) {
            throw new IllegalStateException("Aprovacao excederia o limite de horas.");
        }
        s.setStatus(StatusSolicitacao.APROVADO);
        s.setParecer(parecer);
        s.setDataDecisao(LocalDate.now());
        s.setAvaliadorResponsavel(comissao);
        s.getAlunoSolicitante().adicionarHoras(s.getCargaHorariaPleiteada());
        logService.registrar(comissao.getNome(),
                "Comissao deferiu solicitacao #" + s.getId());
    }

    public void cancelarPeloDiscente(Discente d, SolicitacaoAproveitamento s) {
        if (!s.getAlunoSolicitante().equals(d)) {
            throw new IllegalStateException("Discente nao pode cancelar solicitacao de outro.");
        }
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Apenas pendentes podem ser canceladas.");
        }
        s.setStatus(StatusSolicitacao.CANCELADO);
        logService.registrar(d.getNome(),
                "Cancelou solicitacao #" + s.getId());
    }

    public void reenviar(Discente d, SolicitacaoAproveitamento s, String novoDocumento) {
        if (!s.getAlunoSolicitante().equals(d)) {
            throw new IllegalStateException("Discente nao pode reenviar solicitacao de outro.");
        }
        if (s.getStatus() != StatusSolicitacao.INDEFERIDO) {
            throw new IllegalStateException("So e possivel reenviar solicitacoes indeferidas.");
        }
        if (!regraService.prazoReenvioValido(s, LocalDate.now())) {
            throw new IllegalStateException(
                    "Prazo de " + regraService.getPrazoReenvioDiscenteDias() + " dias para reenvio expirado.");
        }
        s.registrarReenvio(novoDocumento);
        logService.registrar(d.getNome(),
                "Reenviou solicitacao #" + s.getId() + " (reenvio #" + s.getContadorReenvios() + ")");
    }

    public List<SolicitacaoAproveitamento> listarTodas() {
        return solicitacoes;
    }

    public List<SolicitacaoAproveitamento> listarPendentesCoordenador() {
        List<SolicitacaoAproveitamento> r = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if (s.getStatus() == StatusSolicitacao.PENDENTE && !s.isDelegadaParaComissao()) r.add(s);
        }
        return r;
    }

    public List<SolicitacaoAproveitamento> listarPendentesComissao() {
        List<SolicitacaoAproveitamento> r = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if (s.getStatus() == StatusSolicitacao.PENDENTE && s.isDelegadaParaComissao()) r.add(s);
        }
        return r;
    }

    public List<SolicitacaoAproveitamento> listarPorDiscente(Discente d) {
        List<SolicitacaoAproveitamento> r = new ArrayList<>();
        for (SolicitacaoAproveitamento s : solicitacoes) {
            if (s.getAlunoSolicitante().equals(d)) r.add(s);
        }
        return r;
    }

    private void validarAvaliador(SolicitacaoAproveitamento s) {
        if (s.isDelegadaParaComissao()) {
            throw new IllegalStateException(
                    "Solicitacao delegada a comissao - use o fluxo da comissao.");
        }
    }
}
