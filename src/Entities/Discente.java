package Entities;

import Model.TipoUsuario;

public class Discente extends Usuario {

    private int matricula;
    private int semestre;
    private int horasDeExtensaoAcumuladas;

    public Discente(String nome, String email, String senha,
                    int matricula, int semestre, int horasDeExtensaoAcumuladas) {
        super(nome, email, senha);
        this.matricula = matricula;
        this.semestre = semestre;
        this.horasDeExtensaoAcumuladas = horasDeExtensaoAcumuladas;
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.DISCENTE;
    }

    @Override
    public void exibirDashboard() {
        System.out.println("\n=========== DASHBOARD DO DISCENTE ===========");
        System.out.println("Nome.......: " + nome);
        System.out.println("Matricula..: " + matricula);
        System.out.println("Semestre...: " + semestre);
        System.out.println("Horas......: " + horasDeExtensaoAcumuladas + "h acumuladas");
        System.out.println("=============================================");
    }

    public void adicionarHoras(int horas) {
        this.horasDeExtensaoAcumuladas += horas;
    }

    public int getMatricula() {
        return matricula;
    }

    public int getSemestre() {
        return semestre;
    }

    public int getHorasDeExtensaoAcumuladas() {
        return horasDeExtensaoAcumuladas;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
}
