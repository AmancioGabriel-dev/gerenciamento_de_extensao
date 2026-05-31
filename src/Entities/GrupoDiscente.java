package Entities;

import Model.TipoCargo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GrupoDiscente {

    private String nome;
    private String descricao;
    private String email;
    private Docente responsavel;
    private final List<Discente> membros;
    private final List<Cargo> cargosAtivos;
    private final List<HistoricoCargo> historicoCargos;

    public GrupoDiscente(String nome, String descricao, String email, Docente responsavel) {
        this.nome = nome;
        this.descricao = descricao;
        this.email = email;
        this.responsavel = responsavel;
        this.membros = new ArrayList<>();
        this.cargosAtivos = new ArrayList<>();
        this.historicoCargos = new ArrayList<>();
    }

    public void adicionarMembro(Discente discente) {
        if (!membros.contains(discente)) {
            membros.add(discente);
        }
    }

    public void removerMembro(Discente discente) {
        membros.remove(discente);
        cargosAtivos.stream()
                .filter(c -> c.getOcupante().equals(discente))
                .forEach(c -> encerrarCargo(c, LocalDate.now()));
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
        cargosAtivos.remove(cargo);
        historicoCargos.add(new HistoricoCargo(cargo));
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
