package com.extensao.repository;

import com.extensao.entity.Discente;
import com.extensao.entity.SolicitacaoAproveitamento;
import com.extensao.model.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoAproveitamentoRepository extends JpaRepository<SolicitacaoAproveitamento, Long> {

    List<SolicitacaoAproveitamento> findByAlunoSolicitante(Discente discente);

    List<SolicitacaoAproveitamento> findByStatusAndDelegadaParaComissao(StatusSolicitacao status, boolean delegada);
}
