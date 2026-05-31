package Services;

import Entities.Perfil;
import Entities.Usuario;
import Model.Acao;
import Model.Modulo;
import Model.TipoUsuario;

import java.util.ArrayList;
import java.util.List;

public class PerfilService {

    private final List<Perfil> perfis = new ArrayList<>();
    private final LogService logService;

    public PerfilService(LogService logService) {
        this.logService = logService;
        carregarPerfisPadrao();
    }

    private void carregarPerfisPadrao() {
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

        perfis.add(pAdmin);
        perfis.add(pCoord);
        perfis.add(pDoc);
        perfis.add(pCom);
        perfis.add(pSec);
        perfis.add(pDis);
    }

    public Perfil buscarPorNome(String nome) {
        return perfis.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst().orElse(null);
    }

    public Perfil obterPerfilPadrao(TipoUsuario tipo) {
        return buscarPorNome(tipo.name());
    }

    public List<Perfil> listar() {
        return perfis;
    }

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
    }

    public boolean usuarioPode(Usuario usuario, Modulo modulo, Acao acao) {
        return usuario.getPerfil() != null && usuario.getPerfil().possuiPermissao(modulo, acao);
    }
}
