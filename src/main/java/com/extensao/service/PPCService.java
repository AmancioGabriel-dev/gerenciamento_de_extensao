package com.extensao.service;

import com.extensao.entity.PPC;
import com.extensao.repository.PPCRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Versionamento do PPC. Ao cadastrar uma nova versao, encerra a vigencia da
 * anterior (se ainda aberta). Logica preservada do original.
 */
@Service
public class PPCService {

    private final PPCRepository ppcRepository;
    private final LogService logService;

    public PPCService(PPCRepository ppcRepository, LogService logService) {
        this.ppcRepository = ppcRepository;
        this.logService = logService;
    }

    @Transactional
    public PPC cadastrarVersao(String responsavelLog, String versao,
                               int cargaHorariaMinima, String autor,
                               LocalDate vigenciaInicio, LocalDate vigenciaFim) {
        ppcRepository.findTopByOrderByIdDesc().ifPresent(anterior -> {
            if (anterior.getVigenciaFim() == null) {
                anterior.encerrarVigencia(vigenciaInicio.minusDays(1));
                ppcRepository.save(anterior);
            }
        });
        PPC novo = new PPC(versao, cargaHorariaMinima, autor, vigenciaInicio, vigenciaFim);
        novo = ppcRepository.save(novo);
        logService.registrar(responsavelLog,
                "Cadastrou nova versao do PPC: v" + versao + " (CH min " + cargaHorariaMinima + "h)");
        return novo;
    }

    public PPC obterVigente() {
        LocalDate hoje = LocalDate.now();
        List<PPC> todos = ppcRepository.findAll();
        for (int i = todos.size() - 1; i >= 0; i--) {
            if (todos.get(i).estaVigente(hoje)) return todos.get(i);
        }
        return null;
    }

    public List<PPC> historico() {
        return ppcRepository.findAll();
    }
}
