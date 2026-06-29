package com.extensao.service;

import com.extensao.entity.Discente;
import com.extensao.entity.Docente;
import com.extensao.entity.Inscricao;
import com.extensao.entity.Oportunidade;
import com.extensao.model.StatusInscricao;
import com.extensao.model.StatusOportunidade;
import com.extensao.repository.InscricaoRepository;
import com.extensao.repository.OportunidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Regras de inscricao + fila de espera. Logica preservada do original.
 */
@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final OportunidadeRepository oportunidadeRepository;
    private final LogService logService;

    public InscricaoService(InscricaoRepository inscricaoRepository,
                            OportunidadeRepository oportunidadeRepository,
                            LogService logService) {
        this.inscricaoRepository = inscricaoRepository;
        this.oportunidadeRepository = oportunidadeRepository;
        this.logService = logService;
    }

    @Transactional
    public Inscricao inscrever(Discente discente, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.ABERTA) {
            throw new IllegalStateException("Oportunidade nao esta aberta para inscricoes.");
        }
        boolean jaInscrito = op.getInscricoes().stream()
                .anyMatch(i -> i.getDiscente().equals(discente)
                        && i.getStatus() != StatusInscricao.CANCELADA
                        && i.getStatus() != StatusInscricao.REMOVIDA);
        if (jaInscrito) {
            throw new IllegalStateException("Discente ja inscrito nesta oportunidade.");
        }
        Inscricao i = new Inscricao(discente, op);
        if (op.getVagasDisponiveis() <= 0) {
            op.adicionarNaFila(discente);
            logService.registrar(discente.getNome(),
                    "Entrou na fila de espera da oportunidade #" + op.getId());
        }
        op.adicionarInscricao(i);
        i = inscricaoRepository.save(i);
        oportunidadeRepository.save(op);
        logService.registrar(discente.getNome(),
                "Inscreveu-se na oportunidade #" + op.getId());
        return i;
    }

    @Transactional
    public void aprovar(Docente responsavel, Inscricao inscricao) {
        validarResponsavel(responsavel, inscricao.getOportunidade());
        if (inscricao.getStatus() != StatusInscricao.PENDENTE) {
            throw new IllegalStateException("Inscricao nao esta pendente.");
        }
        if (inscricao.getOportunidade().getVagasDisponiveis() <= 0) {
            throw new IllegalStateException("Nao ha vagas disponiveis.");
        }
        inscricao.setStatus(StatusInscricao.APROVADA);
        inscricaoRepository.save(inscricao);
        logService.registrar(responsavel.getNome(),
                "Aprovou inscricao de " + inscricao.getDiscente().getNome()
                        + " na oportunidade #" + inscricao.getOportunidade().getId());
    }

    @Transactional
    public void rejeitar(Docente responsavel, Inscricao inscricao, String motivo) {
        validarResponsavel(responsavel, inscricao.getOportunidade());
        inscricao.setStatus(StatusInscricao.REJEITADA);
        inscricao.setJustificativaRemocao(motivo);
        inscricaoRepository.save(inscricao);
        logService.registrar(responsavel.getNome(),
                "Rejeitou inscricao de " + inscricao.getDiscente().getNome()
                        + " (motivo: " + motivo + ")");
    }

    @Transactional
    public void cancelarInscricao(Discente discente, Inscricao inscricao) {
        Oportunidade op = inscricao.getOportunidade();
        if (!inscricao.getDiscente().equals(discente)) {
            throw new IllegalStateException("Discente nao pode cancelar inscricao de outro.");
        }
        if (LocalDate.now().isAfter(op.getDataInicio())) {
            throw new IllegalStateException("Nao e possivel cancelar apos o inicio da oportunidade.");
        }
        inscricao.setStatus(StatusInscricao.CANCELADA);
        inscricaoRepository.save(inscricao);
        logService.registrar(discente.getNome(),
                "Cancelou inscricao na oportunidade #" + op.getId());
        promoverProximoDaFila(op);
    }

    @Transactional
    public void substituirParticipante(Docente responsavel, Inscricao inscricao, String justificativa) {
        validarResponsavel(responsavel, inscricao.getOportunidade());
        if (inscricao.getStatus() != StatusInscricao.APROVADA) {
            throw new IllegalStateException("So e possivel substituir participantes aprovados.");
        }
        inscricao.setStatus(StatusInscricao.REMOVIDA);
        inscricao.setJustificativaRemocao(justificativa);
        inscricaoRepository.save(inscricao);
        logService.registrar(responsavel.getNome(),
                "Removeu " + inscricao.getDiscente().getNome()
                        + " da oportunidade #" + inscricao.getOportunidade().getId()
                        + " (justificativa: " + justificativa + ")");
        promoverProximoDaFila(inscricao.getOportunidade());
    }

    private void promoverProximoDaFila(Oportunidade op) {
        Discente proximo = op.removerProximoDaFila();
        if (proximo != null) {
            Inscricao nova = new Inscricao(proximo, op);
            nova.setStatus(StatusInscricao.APROVADA);
            op.adicionarInscricao(nova);
            inscricaoRepository.save(nova);
            oportunidadeRepository.save(op);
            logService.registrar("SISTEMA",
                    "Promoveu " + proximo.getNome() + " da fila para aprovado na oportunidade #" + op.getId());
        }
    }

    private void validarResponsavel(Docente d, Oportunidade op) {
        if (op.getDocenteResponsavel() != null && !op.getDocenteResponsavel().equals(d)) {
            throw new IllegalStateException(
                    "Apenas o docente responsavel pela oportunidade pode executar esta acao.");
        }
    }

    public Inscricao buscarPorId(Long id) {
        return inscricaoRepository.findById(id).orElse(null);
    }

    public List<Inscricao> listarInscricoesDoDiscente(Discente discente) {
        return inscricaoRepository.findByDiscente(discente);
    }

    public List<Inscricao> listarPendentesDoDocente(Docente docente) {
        return inscricaoRepository.findByOportunidadeDocenteResponsavelAndStatus(
                docente, StatusInscricao.PENDENTE);
    }

    public List<Inscricao> listarAprovadasDoDocente(Docente docente) {
        return inscricaoRepository.findByOportunidadeDocenteResponsavelAndStatus(
                docente, StatusInscricao.APROVADA);
    }
}
