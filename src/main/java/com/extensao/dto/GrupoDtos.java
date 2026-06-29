package com.extensao.dto;

import com.extensao.entity.Cargo;
import com.extensao.entity.Discente;
import com.extensao.entity.GrupoDiscente;
import com.extensao.entity.HistoricoCargo;
import com.extensao.model.TipoCargo;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public final class GrupoDtos {

    private GrupoDtos() {
    }

    public record CriarGrupoRequest(
            String responsavelLog, @NotBlank String nome, String descricao,
            String email, Long docenteResponsavelId) {
    }

    public record CargoRequest(String responsavelLog, TipoCargo tipo, Long discenteId) {
    }

    public record GrupoResponse(
            Long id, String nome, String descricao, String email, String responsavel,
            List<String> membros, List<String> cargosAtivos, List<String> historicoCargos) {

        public static GrupoResponse from(GrupoDiscente g) {
            return new GrupoResponse(
                    g.getId(), g.getNome(), g.getDescricao(), g.getEmail(),
                    g.getResponsavel() == null ? null : g.getResponsavel().getNome(),
                    g.getMembros().stream().map(Discente::getNome).toList(),
                    g.getCargosAtivos().stream().map(Cargo::toString).toList(),
                    g.getHistoricoCargos().stream().map(HistoricoCargo::toString).toList());
        }
    }
}
