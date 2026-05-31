import Entities.Administrador;
import Entities.Comissao;
import Entities.Coordenador;
import Entities.Discente;
import Entities.Docente;
import Entities.GrupoDiscente;
import Entities.HistoricoCargo;
import Entities.Inscricao;
import Entities.Oportunidade;
import Entities.PPC;
import Entities.Perfil;
import Entities.Secretaria;
import Entities.SolicitacaoAproveitamento;
import Entities.Usuario;
import Model.Acao;
import Model.Modalidade;
import Model.Modulo;
import Model.StatusInscricao;
import Model.StatusOportunidade;
import Model.StatusSolicitacao;
import Model.TipoCargo;
import Services.AproveitamentoService;
import Services.GrupoService;
import Services.InscricaoService;
import Services.LogService;
import Services.OportunidadeService;
import Services.PPCService;
import Services.PerfilService;
import Services.RegraNegocioService;
import Services.UsuarioService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static LogService logService;
    private static PerfilService perfilService;
    private static UsuarioService usuarioService;
    private static RegraNegocioService regraService;
    private static OportunidadeService oportunidadeService;
    private static InscricaoService inscricaoService;
    private static AproveitamentoService aproveitamentoService;
    private static GrupoService grupoService;
    private static PPCService ppcService;

    public static void main(String[] args) {
        inicializarServicos();
        carregarDadosIniciais();

        System.out.println("================================================");
        System.out.println(" SISTEMA DE GERENCIAMENTO DE EXTENSAO - ETAPA 2 ");
        System.out.println("================================================");

        boolean executando = true;
        while (executando) {
            executando = menuInicial();
        }

        System.out.println("\nEncerrando sistema. Ate logo!");
    }

    private static void inicializarServicos() {
        logService = new LogService();
        perfilService = new PerfilService(logService);
        usuarioService = new UsuarioService(logService, perfilService);
        regraService = new RegraNegocioService();
        oportunidadeService = new OportunidadeService(logService);
        inscricaoService = new InscricaoService(logService, oportunidadeService);
        aproveitamentoService = new AproveitamentoService(regraService, logService);
        grupoService = new GrupoService(logService);
        ppcService = new PPCService(logService);
    }

    private static void carregarDadosIniciais() {
        usuarioService.cadastrarAdminInicial("Admin Geral", "admin@univ.edu", "admin123");

        Docente docSeed = new Docente("Prof. Ana", "ana@univ.edu", "docente123", "S001", "Computacao");
        docSeed.setPerfil(perfilService.obterPerfilPadrao(Model.TipoUsuario.DOCENTE));
        usuarioService.listarTodos().add(docSeed);

        Coordenador coordSeed = new Coordenador("Coord. Bruno", "bruno@univ.edu", "coord123", "S002", "Ciencia da Computacao");
        coordSeed.setPerfil(perfilService.obterPerfilPadrao(Model.TipoUsuario.COORDENADOR));
        usuarioService.listarTodos().add(coordSeed);

        ppcService.cadastrarVersao("SISTEMA", "1.0", 345, "Coord. Bruno",
                LocalDate.now().minusYears(2), null);

        System.out.println("[SEED] Admin: admin@univ.edu / admin123");
        System.out.println("[SEED] Docente: ana@univ.edu / docente123");
        System.out.println("[SEED] Coordenador: bruno@univ.edu / coord123");
    }

    private static boolean menuInicial() {
        System.out.println("\n=========== MENU INICIAL ===========");
        System.out.println("1. Login");
        System.out.println("2. Autocadastro (Discente)");
        System.out.println("3. Demonstrar Polimorfismo (listar dashboards)");
        System.out.println("0. Sair");
        int op = lerInt("Opcao: ");
        switch (op) {
            case 1 -> efetuarLogin();
            case 2 -> autocadastroDiscente();
            case 3 -> demonstrarPolimorfismo();
            case 0 -> { return false; }
            default -> System.out.println("Opcao invalida.");
        }
        return true;
    }

    private static void efetuarLogin() {
        String email = lerLinha("Email: ");
        String senha = lerLinha("Senha: ");
        Usuario u = usuarioService.autenticar(email, senha);
        if (u == null) {
            System.out.println("Credenciais invalidas ou usuario inativo.");
            return;
        }
        System.out.println("\nBem-vindo(a), " + u.getNome() + "!");
        u.exibirDashboard();
        rotearMenu(u);
    }

    private static void autocadastroDiscente() {
        try {
            String nome = lerLinha("Nome: ");
            String email = lerLinha("Email: ");
            String senha = lerLinha("Senha: ");
            int mat = lerInt("Matricula: ");
            int sem = lerInt("Semestre: ");
            Discente d = usuarioService.autocadastroDiscente(nome, email, senha, mat, sem);
            System.out.println("Discente cadastrado com sucesso! ID matricula: " + d.getMatricula());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void demonstrarPolimorfismo() {
        List<Usuario> todos = usuarioService.listarTodos();
        if (todos.isEmpty()) {
            System.out.println("Nenhum usuario cadastrado.");
            return;
        }
        System.out.println("\n--- POLIMORFISMO: cada subclasse exibe seu proprio dashboard ---");
        for (Usuario u : todos) {
            u.exibirDashboard();
        }
    }

    private static void rotearMenu(Usuario u) {
        if (u instanceof Discente d) menuDiscente(d);
        else if (u instanceof Docente d) menuDocente(d);
        else if (u instanceof Coordenador c) menuCoordenador(c);
        else if (u instanceof Comissao c) menuComissao(c);
        else if (u instanceof Secretaria s) menuSecretaria(s);
        else if (u instanceof Administrador a) menuAdministrador(a);
    }

    // ============================================================
    //                       MENU DISCENTE
    // ============================================================
    private static void menuDiscente(Discente d) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n--- MENU DISCENTE (" + d.getNome() + ") ---");
            System.out.println("1. Listar oportunidades abertas");
            System.out.println("2. Inscrever-se em oportunidade");
            System.out.println("3. Cancelar inscricao");
            System.out.println("4. Criar solicitacao de aproveitamento");
            System.out.println("5. Reenviar solicitacao indeferida");
            System.out.println("6. Cancelar solicitacao pendente");
            System.out.println("7. Acompanhar status das solicitacoes");
            System.out.println("8. Painel de progresso (barra)");
            System.out.println("9. Listar grupos discentes");
            System.out.println("0. Logout");
            int op = lerInt("Opcao: ");
            try {
                switch (op) {
                    case 1 -> listarOportunidadesAbertas();
                    case 2 -> inscreverEmOportunidade(d);
                    case 3 -> cancelarInscricao(d);
                    case 4 -> criarSolicitacaoAproveitamento(d);
                    case 5 -> reenviarSolicitacao(d);
                    case 6 -> cancelarSolicitacaoPendente(d);
                    case 7 -> acompanharSolicitacoes(d);
                    case 8 -> exibirPainelProgresso(d);
                    case 9 -> listarGrupos();
                    case 0 -> stay = false;
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void listarOportunidadesAbertas() {
        List<Oportunidade> abertas = oportunidadeService.listarPorStatus(StatusOportunidade.ABERTA);
        if (abertas.isEmpty()) System.out.println("Nenhuma oportunidade aberta no momento.");
        abertas.forEach(System.out::println);
    }

    private static void inscreverEmOportunidade(Discente d) {
        listarOportunidadesAbertas();
        int id = lerInt("ID da oportunidade: ");
        Oportunidade op = oportunidadeService.buscarPorId(id);
        if (op == null) { System.out.println("Oportunidade nao encontrada."); return; }
        Inscricao i = inscricaoService.inscrever(d, op);
        System.out.println("Inscricao registrada: " + i);
    }

    private static void cancelarInscricao(Discente d) {
        List<Inscricao> minhas = inscricaoService.listarInscricoesDoDiscente(d);
        if (minhas.isEmpty()) { System.out.println("Sem inscricoes."); return; }
        for (int i = 0; i < minhas.size(); i++) System.out.println(i + ". " + minhas.get(i));
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= minhas.size()) { System.out.println("Indice invalido."); return; }
        inscricaoService.cancelarInscricao(d, minhas.get(idx));
        System.out.println("Inscricao cancelada.");
    }

    private static void criarSolicitacaoAproveitamento(Discente d) {
        String desc = lerLinha("Descricao da atividade: ");
        int carga = lerInt("Carga horaria pleiteada: ");
        LocalDate ini = lerData("Data inicio (AAAA-MM-DD): ");
        LocalDate fim = lerData("Data fim    (AAAA-MM-DD): ");
        String doc = lerLinha("Documento comprobatorio: ");
        SolicitacaoAproveitamento s = aproveitamentoService.criarSolicitacao(d, desc, carga, ini, fim, doc);
        System.out.println("Solicitacao criada: " + s);
    }

    private static void reenviarSolicitacao(Discente d) {
        List<SolicitacaoAproveitamento> indef = aproveitamentoService.listarPorDiscente(d).stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.INDEFERIDO).toList();
        if (indef.isEmpty()) { System.out.println("Nenhuma solicitacao indeferida."); return; }
        for (int i = 0; i < indef.size(); i++) System.out.println(i + ". " + indef.get(i)
                + " (parecer: " + indef.get(i).getParecer() + ")");
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= indef.size()) { System.out.println("Indice invalido."); return; }
        String doc = lerLinha("Novo documento: ");
        aproveitamentoService.reenviar(d, indef.get(idx), doc);
        System.out.println("Solicitacao reenviada.");
    }

    private static void cancelarSolicitacaoPendente(Discente d) {
        List<SolicitacaoAproveitamento> pend = aproveitamentoService.listarPorDiscente(d).stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.PENDENTE).toList();
        if (pend.isEmpty()) { System.out.println("Nenhuma pendente."); return; }
        for (int i = 0; i < pend.size(); i++) System.out.println(i + ". " + pend.get(i));
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= pend.size()) { System.out.println("Indice invalido."); return; }
        aproveitamentoService.cancelarPeloDiscente(d, pend.get(idx));
        System.out.println("Solicitacao cancelada.");
    }

    private static void acompanharSolicitacoes(Discente d) {
        List<SolicitacaoAproveitamento> minhas = aproveitamentoService.listarPorDiscente(d);
        if (minhas.isEmpty()) { System.out.println("Sem solicitacoes."); return; }
        LocalDate hoje = LocalDate.now();
        System.out.println("\n--- MINHAS SOLICITACOES ---");
        for (SolicitacaoAproveitamento s : minhas) {
            System.out.println(s);
            if (!s.getParecer().isBlank())
                System.out.println("    Parecer: " + s.getParecer());
            switch (s.getStatus()) {
                case PENDENTE -> {
                    long restantes = regraService.diasRestantesCoordenador(s, hoje);
                    String alerta = restantes < 0 ? "PRAZO EXCEDIDO" : restantes + " dia(s) restantes";
                    System.out.println("    [Alerta] Decisao coordenador: " + alerta);
                }
                case INDEFERIDO -> {
                    long restantes = regraService.diasRestantesReenvio(s, hoje);
                    String alerta = restantes < 0 ? "Prazo de reenvio expirado" : restantes + " dia(s) para reenvio";
                    System.out.println("    [Alerta] " + alerta);
                }
                default -> { /* nada */ }
            }
        }
    }

    private static void exibirPainelProgresso(Discente d) {
        int total = regraService.getLimiteHoras();
        int feitas = d.getHorasDeExtensaoAcumuladas();
        int pendentes = aproveitamentoService.listarPorDiscente(d).stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.PENDENTE)
                .mapToInt(SolicitacaoAproveitamento::getCargaHorariaPleiteada).sum();

        int pct = Math.min(100, (feitas * 100) / total);
        int blocos = pct / 5;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 20; i++) bar.append(i < blocos ? "#" : "-");
        bar.append("]");

        System.out.println("\n========== PAINEL DE PROGRESSO ==========");
        System.out.println("Horas concluidas..: " + feitas + "h / " + total + "h");
        System.out.println("Horas pendentes...: " + pendentes + "h (em analise)");
        System.out.println("Progresso.........: " + bar + " " + pct + "%");
        System.out.println("\n--- Historico ---");
        aproveitamentoService.listarPorDiscente(d).forEach(s -> System.out.println("  " + s));
        System.out.println("=========================================");
    }

    private static void listarGrupos() {
        List<GrupoDiscente> gs = grupoService.listar();
        if (gs.isEmpty()) { System.out.println("Nenhum grupo cadastrado."); return; }
        gs.forEach(System.out::println);
    }

    // ============================================================
    //                       MENU DOCENTE
    // ============================================================
    private static void menuDocente(Docente d) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n--- MENU DOCENTE (" + d.getNome() + ") ---");
            System.out.println("1. Criar oportunidade (rascunho)");
            System.out.println("2. Enviar oportunidade para aprovacao");
            System.out.println("3. Aprovar/rejeitar inscricoes");
            System.out.println("4. Substituir participante");
            System.out.println("5. Encerrar oportunidade (selecionar concluintes)");
            System.out.println("6. Criar grupo discente");
            System.out.println("7. Atribuir/remover cargo em grupo");
            System.out.println("8. Historico de cargos do grupo");
            System.out.println("0. Logout");
            int op = lerInt("Opcao: ");
            try {
                switch (op) {
                    case 1 -> criarOportunidade(d, null);
                    case 2 -> enviarParaAprovacao(d);
                    case 3 -> avaliarInscricoes(d);
                    case 4 -> substituirParticipante(d);
                    case 5 -> encerrarOportunidade(d);
                    case 6 -> criarGrupoDiscente(d);
                    case 7 -> gerenciarCargos(d);
                    case 8 -> exibirHistoricoCargos();
                    case 0 -> stay = false;
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void criarOportunidade(Usuario criador, Docente docenteResponsavel) {
        String titulo = lerLinha("Titulo: ");
        String descricao = lerLinha("Descricao: ");
        Modalidade m = lerEnum("Modalidade", Modalidade.values());
        int ch = lerInt("Carga horaria prevista (h): ");
        LocalDate ini = lerData("Data inicio (AAAA-MM-DD): ");
        LocalDate fim = lerData("Data fim    (AAAA-MM-DD): ");
        int vagas = lerInt("Vagas: ");

        Docente resp = docenteResponsavel;
        if (resp == null && criador instanceof Docente cd) resp = cd;
        if (resp == null) {
            System.out.println("(Discente) Selecione um docente responsavel:");
            List<Docente> docs = usuarioService.listarDocentes();
            for (int i = 0; i < docs.size(); i++) System.out.println(i + ". " + docs.get(i).getNome());
            int idx = lerInt("Indice: ");
            if (idx < 0 || idx >= docs.size()) { System.out.println("Indice invalido."); return; }
            resp = docs.get(idx);
        }

        Oportunidade op = oportunidadeService.criar(criador, resp, titulo, descricao, m, ch, ini, fim, vagas);
        while (true) {
            String anexo = lerLinha("Anexo (vazio para terminar): ");
            if (anexo.isBlank()) break;
            op.adicionarAnexo(anexo);
        }
        System.out.println("Oportunidade criada: " + op);
    }

    private static void enviarParaAprovacao(Docente d) {
        List<Oportunidade> rascunhos = oportunidadeService.listarPorStatus(StatusOportunidade.RASCUNHO);
        if (rascunhos.isEmpty()) { System.out.println("Nenhum rascunho."); return; }
        rascunhos.forEach(System.out::println);
        int id = lerInt("ID: ");
        Oportunidade op = oportunidadeService.buscarPorId(id);
        if (op == null) { System.out.println("Nao encontrada."); return; }
        oportunidadeService.enviarParaAprovacao(d, op);
        System.out.println("Enviada para aprovacao.");
    }

    private static void avaliarInscricoes(Docente d) {
        List<Inscricao> pendentes = new java.util.ArrayList<>();
        for (Oportunidade op : oportunidadeService.listarTodas()) {
            if (op.getDocenteResponsavel() == null || !op.getDocenteResponsavel().equals(d)) continue;
            for (Inscricao i : op.getInscricoes())
                if (i.getStatus() == StatusInscricao.PENDENTE) pendentes.add(i);
        }
        if (pendentes.isEmpty()) { System.out.println("Sem inscricoes pendentes."); return; }
        for (int i = 0; i < pendentes.size(); i++) System.out.println(i + ". " + pendentes.get(i));
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= pendentes.size()) { System.out.println("Indice invalido."); return; }
        String decisao = lerLinha("Aprovar (A) ou Rejeitar (R)? ");
        if (decisao.equalsIgnoreCase("A")) {
            inscricaoService.aprovar(d, pendentes.get(idx));
            System.out.println("Aprovada.");
        } else {
            String motivo = lerLinha("Motivo: ");
            inscricaoService.rejeitar(d, pendentes.get(idx), motivo);
            System.out.println("Rejeitada.");
        }
    }

    private static void substituirParticipante(Docente d) {
        List<Inscricao> aprovadas = new java.util.ArrayList<>();
        for (Oportunidade op : oportunidadeService.listarTodas()) {
            if (op.getDocenteResponsavel() == null || !op.getDocenteResponsavel().equals(d)) continue;
            for (Inscricao i : op.getInscricoes())
                if (i.getStatus() == StatusInscricao.APROVADA) aprovadas.add(i);
        }
        if (aprovadas.isEmpty()) { System.out.println("Sem participantes aprovados."); return; }
        for (int i = 0; i < aprovadas.size(); i++) System.out.println(i + ". " + aprovadas.get(i));
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= aprovadas.size()) { System.out.println("Indice invalido."); return; }
        String just = lerLinha("Justificativa da remocao: ");
        inscricaoService.substituirParticipante(d, aprovadas.get(idx), just);
        System.out.println("Participante removido. Proximo da fila promovido (se houver).");
    }

    private static void encerrarOportunidade(Docente d) {
        List<Oportunidade> abertas = new java.util.ArrayList<>();
        for (Oportunidade o : oportunidadeService.listarTodas()) {
            if (o.getDocenteResponsavel() != null && o.getDocenteResponsavel().equals(d)
                    && (o.getStatus() == StatusOportunidade.ABERTA
                    || o.getStatus() == StatusOportunidade.EM_EXECUCAO)) abertas.add(o);
        }
        if (abertas.isEmpty()) { System.out.println("Sem oportunidades para encerrar."); return; }
        abertas.forEach(System.out::println);
        int id = lerInt("ID: ");
        Oportunidade op = oportunidadeService.buscarPorId(id);
        if (op == null) { System.out.println("Nao encontrada."); return; }

        List<Inscricao> aprovadas = op.getInscricoes().stream()
                .filter(i -> i.getStatus() == StatusInscricao.APROVADA).toList();
        if (aprovadas.isEmpty()) {
            System.out.println("Nenhum aprovado para certificar.");
            oportunidadeService.encerrar(d, op, List.of());
            return;
        }
        List<Inscricao> concluintes = new java.util.ArrayList<>();
        System.out.println("Selecione os concluintes (indices separados por virgula):");
        for (int i = 0; i < aprovadas.size(); i++) System.out.println(i + ". " + aprovadas.get(i));
        String linha = lerLinha("Indices: ");
        for (String token : linha.split(",")) {
            try {
                int idx = Integer.parseInt(token.trim());
                if (idx >= 0 && idx < aprovadas.size()) concluintes.add(aprovadas.get(idx));
            } catch (NumberFormatException ignored) {}
        }
        oportunidadeService.encerrar(d, op, concluintes);
    }

    private static void criarGrupoDiscente(Docente d) {
        String nome = lerLinha("Nome do grupo: ");
        String desc = lerLinha("Descricao: ");
        String email = lerLinha("Email do grupo: ");
        GrupoDiscente g = grupoService.criarGrupo(d.getNome(), nome, desc, email, d);
        System.out.println("Grupo criado: " + g);
    }

    private static void gerenciarCargos(Docente d) {
        List<GrupoDiscente> meus = grupoService.listar().stream()
                .filter(g -> g.getResponsavel().equals(d)).toList();
        if (meus.isEmpty()) { System.out.println("Sem grupos."); return; }
        for (int i = 0; i < meus.size(); i++) System.out.println(i + ". " + meus.get(i));
        int idx = lerInt("Indice grupo: ");
        if (idx < 0 || idx >= meus.size()) return;
        GrupoDiscente g = meus.get(idx);

        System.out.println("1. Atribuir cargo  2. Remover cargo");
        int op = lerInt("Opcao: ");
        TipoCargo tc = lerEnum("Tipo de cargo", TipoCargo.values());

        List<Discente> discentes = usuarioService.listarDiscentes();
        if (discentes.isEmpty()) { System.out.println("Nenhum discente cadastrado."); return; }
        for (int i = 0; i < discentes.size(); i++) System.out.println(i + ". " + discentes.get(i).getNome());
        int didx = lerInt("Indice discente: ");
        if (didx < 0 || didx >= discentes.size()) return;
        Discente disc = discentes.get(didx);

        if (op == 1) {
            grupoService.atribuirCargo(d.getNome(), g, tc, disc);
            System.out.println("Cargo atribuido.");
        } else if (op == 2) {
            boolean ok = grupoService.removerCargo(d.getNome(), g, tc, disc);
            System.out.println(ok ? "Cargo removido." : "Cargo nao encontrado.");
        }
    }

    private static void exibirHistoricoCargos() {
        for (GrupoDiscente g : grupoService.listar()) {
            System.out.println("\nGrupo: " + g.getNome());
            List<HistoricoCargo> h = grupoService.historicoDoGrupo(g);
            if (h.isEmpty()) System.out.println("  (sem historico)");
            else h.forEach(hc -> System.out.println("  " + hc));
        }
    }

    // ============================================================
    //                     MENU COORDENADOR
    // ============================================================
    private static void menuCoordenador(Coordenador c) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n--- MENU COORDENADOR (" + c.getNome() + ") ---");
            System.out.println("1. Aprovar oportunidades aguardando");
            System.out.println("2. Avaliar solicitacoes de aproveitamento");
            System.out.println("3. Delegar solicitacao a comissao");
            System.out.println("4. Cadastrar nova versao do PPC");
            System.out.println("5. Listar historico do PPC");
            System.out.println("6. Criar oportunidade (rascunho)");
            System.out.println("0. Logout");
            int op = lerInt("Opcao: ");
            try {
                switch (op) {
                    case 1 -> aprovarOportunidadesCoord(c);
                    case 2 -> avaliarSolicitacoesCoord(c);
                    case 3 -> delegarSolicitacao(c);
                    case 4 -> cadastrarPPC(c);
                    case 5 -> ppcService.historico().forEach(System.out::println);
                    case 6 -> criarOportunidade(c, null);
                    case 0 -> stay = false;
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void aprovarOportunidadesCoord(Coordenador c) {
        List<Oportunidade> aguardando = oportunidadeService.listarPorStatus(StatusOportunidade.AGUARDANDO_APROVACAO);
        if (aguardando.isEmpty()) { System.out.println("Nenhuma oportunidade aguardando."); return; }
        aguardando.forEach(System.out::println);
        int id = lerInt("ID para aprovar (0 = cancelar): ");
        if (id == 0) return;
        Oportunidade op = oportunidadeService.buscarPorId(id);
        if (op == null) { System.out.println("Nao encontrada."); return; }
        oportunidadeService.aprovar(c, op);
        System.out.println("Aprovada e ABERTA.");
    }

    private static void avaliarSolicitacoesCoord(Coordenador c) {
        List<SolicitacaoAproveitamento> pend = aproveitamentoService.listarPendentesCoordenador();
        if (pend.isEmpty()) { System.out.println("Nenhuma pendente."); return; }
        LocalDate hoje = LocalDate.now();
        for (int i = 0; i < pend.size(); i++) {
            SolicitacaoAproveitamento s = pend.get(i);
            long rest = regraService.diasRestantesCoordenador(s, hoje);
            String alerta = rest < 0 ? " [PRAZO EXCEDIDO]" : " [" + rest + "d restantes]";
            System.out.println(i + ". " + s + alerta);
        }
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= pend.size()) return;
        SolicitacaoAproveitamento s = pend.get(idx);
        String decisao = lerLinha("Deferir (D) ou Indeferir (I)? ");
        String parecer = lerLinha("Parecer: ");
        if (decisao.equalsIgnoreCase("D")) {
            aproveitamentoService.deferir(c, s, parecer);
            System.out.println("Deferida. Horas creditadas ao discente.");
        } else {
            aproveitamentoService.indeferir(c, s, parecer);
            System.out.println("Indeferida.");
        }
    }

    private static void delegarSolicitacao(Coordenador c) {
        List<SolicitacaoAproveitamento> pend = aproveitamentoService.listarPendentesCoordenador();
        if (pend.isEmpty()) { System.out.println("Nada para delegar."); return; }
        for (int i = 0; i < pend.size(); i++) System.out.println(i + ". " + pend.get(i));
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= pend.size()) return;

        List<Usuario> comissoes = usuarioService.listarTodos().stream()
                .filter(u -> u instanceof Comissao).toList();
        if (comissoes.isEmpty()) { System.out.println("Nenhuma comissao cadastrada."); return; }
        for (int i = 0; i < comissoes.size(); i++) System.out.println(i + ". " + comissoes.get(i).getNome());
        int cidx = lerInt("Indice comissao: ");
        if (cidx < 0 || cidx >= comissoes.size()) return;
        aproveitamentoService.delegarParaComissao(c, pend.get(idx), (Comissao) comissoes.get(cidx));
        System.out.println("Delegada.");
    }

    private static void cadastrarPPC(Coordenador c) {
        String versao = lerLinha("Versao: ");
        int ch = lerInt("Carga horaria minima: ");
        LocalDate ini = lerData("Vigencia inicio: ");
        String hasFim = lerLinha("Tem data fim? (s/n): ");
        LocalDate fim = hasFim.equalsIgnoreCase("s") ? lerData("Vigencia fim: ") : null;
        PPC p = ppcService.cadastrarVersao(c.getNome(), versao, ch, c.getNome(), ini, fim);
        System.out.println("Cadastrado: " + p);
    }

    // ============================================================
    //                       MENU COMISSAO
    // ============================================================
    private static void menuComissao(Comissao c) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n--- MENU COMISSAO (" + c.getNome() + ") ---");
            System.out.println("1. Avaliar solicitacoes delegadas");
            System.out.println("0. Logout");
            int op = lerInt("Opcao: ");
            try {
                switch (op) {
                    case 1 -> {
                        List<SolicitacaoAproveitamento> pend = aproveitamentoService.listarPendentesComissao();
                        if (pend.isEmpty()) { System.out.println("Sem pendencias."); break; }
                        for (int i = 0; i < pend.size(); i++) System.out.println(i + ". " + pend.get(i));
                        int idx = lerInt("Indice: ");
                        if (idx < 0 || idx >= pend.size()) break;
                        SolicitacaoAproveitamento s = pend.get(idx);
                        String dec = lerLinha("Deferir (D) ou Indeferir (I)? ");
                        String parecer = lerLinha("Parecer: ");
                        if (dec.equalsIgnoreCase("D")) aproveitamentoService.deferirPelaComissao(c, s, parecer);
                        else aproveitamentoService.indeferir(c, s, parecer);
                        System.out.println("Decisao registrada.");
                    }
                    case 0 -> stay = false;
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    // ============================================================
    //                      MENU SECRETARIA
    // ============================================================
    private static void menuSecretaria(Secretaria s) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n--- MENU SECRETARIA (" + s.getNome() + ") ---");
            System.out.println("1. Listar todas oportunidades");
            System.out.println("2. Listar todos discentes e horas");
            System.out.println("3. Listar solicitacoes de aproveitamento");
            System.out.println("0. Logout");
            int op = lerInt("Opcao: ");
            switch (op) {
                case 1 -> oportunidadeService.listarTodas().forEach(System.out::println);
                case 2 -> usuarioService.listarDiscentes().forEach(d ->
                        System.out.println(d.getNome() + " - " + d.getHorasDeExtensaoAcumuladas() + "h"));
                case 3 -> aproveitamentoService.listarTodas().forEach(System.out::println);
                case 0 -> stay = false;
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    // ============================================================
    //                     MENU ADMINISTRADOR
    // ============================================================
    private static void menuAdministrador(Administrador a) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n--- MENU ADMIN (" + a.getNome() + ") ---");
            System.out.println("1. Cadastrar Docente");
            System.out.println("2. Cadastrar Coordenador");
            System.out.println("3. Cadastrar Comissao");
            System.out.println("4. Cadastrar Secretaria");
            System.out.println("5. Gerenciar perfis (permissoes por modulo/acao)");
            System.out.println("6. Visualizar logs de alteracoes");
            System.out.println("7. Listar todos os usuarios (polimorfismo)");
            System.out.println("8. Ativar/Inativar usuario");
            System.out.println("0. Logout");
            int op = lerInt("Opcao: ");
            try {
                switch (op) {
                    case 1 -> {
                        String nome = lerLinha("Nome: ");
                        String email = lerLinha("Email: ");
                        String siape = lerLinha("SIAPE: ");
                        String dep = lerLinha("Departamento: ");
                        usuarioService.cadastrarDocente(a.getNome(), nome, email, siape, dep);
                    }
                    case 2 -> {
                        String nome = lerLinha("Nome: ");
                        String email = lerLinha("Email: ");
                        String siape = lerLinha("SIAPE: ");
                        String curso = lerLinha("Curso: ");
                        usuarioService.cadastrarCoordenador(a.getNome(), nome, email, siape, curso);
                    }
                    case 3 -> {
                        String nome = lerLinha("Nome: ");
                        String email = lerLinha("Email: ");
                        String area = lerLinha("Area: ");
                        usuarioService.cadastrarComissao(a.getNome(), nome, email, area);
                    }
                    case 4 -> {
                        String nome = lerLinha("Nome: ");
                        String email = lerLinha("Email: ");
                        String setor = lerLinha("Setor: ");
                        usuarioService.cadastrarSecretaria(a.getNome(), nome, email, setor);
                    }
                    case 5 -> gerenciarPerfis(a);
                    case 6 -> logService.imprimirTodos();
                    case 7 -> demonstrarPolimorfismo();
                    case 8 -> alternarAtivacao();
                    case 0 -> stay = false;
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void gerenciarPerfis(Administrador a) {
        List<Perfil> perfis = perfilService.listar();
        for (int i = 0; i < perfis.size(); i++) System.out.println(i + ". " + perfis.get(i).getNome());
        int idx = lerInt("Perfil: ");
        if (idx < 0 || idx >= perfis.size()) return;
        Perfil p = perfis.get(idx);
        System.out.println(p);
        Modulo m = lerEnum("Modulo", Modulo.values());
        Acao ac = lerEnum("Acao", Acao.values());
        String dec = lerLinha("Conceder (C) ou Revogar (R)? ");
        perfilService.alterarPermissao(a.getNome(), p, m, ac, dec.equalsIgnoreCase("C"));
        System.out.println("Permissao atualizada.");
    }

    private static void alternarAtivacao() {
        List<Usuario> us = usuarioService.listarTodos();
        for (int i = 0; i < us.size(); i++)
            System.out.println(i + ". " + us.get(i) + (us.get(i).isAtivo() ? " [ATIVO]" : " [INATIVO]"));
        int idx = lerInt("Indice: ");
        if (idx < 0 || idx >= us.size()) return;
        Usuario u = us.get(idx);
        u.setAtivo(!u.isAtivo());
        logService.registrar("ADMIN",
                (u.isAtivo() ? "Reativou" : "Inativou") + " usuario " + u.getNome());
        System.out.println("Status atualizado.");
    }

    // ============================================================
    //                       UTILITARIOS I/O
    // ============================================================
    private static String lerLinha(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = sc.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Numero invalido, tente novamente.");
            }
        }
    }

    private static LocalDate lerData(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = sc.nextLine().trim();
            try {
                return LocalDate.parse(linha);
            } catch (Exception e) {
                System.out.println("Data invalida, use o formato AAAA-MM-DD.");
            }
        }
    }

    private static <E extends Enum<E>> E lerEnum(String label, E[] valores) {
        for (int i = 0; i < valores.length; i++) System.out.println(i + ". " + valores[i]);
        while (true) {
            int idx = lerInt(label + ": ");
            if (idx >= 0 && idx < valores.length) return valores[idx];
            System.out.println("Indice invalido.");
        }
    }
}
