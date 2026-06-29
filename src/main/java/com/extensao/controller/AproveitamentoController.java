package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.AproveitamentoDtos.*;
import com.extensao.entity.*;
import com.extensao.service.AproveitamentoService;
import com.extensao.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aproveitamentos")
public class AproveitamentoController {

    private final AproveitamentoService aproveitamentoService;
    private final UsuarioService usuarioService;

    public AproveitamentoController(AproveitamentoService aproveitamentoService,
                                    UsuarioService usuarioService) {
        this.aproveitamentoService = aproveitamentoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse criar(@RequestBody CriarSolicitacaoRequest r) {
        Discente d = discenteOrThrow(r.discenteId());
        SolicitacaoAproveitamento s = aproveitamentoService.criarSolicitacao(
                d, r.descricao(), r.cargaHorariaPleiteada(), r.dataInicio(), r.dataFim(),
                r.documentoComprobatorio());
        return SolicitacaoResponse.from(s);
    }

    @GetMapping
    public List<SolicitacaoResponse> listar(@RequestParam(required = false) Long discenteId) {
        List<SolicitacaoAproveitamento> lista = (discenteId == null)
                ? aproveitamentoService.listarTodas()
                : aproveitamentoService.listarPorDiscente(discenteOrThrow(discenteId));
        return lista.stream().map(SolicitacaoResponse::from).toList();
    }

    @GetMapping("/pendentes/coordenador")
    public List<SolicitacaoResponse> pendentesCoordenador() {
        return aproveitamentoService.listarPendentesCoordenador().stream()
                .map(SolicitacaoResponse::from).toList();
    }

    @GetMapping("/pendentes/comissao")
    public List<SolicitacaoResponse> pendentesComissao() {
        return aproveitamentoService.listarPendentesComissao().stream()
                .map(SolicitacaoResponse::from).toList();
    }

    @PatchMapping("/{id}/deferir")
    public SolicitacaoResponse deferir(@PathVariable Long id, @RequestBody ParecerRequest r) {
        SolicitacaoAproveitamento s = solicitacaoOrThrow(id);
        aproveitamentoService.deferir(coordenadorOrThrow(r.avaliadorId()), s, r.parecer());
        return SolicitacaoResponse.from(s);
    }

    @PatchMapping("/{id}/indeferir")
    public SolicitacaoResponse indeferir(@PathVariable Long id, @RequestBody ParecerRequest r) {
        SolicitacaoAproveitamento s = solicitacaoOrThrow(id);
        aproveitamentoService.indeferir(usuarioOrThrow(r.avaliadorId()), s, r.parecer());
        return SolicitacaoResponse.from(s);
    }

    @PatchMapping("/{id}/delegar")
    public SolicitacaoResponse delegar(@PathVariable Long id, @RequestBody DelegarRequest r) {
        SolicitacaoAproveitamento s = solicitacaoOrThrow(id);
        aproveitamentoService.delegarParaComissao(
                coordenadorOrThrow(r.coordenadorId()), s, comissaoOrThrow(r.comissaoId()));
        return SolicitacaoResponse.from(s);
    }

    @PatchMapping("/{id}/deferir-comissao")
    public SolicitacaoResponse deferirComissao(@PathVariable Long id, @RequestBody ParecerRequest r) {
        SolicitacaoAproveitamento s = solicitacaoOrThrow(id);
        aproveitamentoService.deferirPelaComissao(comissaoOrThrow(r.avaliadorId()), s, r.parecer());
        return SolicitacaoResponse.from(s);
    }

    @PatchMapping("/{id}/cancelar")
    public SolicitacaoResponse cancelar(@PathVariable Long id, @RequestBody DiscenteRequest r) {
        SolicitacaoAproveitamento s = solicitacaoOrThrow(id);
        aproveitamentoService.cancelarPeloDiscente(discenteOrThrow(r.discenteId()), s);
        return SolicitacaoResponse.from(s);
    }

    @PatchMapping("/{id}/reenviar")
    public SolicitacaoResponse reenviar(@PathVariable Long id, @RequestBody ReenvioRequest r) {
        SolicitacaoAproveitamento s = solicitacaoOrThrow(id);
        aproveitamentoService.reenviar(discenteOrThrow(r.discenteId()), s, r.novoDocumento());
        return SolicitacaoResponse.from(s);
    }

    // ---------- auxiliares ----------

    private SolicitacaoAproveitamento solicitacaoOrThrow(Long id) {
        SolicitacaoAproveitamento s = aproveitamentoService.buscarPorId(id);
        if (s == null) {
            throw new RecursoNaoEncontradoException("Solicitacao " + id + " nao encontrada.");
        }
        return s;
    }

    private Usuario usuarioOrThrow(Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado.");
        }
        return u;
    }

    private Discente discenteOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Discente d) {
            return d;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um discente.");
    }

    private Coordenador coordenadorOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Coordenador c) {
            return c;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um coordenador.");
    }

    private Comissao comissaoOrThrow(Long id) {
        if (usuarioOrThrow(id) instanceof Comissao c) {
            return c;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e uma comissao.");
    }
}
