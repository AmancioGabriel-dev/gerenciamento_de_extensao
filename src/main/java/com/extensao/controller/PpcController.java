package com.extensao.controller;

import com.extensao.config.RecursoNaoEncontradoException;
import com.extensao.dto.PpcDtos.*;
import com.extensao.entity.PPC;
import com.extensao.service.PPCService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ppcs")
public class PpcController {

    private final PPCService ppcService;

    public PpcController(PPCService ppcService) {
        this.ppcService = ppcService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PpcResponse cadastrar(@Valid @RequestBody CadastrarPpcRequest r) {
        PPC p = ppcService.cadastrarVersao(r.responsavelLog(), r.versao(), r.cargaHorariaMinima(),
                r.autor(), r.vigenciaInicio(), r.vigenciaFim());
        return PpcResponse.from(p);
    }

    @GetMapping
    public List<PpcResponse> historico() {
        return ppcService.historico().stream().map(PpcResponse::from).toList();
    }

    @GetMapping("/vigente")
    public PpcResponse vigente() {
        PPC p = ppcService.obterVigente();
        if (p == null) {
            throw new RecursoNaoEncontradoException("Nenhum PPC vigente no momento.");
        }
        return PpcResponse.from(p);
    }
}
