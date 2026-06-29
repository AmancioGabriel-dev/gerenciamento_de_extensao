package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.InscricaoDtos.*;
import com.extensao.entity.*;
import com.extensao.service.InscricaoService;
import com.extensao.service.OportunidadeService;
import com.extensao.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    private final InscricaoService inscricaoService;
    private final OportunidadeService oportunidadeService;
    private final UsuarioService usuarioService;

    public InscricaoController(InscricaoService inscricaoService,
                              OportunidadeService oportunidadeService,
                              UsuarioService usuarioService) {
        this.inscricaoService = inscricaoService;
        this.oportunidadeService = oportunidadeService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponse inscrever(@RequestBody InscreverRequest r) {
        Discente d = discenteOrThrow(r.discenteId());
        Oportunidade op = oportunidadeService.buscarPorId(r.oportunidadeId());
        if (op == null) {
            throw new RecursoNaoEncontradoException("Oportunidade " + r.oportunidadeId() + " nao encontrada.");
        }
        return InscricaoResponse.from(inscricaoService.inscrever(d, op));
    }

    @GetMapping
    public List<InscricaoResponse> listarDoDiscente(@RequestParam Long discenteId) {
        Discente d = discenteOrThrow(discenteId);
        return inscricaoService.listarInscricoesDoDiscente(d).stream()
                .map(InscricaoResponse::from).toList();
    }

    @GetMapping("/pendentes")
    public List<InscricaoResponse> pendentesDoDocente(@RequestParam Long docenteId) {
        return inscricaoService.listarPendentesDoDocente(docenteOrThrow(docenteId)).stream()
                .map(InscricaoResponse::from).toList();
    }

    @GetMapping("/aprovadas")
    public List<InscricaoResponse> aprovadasDoDocente(@RequestParam Long docenteId) {
        return inscricaoService.listarAprovadasDoDocente(docenteOrThrow(docenteId)).stream()
                .map(InscricaoResponse::from).toList();
    }

    @PatchMapping("/{id}/aprovar")
    public InscricaoResponse aprovar(@PathVariable Long id, @RequestBody ResponsavelRequest r) {
        Inscricao i = inscricaoOrThrow(id);
        inscricaoService.aprovar(docenteOrThrow(r.usuarioId()), i);
        return InscricaoResponse.from(i);
    }

    @PatchMapping("/{id}/rejeitar")
    public InscricaoResponse rejeitar(@PathVariable Long id, @RequestBody RejeitarRequest r) {
        Inscricao i = inscricaoOrThrow(id);
        inscricaoService.rejeitar(docenteOrThrow(r.docenteId()), i, r.motivo());
        return InscricaoResponse.from(i);
    }

    @PatchMapping("/{id}/cancelar")
    public InscricaoResponse cancelar(@PathVariable Long id, @RequestBody ResponsavelRequest r) {
        Inscricao i = inscricaoOrThrow(id);
        inscricaoService.cancelarInscricao(discenteOrThrow(r.usuarioId()), i);
        return InscricaoResponse.from(i);
    }

    @PatchMapping("/{id}/substituir")
    public InscricaoResponse substituir(@PathVariable Long id, @RequestBody SubstituirRequest r) {
        Inscricao i = inscricaoOrThrow(id);
        inscricaoService.substituirParticipante(docenteOrThrow(r.docenteId()), i, r.justificativa());
        return InscricaoResponse.from(i);
    }

    // ---------- auxiliares ----------

    private Inscricao inscricaoOrThrow(Long id) {
        Inscricao i = inscricaoService.buscarPorId(id);
        if (i == null) {
            throw new RecursoNaoEncontradoException("Inscricao " + id + " nao encontrada.");
        }
        return i;
    }

    private Discente discenteOrThrow(Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado.");
        }
        if (u instanceof Discente d) {
            return d;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um discente.");
    }

    private Docente docenteOrThrow(Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado.");
        }
        if (u instanceof Docente d) {
            return d;
        }
        throw new IllegalArgumentException("Usuario " + id + " nao e um docente.");
    }
}
