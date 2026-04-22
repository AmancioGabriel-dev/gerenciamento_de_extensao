package Services;

import Entities.SoliciacaoAproveitamento;
import Model.StatusSolicitacao;

public class AproveitamentoService {

    private RegraNegocioService regraService = new RegraNegocioService();

    public void processarAprovacao(SoliciacaoAproveitamento solicitacao) {
        // Simula a aprovação
        if (solicitacao.getAlunoSolicitante().getHorasDeExtensaoAcumuladas() + solicitacao.getCargaHorarioPleiteada() <= regraService.getLimiteHoras()) {
            // Se aprovado, soma as horas ao aluno
            solicitacao.getAlunoSolicitante().adicionarHoras(solicitacao.getCargaHorarioPleiteada());
            System.out.println("Solicitação de " + solicitacao.getDescricao() + " APROVADA.");
        } else {
            System.out.println("Solicitação NEGADA: Limite de horas excedido.");
        }
    }
}