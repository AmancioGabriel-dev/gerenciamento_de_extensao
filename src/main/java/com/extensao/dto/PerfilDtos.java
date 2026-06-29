package com.extensao.dto;

import com.extensao.entity.Perfil;
import com.extensao.model.Acao;
import com.extensao.model.Modulo;

import java.util.List;

public final class PerfilDtos {

    private PerfilDtos() {
    }

    public record AlterarPermissaoRequest(
            String responsavel, Modulo modulo, Acao acao, boolean conceder) {
    }

    public record PerfilResponse(Long id, String nome, List<String> permissoes) {
        public static PerfilResponse from(Perfil p) {
            List<String> perms = p.getPermissoes().stream()
                    .map(perm -> perm.getModulo() + ":" + perm.getAcao())
                    .sorted()
                    .toList();
            return new PerfilResponse(p.getId(), p.getNome(), perms);
        }
    }
}
