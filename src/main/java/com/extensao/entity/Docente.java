package com.extensao.entity;

import com.extensao.model.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOCENTE")
public class Docente extends Usuario {

    private String siape;
    private String departamento;

    protected Docente() {
    }

    public Docente(String nome, String email, String senha,
                   String siape, String departamento) {
        super(nome, email, senha);
        this.siape = siape;
        this.departamento = departamento;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.DOCENTE;
    }

    public String getSiape() {
        return siape;
    }

    public String getDepartamento() {
        return departamento;
    }
}
