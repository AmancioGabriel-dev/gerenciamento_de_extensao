package com.extensao.entity;

import com.extensao.model.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMISSAO")
public class Comissao extends Usuario {

    private String area;

    protected Comissao() {
    }

    public Comissao(String nome, String email, String senha, String area) {
        super(nome, email, senha);
        this.area = area;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.COMISSAO;
    }

    public String getArea() {
        return area;
    }
}
