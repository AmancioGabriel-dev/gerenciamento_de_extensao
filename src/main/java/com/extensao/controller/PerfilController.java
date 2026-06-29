package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.PerfilDtos.*;
import com.extensao.entity.Perfil;
import com.extensao.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public List<PerfilResponse> listar() {
        return perfilService.listar().stream().map(PerfilResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PerfilResponse buscar(@PathVariable Long id) {
        return PerfilResponse.from(carregar(id));
    }

    @PatchMapping("/{id}/permissoes")
    public PerfilResponse alterarPermissao(@PathVariable Long id,
                                           @Valid @RequestBody AlterarPermissaoRequest r) {
        Perfil p = carregar(id);
        perfilService.alterarPermissao(r.responsavel(), p, r.modulo(), r.acao(), r.conceder());
        return PerfilResponse.from(p);
    }

    private Perfil carregar(Long id) {
        Perfil p = perfilService.buscarPorId(id);
        if (p == null) {
            throw new RecursoNaoEncontradoException("Perfil " + id + " nao encontrado.");
        }
        return p;
    }
}
