package com.extensao.entity;

import com.extensao.model.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SECRETARIA")
public class Secretaria extends Usuario {

    private String setor;

    protected Secretaria() {
    }

    public Secretaria(String nome, String email, String senha, String setor) {
        super(nome, email, senha);
        this.setor = setor;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.SECRETARIA;
    }

    public String getSetor() {
        return setor;
    }
}
