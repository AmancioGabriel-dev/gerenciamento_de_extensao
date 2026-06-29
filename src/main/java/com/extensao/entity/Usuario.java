package com.extensao.entity;

import com.extensao.model.TipoUsuario;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade base de todos os usuarios.
 *
 * CONCEITOS NOVOS (JPA):
 *  - @Entity            -> esta classe vira uma tabela no banco.
 *  - @Inheritance(SINGLE_TABLE) -> toda a hierarquia (Discente, Docente...) fica
 *    em UMA UNICA tabela "usuario". Uma coluna "tipo_usuario" diz qual e o tipo.
 *    (Era exatamente o papel do enum TipoUsuario no console.)
 *  - @DiscriminatorColumn -> nome dessa coluna que distingue os tipos.
 *  - @Id + @GeneratedValue -> agora o BANCO gera o id (adeus "static contador").
 *
 * CONCEITO DE POO mantido: continua ABSTRATA (abstract) com o metodo abstrato
 * getTipo(), que cada subclasse e obrigada a implementar -> polimorfismo.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    protected String nome;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false)
    protected String senha;

    @Column(nullable = false)
    protected LocalDate dataCadastro;

    @Column(nullable = false)
    protected boolean ativo;

    // Muitos usuarios podem compartilhar o mesmo perfil (ManyToOne).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id")
    protected Perfil perfil;

    /** Construtor sem argumentos exigido pelo JPA (protegido). */
    protected Usuario() {
    }

    protected Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDate.now();
        this.ativo = true;
    }

    /** Cada subclasse declara o seu tipo -> polimorfismo. */
    public abstract TipoUsuario getTipo();

    public boolean autenticar(String email, String senha) {
        return this.ativo && this.email.equalsIgnoreCase(email) && this.senha.equals(senha);
    }

    public Long getId() {
        return id;
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

    /**
     * Em JPA, a identidade da entidade e o id. Implementamos equals/hashCode
     * por id para que ".equals()" e "List.contains()" (muito usados nos services)
     * funcionem mesmo com objetos carregados separadamente do banco.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s <%s>", getTipo(), nome, email);
    }
}
