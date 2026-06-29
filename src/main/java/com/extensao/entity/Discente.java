package com.extensao.entity;

import com.extensao.model.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Subclasse de Usuario (HERANCA).
 * @DiscriminatorValue("DISCENTE") -> valor gravado na coluna "tipo_usuario"
 * para as linhas que sao discentes.
 */
@Entity
@DiscriminatorValue("DISCENTE")
public class Discente extends Usuario {

    private int matricula;
    private int semestre;
    private int horasDeExtensaoAcumuladas;

    protected Discente() {
    }

    public Discente(String nome, String email, String senha,
                    int matricula, int semestre, int horasDeExtensaoAcumuladas) {
        super(nome, email, senha);
        this.matricula = matricula;
        this.semestre = semestre;
        this.horasDeExtensaoAcumuladas = horasDeExtensaoAcumuladas;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.DISCENTE;
    }

    /** Regra de dominio: so e possivel SOMAR horas, nunca definir um valor arbitrario. */
    public void adicionarHoras(int horas) {
        this.horasDeExtensaoAcumuladas += horas;
    }

    public int getMatricula() {
        return matricula;
    }

    public int getSemestre() {
        return semestre;
    }

    public int getHorasDeExtensaoAcumuladas() {
        return horasDeExtensaoAcumuladas;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
}
