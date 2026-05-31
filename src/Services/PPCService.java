package Services;

import Entities.PPC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PPCService {

    private final List<PPC> historico = new ArrayList<>();
    private final LogService logService;

    public PPCService(LogService logService) {
        this.logService = logService;
    }

    public PPC cadastrarVersao(String responsavelLog, String versao,
                               int cargaHorariaMinima, String autor,
                               LocalDate vigenciaInicio, LocalDate vigenciaFim) {
        if (!historico.isEmpty()) {
            PPC anterior = historico.get(historico.size() - 1);
            if (anterior.getVigenciaFim() == null) {
                anterior.encerrarVigencia(vigenciaInicio.minusDays(1));
            }
        }
        PPC novo = new PPC(versao, cargaHorariaMinima, autor, vigenciaInicio, vigenciaFim);
        historico.add(novo);
        logService.registrar(responsavelLog,
                "Cadastrou nova versao do PPC: v" + versao + " (CH min " + cargaHorariaMinima + "h)");
        return novo;
    }

    public PPC obterVigente() {
        LocalDate hoje = LocalDate.now();
        for (int i = historico.size() - 1; i >= 0; i--) {
            if (historico.get(i).estaVigente(hoje)) return historico.get(i);
        }
        return null;
    }

    public List<PPC> historico() {
        return historico;
    }
}
