package Entities;

import Model.TipoUsuario;

public class Comissao extends Usuario {

    private String area;

    public Comissao(String nome, String email, String senha, String area) {
        super(nome, email, senha);
        this.area = area;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.COMISSAO;
    }

    @Override
    public void exibirDashboard() {
        System.out.println("\n========== DASHBOARD DA COMISSAO ============");
        System.out.println("Nome..: " + nome);
        System.out.println("Area..: " + area);
        System.out.println("=============================================");
    }

    public String getArea() {
        return area;
    }
}
