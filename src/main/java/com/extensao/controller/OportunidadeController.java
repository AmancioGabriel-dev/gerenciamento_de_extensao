package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.OportunidadeDtos.*;
import com.extensao.entity.*;
import com.extensao.model.StatusOportunidade;
import com.extensao.service.InscricaoService;
import com.extensao.service.OportunidadeService;
import com.extensao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/oportunidades")
public class OportunidadeController {

    private final OportunidadeService oportunidadeService;
    private final UsuarioService usuarioService;
    private final InscricaoService inscricaoService;

    public OportunidadeController(OportunidadeService oportunidadeService,
                                  UsuarioService usuarioService,
                                  InscricaoService inscricaoService) {
        this.oportunidadeService = oportunidadeService;
        this.usuarioService = usuarioService;
        this.inscricaoService = inscricaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadeResponse criar(@Valid @RequestBody CriarOportunidadeRequest r) {
        Usuario criador = usuarioOrThrow(r.criadorId());
        Docente responsavel = r.docenteResponsavelId() == null
                ? null : docenteOrThrow(r.docenteResponsavelId());
        Oportunidade op = oportunidadeService.criar(criador, responsavel,
                r.titulo(), r.descricao(), r.modalidade(), r.cargaHorariaPrevista(),
                r.dataInicio(), r.dataFim(), r.vagas());
        return OportunidadeResponse.from(op);
    }

    @GetMapping
    public List<OportunidadeResponse> listar(@RequestParam(required = false) StatusOportunidade status) {
        List<Oportunidade> ops = (status == null)
                ? oportunidadeService.listarTodas()
                : oportunidadeService.listarPorStatus(status);
        return ops.stream().map(OportunidadeResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OportunidadeResponse buscar(@PathVariable Long id) {
        return OportunidadeResponse.from(opOrThrow(id));
    }

    @PostMapping("/{id}/anexos")
    public OportunidadeResponse adicionarAnexo(@PathVariable Long id, @Valid @RequestBody AnexoRequest r) {
        Oportunidade op = oportunidadeService.adicionarAnexo(opOrThrow(id), r.anexo());
        return OportunidadeResponse.from(op);
    }

    @PatchMapping("/{id}/enviar-aprovacao")
    public OportunidadeResponse enviarParaAprovacao(@PathVariable Long id, @RequestBody ResponsavelRequest r) {
        Oportunidade op = opOrThrow(id);
        oportunidadeService.enviarParaAprovacao(usuarioOrThrow(r.usuarioId()), op);
        return OportunidadeResponse.from(op);
    }

    @PatchMapping("/{id}/aprovar")
    public OportunidadeResponse aprovar(@PathVariable Long id, @RequestBody ResponsavelRequest r) {
        Oportunidade op = opOrThrow(id);
        oportunidadeService.aprovar(coordenadorOrThrow(r.usuarioId()), op);
        return OportunidadeResponse.from(op);
    }

    @PatchMapping("/{id}/cancelar")
    public OportunidadeResponse cancelar(@PathVariable Long id, @RequestBody ResponsavelRequest r) {
        Oportunidade op = opOrThrow(id);
        oportunidadeService.cancelar(usuarioOrThrow(r.usuarioId()), op);
        return OportunidadeResponse.from(op);
    }

    @PatchMapping("/{id}/iniciar-execucao")
    public OportunidadeResponse iniciarExecucao(@PathVariable Long id, @RequestBody ResponsavelRequest r) {
        Oportunidade op = opOrThrow(id);
        oportunidadeService.iniciarExecucao(usuarioOrThrow(r.usuarioId()), op);
        return OportunidadeResponse.from(op);
    }

    @PatchMapping("/{id}/encerrar")
    public OportunidadeResponse encerrar(@PathVariable Long id, @RequestBody EncerrarRequest r) {
        Oportunidade op = opOrThrow(id);
        Docente docente = docenteOrThrow(r.docenteId());
        List<Inscricao> concluintes = new ArrayList<>();
        if (r.concluintesIds() != null) {
            for (Long inscricaoId : r.concluintesIds()) {
                Inscricao i = inscricaoService.buscarPorId(inscricaoId);
                if (i == null) {
                    throw new RecursoNaoEncontradoException("Inscricao " + inscricaoId + " nao encontrada.");
                }
                concluintes.add(i);
            }
        }
        oportunidadeService.encerrar(docente, op, concluintes);
        return OportunidadeResponse.from(op);
    }

    // ---------- auxiliares: resolvem id -> entidade do tipo certo ----------

    private Oportunidade opOrThrow(Long id) {
        Oportunidade op = oportunidadeService.buscarPorId(id);
        if (op == null) {
            throw new RecursoNaoEncontradoException("Oportunidade " + id + " nao encontrada.");
        }
        return op;
    }

    private Usuario usuarioOrThrow(Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado.");
        }
        return u;
    }

    private Docente docenteOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Docente d) {
            return d;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um docente.");
    }

    private Coordenador coordenadorOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Coordenador c) {
            return c;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um coordenador.");
    }
}
