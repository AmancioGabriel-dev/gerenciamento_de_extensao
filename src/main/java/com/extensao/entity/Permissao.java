package com.extensao.entity;

import com.extensao.model.Acao;
import com.extensao.model.Modulo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * VALUE OBJECT (objeto de valor).
 *
 * @Embeddable -> nao vira tabela propria; e "embutido" na tabela dona
 * (aqui, na colecao de permissoes do Perfil).
 *
 * Caracteristicas classicas de value object:
 *  - sem id proprio (sua identidade SAO os valores que carrega)
 *  - imutavel (campos finais, sem setters)
 *  - dois objetos com mesmos valores sao IGUAIS (equals/hashCode por valor)
 *
 * Representa o par (Modulo, Acao), ex.: (OPORTUNIDADES, APROVAR).
 */
@Embeddable
public class Permissao {

    @Enumerated(EnumType.STRING)
    private Modulo modulo;

    @Enumerated(EnumType.STRING)
    private Acao acao;

    /** Exigido pelo JPA. */
    protected Permissao() {
    }

    public Permissao(Modulo modulo, Acao acao) {
        this.modulo = modulo;
        this.acao = acao;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public Acao getAcao() {
        return acao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permissao that)) return false;
        return modulo == that.modulo && acao == that.acao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modulo, acao);
    }

    @Override
    public String toString() {
        return modulo + ":" + acao;
    }
}
