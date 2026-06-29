package com.extensao.entity;

import com.extensao.model.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COORDENADOR")
public class Coordenador extends Usuario {

    private String siape;
    private String curso;

    protected Coordenador() {
    }

    public Coordenador(String nome, String email, String senha,
                       String siape, String curso) {
        super(nome, email, senha);
        this.siape = siape;
        this.curso = curso;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.COORDENADOR;
    }

    public String getSiape() {
        return siape;
    }

    public String getCurso() {
        return curso;
    }
}
