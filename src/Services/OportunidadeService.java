package Services;

import Entities.Coordenador;
import Entities.Discente;
import Entities.Docente;
import Entities.Inscricao;
import Entities.Oportunidade;
import Entities.Usuario;
import Model.Modalidade;
import Model.StatusInscricao;
import Model.StatusOportunidade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OportunidadeService {

    private final List<Oportunidade> oportunidades = new ArrayList<>();
    private final LogService logService;

    public OportunidadeService(LogService logService) {
        this.logService = logService;
    }

    public Oportunidade criar(Usuario criador, Docente docenteResponsavel,
                              String titulo, String descricao, Modalidade modalidade,
                              int cargaHoraria, LocalDate inicio, LocalDate fim, int vagas) {
        if (!podeCriar(criador)) {
            throw new IllegalStateException(
                    "Apenas docentes, coordenadores ou lideres discentes podem criar oportunidades.");
        }
        if (criador instanceof Discente && docenteResponsavel == null) {
            throw new IllegalStateException(
                    "Oportunidade criada por discente exige validacao/aprovacao de um docente responsavel.");
        }
        Oportunidade op = new Oportunidade(titulo, descricao, modalidade, cargaHoraria,
                inicio, fim, vagas, criador, docenteResponsavel);
        oportunidades.add(op);
        logService.registrar(criador.getNome(),
                "Criou oportunidade #" + op.getId() + " - " + titulo);
        return op;
    }

    private boolean podeCriar(Usuario criador) {
        return criador instanceof Docente
                || criador instanceof Coordenador
                || criador instanceof Discente;
    }

    public void enviarParaAprovacao(Usuario responsavel, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.RASCUNHO) {
            throw new IllegalStateException("Apenas rascunhos podem ser enviados para aprovacao.");
        }
        op.setStatus(StatusOportunidade.AGUARDANDO_APROVACAO);
        logService.registrar(responsavel.getNome(),
                "Enviou oportunidade #" + op.getId() + " para aprovacao");
    }

    public void aprovar(Coordenador coordenador, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Oportunidade nao esta aguardando aprovacao.");
        }
        op.setStatus(StatusOportunidade.ABERTA);
        logService.registrar(coordenador.getNome(),
                "Aprovou oportunidade #" + op.getId() + " (agora ABERTA)");
    }

    public void cancelar(Usuario responsavel, Oportunidade op) {
        op.setStatus(StatusOportunidade.CANCELADA);
        logService.registrar(responsavel.getNome(),
                "Cancelou oportunidade #" + op.getId());
    }

    public void iniciarExecucao(Usuario responsavel, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.ABERTA) {
            throw new IllegalStateException("Somente oportunidades ABERTAS podem entrar em execucao.");
        }
        op.setStatus(StatusOportunidade.EM_EXECUCAO);
        logService.registrar(responsavel.getNome(),
                "Iniciou execucao da oportunidade #" + op.getId());
    }

    public void encerrar(Docente docente, Oportunidade op, List<Inscricao> concluintes) {
        if (op.getStatus() != StatusOportunidade.ABERTA
                && op.getStatus() != StatusOportunidade.EM_EXECUCAO) {
            throw new IllegalStateException("So e possivel encerrar oportunidades abertas/em execucao.");
        }
        for (Inscricao i : concluintes) {
            if (i.getStatus() == StatusInscricao.APROVADA) {
                i.setStatus(StatusInscricao.CONCLUIDA);
                i.setConcluinte(true);
                i.getDiscente().adicionarHoras(op.getCargaHorariaPrevista());
                System.out.println("[CERTIFICACAO] " + i.getDiscente().getNome()
                        + " certificado(a) com " + op.getCargaHorariaPrevista() + "h.");
            }
        }
        op.setStatus(StatusOportunidade.ENCERRADA);
        logService.registrar(docente.getNome(),
                "Encerrou oportunidade #" + op.getId() + " (concluintes: " + concluintes.size() + ")");
    }

    public List<Oportunidade> listarTodas() {
        return oportunidades;
    }

    public List<Oportunidade> listarPorStatus(StatusOportunidade status) {
        List<Oportunidade> result = new ArrayList<>();
        for (Oportunidade o : oportunidades) {
            if (o.getStatus() == status) result.add(o);
        }
        return result;
    }

    public Oportunidade buscarPorId(int id) {
        return oportunidades.stream()
                .filter(o -> o.getId() == id)
                .findFirst().orElse(null);
    }
}
