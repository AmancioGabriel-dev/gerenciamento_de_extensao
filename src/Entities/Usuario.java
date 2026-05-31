package Entities;

import Model.TipoUsuario;

import java.time.LocalDate;

public abstract class Usuario {

    protected String nome;
    protected String email;
    protected String senha;
    protected LocalDate dataCadastro;
    protected boolean ativo;
    protected Perfil perfil;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDate.now();
        this.ativo = true;
    }

    public abstract TipoUsuario getTipo();

    public abstract void exibirDashboard();

    public boolean autenticar(String email, String senha) {
        return this.ativo && this.email.equalsIgnoreCase(email) && this.senha.equals(senha);
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s <%s>", getTipo(), nome, email);
    }
}
