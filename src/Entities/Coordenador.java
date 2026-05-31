package Entities;

import Model.TipoUsuario;

public class Coordenador extends Usuario {

    private String siape;
    private String curso;

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

    @Override
    public void exibirDashboard() {
        System.out.println("\n========= DASHBOARD DO COORDENADOR ==========");
        System.out.println("Nome...: " + nome);
        System.out.println("SIAPE..: " + siape);
        System.out.println("Curso..: " + curso);
        System.out.println("=============================================");
    }

    public String getSiape() {
        return siape;
    }

    public String getCurso() {
        return curso;
    }
}
