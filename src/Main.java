import Entities.Aluno;
import Entities.Oportunidade;
import Entities.SoliciacaoAproveitamento;
import Model.Modalidade;
import Model.StatusOportunidade;
import Model.StatusSolicitacao;
import Services.AproveitamentoService;
import Services.RegraNegocioService;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciar Aluno e Oportunidade
        Aluno aluno = new Aluno("João Silva", 2023001, "joao@universidade.edu", 3, 100);

        Oportunidade op = new Oportunidade(
                "Projeto de Extensão IA", "Desenvolvimento de Chatbots",
                Modalidade.PROJETO, 40,
                LocalDate.now(), LocalDate.now().plusMonths(2),
                5, StatusOportunidade.ABERTA
        );

        System.out.println("--- Início da Simulação ---");
        System.out.println("Aluno: " + aluno.getNome() + " | Horas atuais: " + aluno.getHorasDeExtensaoAcumuladas() + "h");

        // 2. Simular inscrição (Lógica simples de console)
        System.out.println("Inscrito na oportunidade: " + op.getTitulo());

        // 3. Criar solicitação de aproveitamento externa (ex: Certificado de 40h)
        SoliciacaoAproveitamento solicitacao = new SoliciacaoAproveitamento(
                aluno, "Curso de Python Externo", 40,
                LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 20),
                "certificado.pdf", "", StatusSolicitacao.PENDENTE, LocalDate.now()
        );

        // 4. Simular aprovação do Coordenador
        AproveitamentoService service = new AproveitamentoService();
        service.processarAprovacao(solicitacao);

        // 5. Imprimir Extrato
        RegraNegocioService regras = new RegraNegocioService();
        int horasAtuais = aluno.getHorasDeExtensaoAcumuladas();
        int faltam = regras.getLimiteHoras() - horasAtuais;

        System.out.println("\n--- EXTRATO DE HORAS ---");
        System.out.println("Total Acumulado: " + horasAtuais + "h");
        System.out.println("Objetivo: " + regras.getLimiteHoras() + "h");

        if (faltam > 0) {
            System.out.println("Status: Faltam " + faltam + "h para completar a carga obrigatória.");
        } else {
            System.out.println("Status: Carga horária de extensão COMPLETA!");
        }
    }
}