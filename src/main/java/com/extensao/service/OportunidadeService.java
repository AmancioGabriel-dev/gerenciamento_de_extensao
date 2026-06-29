package com.extensao.service;

import com.extensao.entity.*;
import com.extensao.model.Modalidade;
import com.extensao.model.StatusInscricao;
import com.extensao.model.StatusOportunidade;
import com.extensao.repository.OportunidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Ciclo de vida da Oportunidade (maquina de estados) e suas regras.
 * Toda a logica de status/validacao foi preservada do projeto original.
 */
@Service
public class OportunidadeService {

    private final OportunidadeRepository oportunidadeRepository;
    private final LogService logService;

    public OportunidadeService(OportunidadeRepository oportunidadeRepository, LogService logService) {
        this.oportunidadeRepository = oportunidadeRepository;
        this.logService = logService;
    }

    @Transactional
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
        op = oportunidadeRepository.save(op);
        logService.registrar(criador.getNome(),
                "Criou oportunidade #" + op.getId() + " - " + titulo);
        return op;
    }

    private boolean podeCriar(Usuario criador) {
        return criador instanceof Docente
                || criador instanceof Coordenador
                || criador instanceof Discente;
    }

    @Transactional
    public void enviarParaAprovacao(Usuario responsavel, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.RASCUNHO) {
            throw new IllegalStateException("Apenas rascunhos podem ser enviados para aprovacao.");
        }
        op.setStatus(StatusOportunidade.AGUARDANDO_APROVACAO);
        oportunidadeRepository.save(op);
        logService.registrar(responsavel.getNome(),
                "Enviou oportunidade #" + op.getId() + " para aprovacao");
    }

    @Transactional
    public void aprovar(Coordenador coordenador, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Oportunidade nao esta aguardando aprovacao.");
        }
        op.setStatus(StatusOportunidade.ABERTA);
        oportunidadeRepository.save(op);
        logService.registrar(coordenador.getNome(),
                "Aprovou oportunidade #" + op.getId() + " (agora ABERTA)");
    }

    @Transactional
    public void cancelar(Usuario responsavel, Oportunidade op) {
        op.setStatus(StatusOportunidade.CANCELADA);
        oportunidadeRepository.save(op);
        logService.registrar(responsavel.getNome(),
                "Cancelou oportunidade #" + op.getId());
    }

    @Transactional
    public void iniciarExecucao(Usuario responsavel, Oportunidade op) {
        if (op.getStatus() != StatusOportunidade.ABERTA) {
            throw new IllegalStateException("Somente oportunidades ABERTAS podem entrar em execucao.");
        }
        op.setStatus(StatusOportunidade.EM_EXECUCAO);
        oportunidadeRepository.save(op);
        logService.registrar(responsavel.getNome(),
                "Iniciou execucao da oportunidade #" + op.getId());
    }

    @Transactional
    public void encerrar(Docente docente, Oportunidade op, List<Inscricao> concluintes) {
        if (op.getStatus() != StatusOportunidade.ABERTA
                && op.getStatus() != StatusOportunidade.EM_EXECUCAO) {
            throw new IllegalStateException("So e possivel encerrar oportunidades abertas/em execucao.");
        }
        for (Inscricao i : concluintes) {
            if (i.getStatus() == StatusInscricao.APROVADA) {
                i.setStatus(StatusInscricao.CONCLUIDA);
                i.setConcluinte(true);
                // Certificacao: credita as horas previstas ao discente concluinte.
                i.getDiscente().adicionarHoras(op.getCargaHorariaPrevista());
            }
        }
        op.setStatus(StatusOportunidade.ENCERRADA);
        oportunidadeRepository.save(op);
        logService.registrar(docente.getNome(),
                "Encerrou oportunidade #" + op.getId() + " (concluintes: " + concluintes.size() + ")");
    }

    @Transactional
    public Oportunidade adicionarAnexo(Oportunidade op, String anexo) {
        op.adicionarAnexo(anexo);
        return oportunidadeRepository.save(op);
    }

    public List<Oportunidade> listarTodas() {
        return oportunidadeRepository.findAll();
    }

    public List<Oportunidade> listarPorStatus(StatusOportunidade status) {
        return oportunidadeRepository.findByStatus(status);
    }

    public Oportunidade buscarPorId(Long id) {
        return oportunidadeRepository.findById(id).orElse(null);
    }
}
