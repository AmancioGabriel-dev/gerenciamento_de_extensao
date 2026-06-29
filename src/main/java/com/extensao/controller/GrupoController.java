package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.GrupoDtos.*;
import com.extensao.entity.Discente;
import com.extensao.entity.Docente;
import com.extensao.entity.GrupoDiscente;
import com.extensao.entity.Usuario;
import com.extensao.service.GrupoService;
import com.extensao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService grupoService;
    private final UsuarioService usuarioService;

    public GrupoController(GrupoService grupoService, UsuarioService usuarioService) {
        this.grupoService = grupoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrupoResponse criar(@Valid @RequestBody CriarGrupoRequest r) {
        Docente docente = docenteOrThrow(r.docenteResponsavelId());
        GrupoDiscente g = grupoService.criarGrupo(
                r.responsavelLog(), r.nome(), r.descricao(), r.email(), docente);
        return GrupoResponse.from(g);
    }

    @GetMapping
    public List<GrupoResponse> listar() {
        return grupoService.listar().stream().map(GrupoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public GrupoResponse buscar(@PathVariable Long id) {
        return GrupoResponse.from(grupoOrThrow(id));
    }

    @GetMapping("/{id}/historico")
    public List<String> historico(@PathVariable Long id) {
        return grupoService.historicoDoGrupo(grupoOrThrow(id)).stream()
                .map(Object::toString).toList();
    }

    @PostMapping("/{id}/cargos")
    public GrupoResponse atribuirCargo(@PathVariable Long id, @RequestBody CargoRequest r) {
        GrupoDiscente g = grupoOrThrow(id);
        grupoService.atribuirCargo(r.responsavelLog(), g, r.tipo(), discenteOrThrow(r.discenteId()));
        return GrupoResponse.from(g);
    }

    @PostMapping("/{id}/cargos/remover")
    public GrupoResponse removerCargo(@PathVariable Long id, @RequestBody CargoRequest r) {
        GrupoDiscente g = grupoOrThrow(id);
        boolean ok = grupoService.removerCargo(r.responsavelLog(), g, r.tipo(), discenteOrThrow(r.discenteId()));
        if (!ok) {
            throw new RecursoNaoEncontradoException("Cargo nao encontrado para este discente no grupo.");
        }
        return GrupoResponse.from(g);
    }

    // ---------- auxiliares ----------

    private GrupoDiscente grupoOrThrow(Long id) {
        GrupoDiscente g = grupoService.buscarPorId(id);
        if (g == null) {
            throw new RecursoNaoEncontradoException("Grupo " + id + " nao encontrado.");
        }
        return g;
    }

    private Docente docenteOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Docente d) {
            return d;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um docente.");
    }

    private Discente discenteOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Discente d) {
            return d;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um discente.");
    }

    private Usuario usuarioOrThrow(Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado.");
        }
        return u;
    }
}
