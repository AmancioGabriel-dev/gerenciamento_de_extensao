package com.extensao.service;

import com.extensao.entity.LogAlteracao;
import com.extensao.repository.LogAlteracaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servico de auditoria.
 *
 * @Service -> diz ao Spring "crie e gerencie um bean desta classe".
 * O repositorio chega pelo CONSTRUTOR (injecao de dependencia): o Spring
 * enxerga o parametro e injeta o LogAlteracaoRepository automaticamente.
 * (E o mesmo que voces faziam na mao no Main, agora automatico.)
 */
@Service
public class LogService {

    private final LogAlteracaoRepository logRepository;

    public LogService(LogAlteracaoRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void registrar(String usuarioResponsavel, String operacao) {
        logRepository.save(new LogAlteracao(usuarioResponsavel, operacao));
    }

    public List<LogAlteracao> listar() {
        return logRepository.findAll();
    }
}
