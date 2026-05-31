package Entities;

import Model.Acao;
import Model.Modulo;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Perfil {

    private String nome;
    private final Map<Modulo, Set<Acao>> permissoes;

    public Perfil(String nome) {
        this.nome = nome;
        this.permissoes = new EnumMap<>(Modulo.class);
    }

    public void concederPermissao(Modulo modulo, Acao acao) {
        permissoes.computeIfAbsent(modulo, m -> EnumSet.noneOf(Acao.class)).add(acao);
    }

    public void revogarPermissao(Modulo modulo, Acao acao) {
        Set<Acao> acoes = permissoes.get(modulo);
        if (acoes != null) {
            acoes.remove(acao);
        }
    }

    public boolean possuiPermissao(Modulo modulo, Acao acao) {
        Set<Acao> acoes = permissoes.get(modulo);
        return acoes != null && acoes.contains(acao);
    }

    public String getNome() {
        return nome;
    }

    public Map<Modulo, Set<Acao>> getPermissoes() {
        return permissoes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Perfil '" + nome + "': ");
        if (permissoes.isEmpty()) {
            sb.append("(sem permissoes)");
        } else {
            permissoes.forEach((modulo, acoes) ->
                    sb.append("\n  - ").append(modulo).append(" => ").append(acoes));
        }
        return sb.toString();
    }
}
