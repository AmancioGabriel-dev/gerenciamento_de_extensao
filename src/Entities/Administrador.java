package Entities;

import Model.TipoUsuario;

public class Administrador extends Usuario {

    public Administrador(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.ADMINISTRADOR;
    }

    @Override
    public void exibirDashboard() {
        System.out.println("\n======== DASHBOARD DO ADMINISTRADOR =========");
        System.out.println("Nome.: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Acesso total ao sistema.");
        System.out.println("=============================================");
    }
}
