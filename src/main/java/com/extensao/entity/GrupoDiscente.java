package com.extensao.entity;

import com.extensao.model.TipoCargo;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Grupo discente (ENTIDADE / raiz de agregado).
 *
 *  - membros        : @ManyToMany com Discente
 *  - cargosAtivos   : @OneToMany de Cargo (entidades com ciclo de vida)
 *  - historicoCargos: @ElementCollection de HistoricoCargo (value objects)
 *
 * A logica de atribuir/remover cargo (incluindo virar historico) fica
 * DENTRO da entidade -- encapsulamento de comportamento de dominio.
 */
@Entity
@Table(name = "grupo_discente")
public class GrupoDiscente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsavel_id")
    private Docente responsavel;

    @ManyToMany
    @JoinTable(name = "grupo_membros",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "discente_id"))
    private List<Discente> membros = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "grupo_id")
    private List<Cargo> cargosAtivos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "grupo_historico_cargos", joinColumns = @JoinColumn(name = "grupo_id"))
    private List<HistoricoCargo> historicoCargos = new ArrayList<>();

    protected GrupoDiscente() {
    }

    public GrupoDiscente(String nome, String descricao, String email, Docente responsavel) {
        this.nome = nome;
        this.descricao = descricao;
        this.email = email;
        this.responsavel = responsavel;
    }

    public void adicionarMembro(Discente discente) {
        if (!membros.contains(discente)) {
            membros.add(discente);
        }
    }

    public void removerMembro(Discente discente) {
        membros.remove(discente);
        for (Cargo c : new ArrayList<>(cargosAtivos)) {
            if (c.getOcupante().equals(discente)) {
                encerrarCargo(c, LocalDate.now());
            }
        }
    }

    public Cargo atribuirCargo(TipoCargo tipo, Discente discente) {
        if (!membros.contains(discente)) {
            adicionarMembro(discente);
        }
        Cargo cargo = new Cargo(tipo, discente, LocalDate.now());
        cargosAtivos.add(cargo);
        return cargo;
    }

    public boolean removerCargo(TipoCargo tipo, Discente discente) {
        for (Cargo c : new ArrayList<>(cargosAtivos)) {
            if (c.getTipo() == tipo && c.getOcupante().equals(discente)) {
                encerrarCargo(c, LocalDate.now());
                return true;
            }
        }
        return false;
    }

    private void encerrarCargo(Cargo cargo, LocalDate fim) {
        cargo.encerrar(fim);
        historicoCargos.add(new HistoricoCargo(cargo));
        cargosAtivos.remove(cargo);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getEmail() {
        return email;
    }

    public Docente getResponsavel() {
        return responsavel;
    }

    public List<Discente> getMembros() {
        return membros;
    }

    public List<Cargo> getCargosAtivos() {
        return cargosAtivos;
    }

    public List<HistoricoCargo> getHistoricoCargos() {
        return historicoCargos;
    }

    @Override
    public String toString() {
        return String.format("Grupo '%s' (%s) - Responsavel: %s - Membros: %d",
                nome, email, responsavel.getNome(), membros.size());
    }
}
