package Entities;

import Model.TipoUsuario;

public class Secretaria extends Usuario {

    private String setor;

    public Secretaria(String nome, String email, String senha, String setor) {
        super(nome, email, senha);
        this.setor = setor;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.SECRETARIA;
    }

    @Override
    public void exibirDashboard() {
        System.out.println("\n========= DASHBOARD DA SECRETARIA ===========");
        System.out.println("Nome...: " + nome);
        System.out.println("Setor..: " + setor);
        System.out.println("=============================================");
    }

    public String getSetor() {
        return setor;
    }
}
