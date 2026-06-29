package com.extensao.service;

import com.extensao.entity.Perfil;
import com.extensao.entity.Usuario;
import com.extensao.model.Acao;
import com.extensao.model.Modulo;
import com.extensao.model.TipoUsuario;
import com.extensao.repository.PerfilRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gerencia perfis e permissoes (modulo x acao).
 *
 * Diferenca para o original: os perfis padrao agora sao PERSISTIDOS no banco.
 * O metodo criarPerfisPadraoSeNecessario() e chamado pelo DataSeeder no startup.
 */
@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final LogService logService;

    public PerfilService(PerfilRepository perfilRepository, LogService logService) {
        this.perfilRepository = perfilRepository;
        this.logService = logService;
    }

    @Transactional
    public void criarPerfisPadraoSeNecessario() {
        if (perfilRepository.count() > 0) {
            return; // ja existem, nao recria
        }

        Perfil pAdmin = new Perfil("ADMINISTRADOR");
        for (Modulo m : Modulo.values()) {
            for (Acao a : Acao.values()) {
                pAdmin.concederPermissao(m, a);
            }
        }

        Perfil pCoord = new Perfil("COORDENADOR");
        pCoord.concederPermissao(Modulo.OPORTUNIDADES, Acao.APROVAR);
        pCoord.concederPermissao(Modulo.OPORTUNIDADES, Acao.LER);
        pCoord.concederPermissao(Modulo.APROVEITAMENTO, Acao.APROVAR);
        pCoord.concederPermissao(Modulo.APROVEITAMENTO, Acao.LER);
        pCoord.concederPermissao(Modulo.PPC, Acao.CRIAR);
        pCoord.concederPermissao(Modulo.PPC, Acao.LER);
        pCoord.concederPermissao(Modulo.PPC, Acao.ATUALIZAR);

        Perfil pDoc = new Perfil("DOCENTE");
        pDoc.concederPermissao(Modulo.OPORTUNIDADES, Acao.CRIAR);
        pDoc.concederPermissao(Modulo.OPORTUNIDADES, Acao.LER);
        pDoc.concederPermissao(Modulo.OPORTUNIDADES, Acao.ATUALIZAR);
        pDoc.concederPermissao(Modulo.INSCRICOES, Acao.APROVAR);
        pDoc.concederPermissao(Modulo.GRUPOS, Acao.CRIAR);
        pDoc.concederPermissao(Modulo.GRUPOS, Acao.ATUALIZAR);

        Perfil pCom = new Perfil("COMISSAO");
        pCom.concederPermissao(Modulo.APROVEITAMENTO, Acao.LER);
        pCom.concederPermissao(Modulo.APROVEITAMENTO, Acao.APROVAR);

        Perfil pSec = new Perfil("SECRETARIA");
        pSec.concederPermissao(Modulo.OPORTUNIDADES, Acao.LER);
        pSec.concederPermissao(Modulo.APROVEITAMENTO, Acao.LER);
        pSec.concederPermissao(Modulo.USUARIOS, Acao.LER);

        Perfil pDis = new Perfil("DISCENTE");
        pDis.concederPermissao(Modulo.OPORTUNIDADES, Acao.LER);
        pDis.concederPermissao(Modulo.INSCRICOES, Acao.CRIAR);
        pDis.concederPermissao(Modulo.APROVEITAMENTO, Acao.CRIAR);
        pDis.concederPermissao(Modulo.APROVEITAMENTO, Acao.LER);

        perfilRepository.saveAll(List.of(pAdmin, pCoord, pDoc, pCom, pSec, pDis));
    }

    public Perfil buscarPorNome(String nome) {
        return perfilRepository.findByNomeIgnoreCase(nome).orElse(null);
    }

    public Perfil buscarPorId(Long id) {
        return perfilRepository.findById(id).orElse(null);
    }

    public Perfil obterPerfilPadrao(TipoUsuario tipo) {
        return buscarPorNome(tipo.name());
    }

    public List<Perfil> listar() {
        return perfilRepository.findAll();
    }

    @Transactional
    public void alterarPermissao(String responsavel, Perfil perfil,
                                 Modulo modulo, Acao acao, boolean conceder) {
        if (conceder) {
            perfil.concederPermissao(modulo, acao);
            logService.registrar(responsavel,
                    "Concedeu " + acao + " em " + modulo + " ao perfil " + perfil.getNome());
        } else {
            perfil.revogarPermissao(modulo, acao);
            logService.registrar(responsavel,
                    "Revogou " + acao + " em " + modulo + " do perfil " + perfil.getNome());
        }
        perfilRepository.save(perfil);
    }

    public boolean usuarioPode(Usuario usuario, Modulo modulo, Acao acao) {
        return usuario.getPerfil() != null && usuario.getPerfil().possuiPermissao(modulo, acao);
    }
}
