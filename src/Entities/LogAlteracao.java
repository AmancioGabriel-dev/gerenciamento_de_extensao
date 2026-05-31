package Entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogAlteracao {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String usuarioResponsavel;
    private final LocalDateTime dataHora;
    private final String operacao;

    public LogAlteracao(String usuarioResponsavel, String operacao) {
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataHora = LocalDateTime.now();
        this.operacao = operacao;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getOperacao() {
        return operacao;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s",
                dataHora.format(FMT), usuarioResponsavel, operacao);
    }
}
