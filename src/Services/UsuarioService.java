package Services;

import Entities.Administrador;
import Entities.Comissao;
import Entities.Coordenador;
import Entities.Discente;
import Entities.Docente;
import Entities.Secretaria;
import Entities.Usuario;
import Model.TipoUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final LogService logService;
    private final PerfilService perfilService;

    public UsuarioService(LogService logService, PerfilService perfilService) {
        this.logService = logService;
        this.perfilService = perfilService;
    }

    public Discente autocadastroDiscente(String nome, String email, String senha,
                                         int matricula, int semestre) {
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        Discente d = new Discente(nome, email, senha, matricula, semestre, 0);
        d.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.DISCENTE));
        usuarios.add(d);
        logService.registrar(nome, "Autocadastro de discente realizado");
        return d;
    }

    public Docente cadastrarDocente(String responsavel, String nome, String email,
                                    String siape, String departamento) {
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Docente d = new Docente(nome, email, senhaGerada, siape, departamento);
        d.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.DOCENTE));
        usuarios.add(d);
        enviarCredenciais(email, senhaGerada, "DOCENTE");
        logService.registrar(responsavel, "Cadastrou docente: " + nome);
        return d;
    }

    public Coordenador cadastrarCoordenador(String responsavel, String nome, String email,
                                            String siape, String curso) {
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Coordenador c = new Coordenador(nome, email, senhaGerada, siape, curso);
        c.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.COORDENADOR));
        usuarios.add(c);
        enviarCredenciais(email, senhaGerada, "COORDENADOR");
        logService.registrar(responsavel, "Cadastrou coordenador: " + nome);
        return c;
    }

    public Comissao cadastrarComissao(String responsavel, String nome, String email, String area) {
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Comissao c = new Comissao(nome, email, senhaGerada, area);
        c.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.COMISSAO));
        usuarios.add(c);
        enviarCredenciais(email, senhaGerada, "COMISSAO");
        logService.registrar(responsavel, "Cadastrou comissao: " + nome);
        return c;
    }

    public Secretaria cadastrarSecretaria(String responsavel, String nome, String email, String setor) {
        if (buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Secretaria s = new Secretaria(nome, email, senhaGerada, setor);
        s.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.SECRETARIA));
        usuarios.add(s);
        enviarCredenciais(email, senhaGerada, "SECRETARIA");
        logService.registrar(responsavel, "Cadastrou secretaria: " + nome);
        return s;
    }

    public Administrador cadastrarAdminInicial(String nome, String email, String senha) {
        Administrador a = new Administrador(nome, email, senha);
        a.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.ADMINISTRADOR));
        usuarios.add(a);
        return a;
    }

    public Usuario autenticar(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.autenticar(email, senha)) return u;
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst().orElse(null);
    }

    public List<Usuario> listarTodos() {
        return usuarios;
    }

    public List<Discente> listarDiscentes() {
        List<Discente> result = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Discente d) result.add(d);
        }
        return result;
    }

    public List<Docente> listarDocentes() {
        List<Docente> result = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Docente d) result.add(d);
        }
        return result;
    }

    private String gerarSenhaProvisoria() {
        return "senha" + (int) (Math.random() * 10000);
    }

    private void enviarCredenciais(String email, String senha, String perfil) {
        System.out.println("\n[ENVIO DE CREDENCIAIS - SIMULADO]");
        System.out.println("Para......: " + email);
        System.out.println("Perfil....: " + perfil);
        System.out.println("Senha tmp.: " + senha);
        System.out.println("(troque no primeiro acesso)\n");
    }
}
