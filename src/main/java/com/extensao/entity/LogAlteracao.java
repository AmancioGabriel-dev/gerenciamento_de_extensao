package com.extensao.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registro de auditoria (ENTIDADE).
 * Cada acao relevante do sistema gera uma linha aqui.
 */
@Entity
@Table(name = "log_alteracao")
public class LogAlteracao {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String usuarioResponsavel;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 1000)
    private String operacao;

    protected LogAlteracao() {
    }

    public LogAlteracao(String usuarioResponsavel, String operacao) {
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataHora = LocalDateTime.now();
        this.operacao = operacao;
    }

    public Long getId() {
        return id;
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
