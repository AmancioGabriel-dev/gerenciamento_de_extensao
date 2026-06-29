package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.UsuarioDtos.*;
import com.extensao.entity.*;
import com.extensao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Endpoints de usuarios.
 *
 * @RestController       -> esta classe responde requisicoes HTTP devolvendo JSON.
 * @RequestMapping       -> prefixo comum das rotas: /usuarios
 * @PostMapping/@GetMapping/@PatchMapping -> verbo HTTP de cada endpoint.
 * @RequestBody          -> o JSON do corpo vira o objeto DTO.
 * @PathVariable         -> pega um pedaco da URL (ex.: /usuarios/5 -> id=5).
 * @Valid                -> dispara a validacao (@NotBlank, etc.) do DTO.
 *
 * Substitui os menus do antigo Main.java.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/discentes")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse autocadastroDiscente(@Valid @RequestBody AutocadastroDiscenteRequest r) {
        Discente d = usuarioService.autocadastroDiscente(
                r.nome(), r.email(), r.senha(), r.matricula(), r.semestre());
        return UsuarioResponse.from(d);
    }

    @PostMapping("/docentes")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarDocente(@Valid @RequestBody CadastroDocenteRequest r) {
        Docente d = usuarioService.cadastrarDocente(
                r.responsavel(), r.nome(), r.email(), r.siape(), r.departamento());
        return UsuarioResponse.from(d);
    }

    @PostMapping("/coordenadores")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarCoordenador(@Valid @RequestBody CadastroCoordenadorRequest r) {
        Coordenador c = usuarioService.cadastrarCoordenador(
                r.responsavel(), r.nome(), r.email(), r.siape(), r.curso());
        return UsuarioResponse.from(c);
    }

    @PostMapping("/comissoes")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarComissao(@Valid @RequestBody CadastroComissaoRequest r) {
        Comissao c = usuarioService.cadastrarComissao(r.responsavel(), r.nome(), r.email(), r.area());
        return UsuarioResponse.from(c);
    }

    @PostMapping("/secretarias")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarSecretaria(@Valid @RequestBody CadastroSecretariaRequest r) {
        Secretaria s = usuarioService.cadastrarSecretaria(r.responsavel(), r.nome(), r.email(), r.setor());
        return UsuarioResponse.from(s);
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest r) {
        Usuario u = usuarioService.autenticar(r.email(), r.senha());
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas ou usuario inativo.");
        }
        return UsuarioResponse.from(u);
    }

    /** Demonstracao de POLIMORFISMO: lista todos os tipos juntos. */
    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/discentes")
    public List<UsuarioResponse> listarDiscentes() {
        return usuarioService.listarDiscentes().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/docentes")
    public List<UsuarioResponse> listarDocentes() {
        return usuarioService.listarDocentes().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario nao encontrado.");
        }
        return UsuarioResponse.from(u);
    }

    @PatchMapping("/{id}/ativacao")
    public ResponseEntity<UsuarioResponse> alternarAtivacao(@PathVariable Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario nao encontrado.");
        }
        usuarioService.alternarAtivacao(u);
        return ResponseEntity.ok(UsuarioResponse.from(u));
    }
}
