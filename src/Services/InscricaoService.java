package Services;

import Entities.Discente;
import Entities.Docente;
import Entities.Inscricao;
import Entities.Oportunidade;
import Model.StatusInscricao;
import Model.StatusOportunidade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InscricaoService {

    private final LogService logService;
    private final OportunidadeService oportunidadeService;

    public InscricaoService(LogService logService, OportunidadeService oportunidadeService) {
        this.logService = logService;
        this.oportunidadeService = oportunidadeService;
    }

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
        logService.registrar(discente.getNome(),
                "Inscreveu-se na oportunidade #" + op.getId());
        return i;
    }

    public void aprovar(Docente responsavel, Inscricao inscricao) {
        validarResponsavel(responsavel, inscricao.getOportunidade());
        if (inscricao.getStatus() != StatusInscricao.PENDENTE) {
            throw new IllegalStateException("Inscricao nao esta pendente.");
        }
        if (inscricao.getOportunidade().getVagasDisponiveis() <= 0) {
            throw new IllegalStateException("Nao ha vagas disponiveis.");
        }
        inscricao.setStatus(StatusInscricao.APROVADA);
        logService.registrar(responsavel.getNome(),
                "Aprovou inscricao de " + inscricao.getDiscente().getNome()
                        + " na oportunidade #" + inscricao.getOportunidade().getId());
    }

    public void rejeitar(Docente responsavel, Inscricao inscricao, String motivo) {
        validarResponsavel(responsavel, inscricao.getOportunidade());
        inscricao.setStatus(StatusInscricao.REJEITADA);
        inscricao.setJustificativaRemocao(motivo);
        logService.registrar(responsavel.getNome(),
                "Rejeitou inscricao de " + inscricao.getDiscente().getNome()
                        + " (motivo: " + motivo + ")");
    }

    public void cancelarInscricao(Discente discente, Inscricao inscricao) {
        Oportunidade op = inscricao.getOportunidade();
        if (!inscricao.getDiscente().equals(discente)) {
            throw new IllegalStateException("Discente nao pode cancelar inscricao de outro.");
        }
        if (LocalDate.now().isAfter(op.getDataInicio())) {
            throw new IllegalStateException("Nao e possivel cancelar apos o inicio da oportunidade.");
        }
        inscricao.setStatus(StatusInscricao.CANCELADA);
        logService.registrar(discente.getNome(),
                "Cancelou inscricao na oportunidade #" + op.getId());
        promoverProximoDaFila(op);
    }

    public void substituirParticipante(Docente responsavel, Inscricao inscricao,
                                       String justificativa) {
        validarResponsavel(responsavel, inscricao.getOportunidade());
        if (inscricao.getStatus() != StatusInscricao.APROVADA) {
            throw new IllegalStateException("So e possivel substituir participantes aprovados.");
        }
        inscricao.setStatus(StatusInscricao.REMOVIDA);
        inscricao.setJustificativaRemocao(justificativa);
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
            logService.registrar("SISTEMA",
                    "Promoveu " + proximo.getNome() + " da fila para aprovado na oportunidade #" + op.getId());
            System.out.println("[FILA] " + proximo.getNome() + " foi promovido(a) da fila de espera.");
        }
    }

    private void validarResponsavel(Docente d, Oportunidade op) {
        if (op.getDocenteResponsavel() != null && !op.getDocenteResponsavel().equals(d)) {
            throw new IllegalStateException(
                    "Apenas o docente responsavel pela oportunidade pode executar esta acao.");
        }
    }

    public List<Inscricao> listarInscricoesDoDiscente(Discente discente) {
        List<Inscricao> result = new ArrayList<>();
        for (Oportunidade op : oportunidadeService.listarTodas()) {
            for (Inscricao i : op.getInscricoes()) {
                if (i.getDiscente().equals(discente)) result.add(i);
            }
        }
        return result;
    }
}
