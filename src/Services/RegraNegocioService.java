package Services;

import Entities.Discente;
import Entities.SolicitacaoAproveitamento;
import Model.StatusSolicitacao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RegraNegocioService {

    private static final int LIMITE_HORAS_CC = 345;
    private static final int PRAZO_COORDENADOR_DIAS = 10;
    private static final int PRAZO_REENVIO_DISCENTE_DIAS = 5;

    public boolean atingiuCargaHorariaTotal(Discente discente) {
        return discente.getHorasDeExtensaoAcumuladas() >= LIMITE_HORAS_CC;
    }

    public boolean prazoCoordenadorValido(SolicitacaoAproveitamento solicitacao, LocalDate dataAnalise) {
        long dias = ChronoUnit.DAYS.between(solicitacao.getDataSubmissao(), dataAnalise);
        return dias <= PRAZO_COORDENADOR_DIAS;
    }

    public long diasRestantesCoordenador(SolicitacaoAproveitamento solicitacao, LocalDate hoje) {
        long decorridos = ChronoUnit.DAYS.between(solicitacao.getDataSubmissao(), hoje);
        return PRAZO_COORDENADOR_DIAS - decorridos;
    }

    public boolean prazoReenvioValido(SolicitacaoAproveitamento solicitacao, LocalDate hoje) {
        if (solicitacao.getStatus() != StatusSolicitacao.INDEFERIDO) return false;
        if (solicitacao.getDataDecisao() == null) return false;
        long dias = ChronoUnit.DAYS.between(solicitacao.getDataDecisao(), hoje);
        return dias <= PRAZO_REENVIO_DISCENTE_DIAS;
    }

    public long diasRestantesReenvio(SolicitacaoAproveitamento solicitacao, LocalDate hoje) {
        if (solicitacao.getDataDecisao() == null) return PRAZO_REENVIO_DISCENTE_DIAS;
        long decorridos = ChronoUnit.DAYS.between(solicitacao.getDataDecisao(), hoje);
        return PRAZO_REENVIO_DISCENTE_DIAS - decorridos;
    }

    public int calcularHorasConvertidas(String tipoAtividade, int quantidade) {
        return switch (tipoAtividade.toUpperCase()) {
            case "PET" -> 110;
            case "PIBITI", "ONG" -> 45;
            case "LIGA", "EMPRESA_JUNIOR" -> 30;
            case "AUDIOVISUAL" -> 15 * quantidade;
            default -> 0;
        };
    }

    public int getLimiteHoras() {
        return LIMITE_HORAS_CC;
    }

    public int getPrazoCoordenadorDias() {
        return PRAZO_COORDENADOR_DIAS;
    }

    public int getPrazoReenvioDiscenteDias() {
        return PRAZO_REENVIO_DISCENTE_DIAS;
    }
}
