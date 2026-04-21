package Entities;

public class Aluno {
    private String nome;
    private int matricula;
    private  String email;
    private int semestre;
    private int horasDeExtensaoAcumuladas;

    public Aluno(String nome , int matricula , String email ,
                 int semestre , int horasDeExtensaoAcumuladas){
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
        this.semestre = semestre;
        this.horasDeExtensaoAcumuladas = horasDeExtensaoAcumuladas;
    }

    public String getNome() {
        return nome;
    }

    public int getMatricula() {
        return matricula;
    }

    public String getEmail() {
        return email;
    }

    public int getSemestre() {
        return semestre;
    }

    public int getHorasDeExtensaoAcumuladas() {
        return horasDeExtensaoAcumuladas;
    }
}
