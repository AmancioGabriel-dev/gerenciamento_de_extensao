package com.extensao.service;

import com.extensao.entity.Comissao;
import com.extensao.entity.Coordenador;
import com.extensao.entity.Discente;
import com.extensao.entity.SolicitacaoAproveitamento;
import com.extensao.entity.Usuario;
import com.extensao.model.StatusSolicitacao;
import com.extensao.repository.SolicitacaoAproveitamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Regras de aproveitamento de horas: limite de 345h, prazos, delegacao a
 * comissao, deferir/indeferir, cancelar e reenviar. Logica preservada.
 */
@Service
public class AproveitamentoService {

    private final SolicitacaoAproveitamentoRepository solicitacaoRepository;
    private final RegraNegocioService regraService;
    private final LogService logService;

    public AproveitamentoService(SolicitacaoAproveitamentoRepository solicitacaoRepository,
                                 RegraNegocioService regraService,
                                 LogService logService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.regraService = regraService;
        this.logService = logService;
    }

    @Transactional
    public SolicitacaoAproveitamento criarSolicitacao(Discente discente, String descricao,
                                                      int carga, LocalDate inicio,
                                                      LocalDate fim, String documento) {
        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento(
                discente, descricao, carga, inicio, fim, documento);
        s = solicitacaoRepository.save(s);
        logService.registrar(discente.getNome(),
                "Submeteu solicitacao de aproveitamento #" + s.getId() + " (" + descricao + ", " + carga + "h)");
        return s;
    }

    @Transactional
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
        solicitacaoRepository.save(s);
        logService.registrar(coordenador.getNome(),
                "Deferiu solicitacao #" + s.getId() + " (parecer: " + parecer + ")");
    }

    @Transactional
    public void indeferir(Usuario avaliador, SolicitacaoAproveitamento s, String parecer) {
        validarAvaliador(s);
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Solicitacao nao esta pendente.");
        }
        s.setStatus(StatusSolicitacao.INDEFERIDO);
        s.setParecer(parecer);
        s.setDataDecisao(LocalDate.now());
        s.setAvaliadorResponsavel(avaliador);
        solicitacaoRepository.save(s);
        logService.registrar(avaliador.getNome(),
                "Indeferiu solicitacao #" + s.getId() + " (parecer: " + parecer + ")");
    }

    @Transactional
    public void delegarParaComissao(Coordenador coord, SolicitacaoAproveitamento s, Comissao comissao) {
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Apenas solicitacoes pendentes podem ser delegadas.");
        }
        s.setDelegadaParaComissao(true);
        s.setAvaliadorResponsavel(comissao);
        solicitacaoRepository.save(s);
        logService.registrar(coord.getNome(),
                "Delegou solicitacao #" + s.getId() + " para a comissao " + comissao.getNome());
    }

    @Transactional
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
        solicitacaoRepository.save(s);
        logService.registrar(comissao.getNome(),
                "Comissao deferiu solicitacao #" + s.getId());
    }

    @Transactional
    public void cancelarPeloDiscente(Discente d, SolicitacaoAproveitamento s) {
        if (!s.getAlunoSolicitante().equals(d)) {
            throw new IllegalStateException("Discente nao pode cancelar solicitacao de outro.");
        }
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Apenas pendentes podem ser canceladas.");
        }
        s.setStatus(StatusSolicitacao.CANCELADO);
        solicitacaoRepository.save(s);
        logService.registrar(d.getNome(), "Cancelou solicitacao #" + s.getId());
    }

    @Transactional
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
        solicitacaoRepository.save(s);
        logService.registrar(d.getNome(),
                "Reenviou solicitacao #" + s.getId() + " (reenvio #" + s.getContadorReenvios() + ")");
    }

    public SolicitacaoAproveitamento buscarPorId(Long id) {
        return solicitacaoRepository.findById(id).orElse(null);
    }

    public List<SolicitacaoAproveitamento> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public List<SolicitacaoAproveitamento> listarPendentesCoordenador() {
        return solicitacaoRepository.findByStatusAndDelegadaParaComissao(StatusSolicitacao.PENDENTE, false);
    }

    public List<SolicitacaoAproveitamento> listarPendentesComissao() {
        return solicitacaoRepository.findByStatusAndDelegadaParaComissao(StatusSolicitacao.PENDENTE, true);
    }

    public List<SolicitacaoAproveitamento> listarPorDiscente(Discente d) {
        return solicitacaoRepository.findByAlunoSolicitante(d);
    }

    private void validarAvaliador(SolicitacaoAproveitamento s) {
        if (s.isDelegadaParaComissao()) {
            throw new IllegalStateException(
                    "Solicitacao delegada a comissao - use o fluxo da comissao.");
        }
    }
}
