package com.extensao.entity;

import com.extensao.model.Acao;
import com.extensao.model.Modulo;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Perfil de acesso (ENTIDADE).
 *
 * As permissoes sao um CONJUNTO de value objects Permissao.
 * @ElementCollection -> cria uma tabela auxiliar "perfil_permissoes" que guarda
 * os pares (modulo, acao) de cada perfil. Substitui o Map<Modulo,Set<Acao>>
 * da versao em memoria, mantendo o mesmo comportamento.
 */
@Entity
@Table(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "perfil_permissoes", joinColumns = @JoinColumn(name = "perfil_id"))
    private Set<Permissao> permissoes = new HashSet<>();

    protected Perfil() {
    }

    public Perfil(String nome) {
        this.nome = nome;
    }

    public void concederPermissao(Modulo modulo, Acao acao) {
        permissoes.add(new Permissao(modulo, acao));
    }

    public void revogarPermissao(Modulo modulo, Acao acao) {
        permissoes.remove(new Permissao(modulo, acao));
    }

    public boolean possuiPermissao(Modulo modulo, Acao acao) {
        return permissoes.contains(new Permissao(modulo, acao));
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Set<Permissao> getPermissoes() {
        return permissoes;
    }
}
