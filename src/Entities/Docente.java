package Entities;

import Model.TipoUsuario;

public class Docente extends Usuario {

    private String siape;
    private String departamento;

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

    @Override
    public void exibirDashboard() {
        System.out.println("\n=========== DASHBOARD DO DOCENTE ============");
        System.out.println("Nome..........: " + nome);
        System.out.println("SIAPE.........: " + siape);
        System.out.println("Departamento..: " + departamento);
        System.out.println("=============================================");
    }

    public String getSiape() {
        return siape;
    }

    public String getDepartamento() {
        return departamento;
    }
}
