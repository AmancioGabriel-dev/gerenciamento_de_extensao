package Services;

import Entities.Aluno;
import Entities.SoliciacaoAproveitamento;
import Model.Modalidade;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RegraNegocioService {

    private static final int LIMITE_HORAS_CC = 345;

    // 1. Validação de Carga Horária Total
    public boolean atingiuCargaHorariaTotal(Aluno aluno) {
        return aluno.getHorasDeExtensaoAcumuladas() >= LIMITE_HORAS_CC;
    }

    // 2. Controle de Prazos
    // Verifica se a análise do coordenador está dentro do prazo de 10 dias
    public boolean prazoCoordenadorValido(SoliciacaoAproveitamento solicitacao, LocalDate dataAnalise) {
        long dias = ChronoUnit.DAYS.between(solicitacao.getDataSubmissao(), dataAnalise);
        return dias <= 10;
    }

    // 3. Cálculo de Horas por Atividade (Tabela de Conversão)
    // Nota: Usei a descrição para identificar o tipo, já que a Modalidade é genérica
    public int calcularHorasConvertidas(String tipoAtividade, int quantidade) {
        return switch (tipoAtividade.toUpperCase()) {
            case "PET" -> 110; // Por semestre
            case "PIBITI", "ONG" -> 45; // Por semestre
            case "LIGA", "EMPRESA_JUNIOR" -> 30; // Por semestre
            case "AUDIOVISUAL" -> 15 * quantidade; // 15h por produto
            default -> 0;
        };
    }

    public int getLimiteHoras() {
        return LIMITE_HORAS_CC;
    }
}