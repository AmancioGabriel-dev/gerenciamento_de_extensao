package Services;

import Entities.Cargo;
import Entities.Discente;
import Entities.Docente;
import Entities.GrupoDiscente;
import Entities.HistoricoCargo;
import Model.TipoCargo;

import java.util.ArrayList;
import java.util.List;

public class GrupoService {

    private final List<GrupoDiscente> grupos = new ArrayList<>();
    private final LogService logService;

    public GrupoService(LogService logService) {
        this.logService = logService;
    }

    public GrupoDiscente criarGrupo(String responsavelLog, String nome, String descricao,
                                    String email, Docente docenteResponsavel) {
        GrupoDiscente g = new GrupoDiscente(nome, descricao, email, docenteResponsavel);
        grupos.add(g);
        logService.registrar(responsavelLog,
                "Criou grupo discente '" + nome + "' sob responsavel " + docenteResponsavel.getNome());
        return g;
    }

    public Cargo atribuirCargo(String responsavelLog, GrupoDiscente grupo,
                               TipoCargo tipo, Discente discente) {
        Cargo c = grupo.atribuirCargo(tipo, discente);
        logService.registrar(responsavelLog,
                "Atribuiu cargo " + tipo + " a " + discente.getNome() + " no grupo " + grupo.getNome());
        return c;
    }

    public boolean removerCargo(String responsavelLog, GrupoDiscente grupo,
                                TipoCargo tipo, Discente discente) {
        boolean ok = grupo.removerCargo(tipo, discente);
        if (ok) {
            logService.registrar(responsavelLog,
                    "Removeu cargo " + tipo + " de " + discente.getNome() + " no grupo " + grupo.getNome());
        }
        return ok;
    }

    public List<GrupoDiscente> listar() {
        return grupos;
    }

    public List<HistoricoCargo> historicoDoGrupo(GrupoDiscente grupo) {
        return grupo.getHistoricoCargos();
    }
}
