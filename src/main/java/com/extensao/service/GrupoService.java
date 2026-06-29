package com.extensao.service;

import com.extensao.entity.Cargo;
import com.extensao.entity.Discente;
import com.extensao.entity.Docente;
import com.extensao.entity.GrupoDiscente;
import com.extensao.entity.HistoricoCargo;
import com.extensao.model.TipoCargo;
import com.extensao.repository.GrupoDiscenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Grupos discentes e cargos. A logica de dominio (atribuir/encerrar cargo,
 * gerar historico) vive dentro da entidade GrupoDiscente; aqui apenas
 * orquestramos e persistimos.
 */
@Service
public class GrupoService {

    private final GrupoDiscenteRepository grupoRepository;
    private final LogService logService;

    public GrupoService(GrupoDiscenteRepository grupoRepository, LogService logService) {
        this.grupoRepository = grupoRepository;
        this.logService = logService;
    }

    @Transactional
    public GrupoDiscente criarGrupo(String responsavelLog, String nome, String descricao,
                                    String email, Docente docenteResponsavel) {
        GrupoDiscente g = new GrupoDiscente(nome, descricao, email, docenteResponsavel);
        g = grupoRepository.save(g);
        logService.registrar(responsavelLog,
                "Criou grupo discente '" + nome + "' sob responsavel " + docenteResponsavel.getNome());
        return g;
    }

    @Transactional
    public Cargo atribuirCargo(String responsavelLog, GrupoDiscente grupo,
                               TipoCargo tipo, Discente discente) {
        Cargo c = grupo.atribuirCargo(tipo, discente);
        grupoRepository.save(grupo);
        logService.registrar(responsavelLog,
                "Atribuiu cargo " + tipo + " a " + discente.getNome() + " no grupo " + grupo.getNome());
        return c;
    }

    @Transactional
    public boolean removerCargo(String responsavelLog, GrupoDiscente grupo,
                                TipoCargo tipo, Discente discente) {
        boolean ok = grupo.removerCargo(tipo, discente);
        if (ok) {
            grupoRepository.save(grupo);
            logService.registrar(responsavelLog,
                    "Removeu cargo " + tipo + " de " + discente.getNome() + " no grupo " + grupo.getNome());
        }
        return ok;
    }

    public GrupoDiscente buscarPorId(Long id) {
        return grupoRepository.findById(id).orElse(null);
    }

    public List<GrupoDiscente> listar() {
        return grupoRepository.findAll();
    }

    public List<GrupoDiscente> listarPorResponsavel(Docente docente) {
        return grupoRepository.findByResponsavel(docente);
    }

    public List<HistoricoCargo> historicoDoGrupo(GrupoDiscente grupo) {
        return grupo.getHistoricoCargos();
    }
}
