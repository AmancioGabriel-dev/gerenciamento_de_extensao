package com.extensao.service;

import com.extensao.entity.*;
import com.extensao.model.TipoUsuario;
import com.extensao.repository.DiscenteRepository;
import com.extensao.repository.DocenteRepository;
import com.extensao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de cadastro e autenticacao de usuarios.
 * As validacoes (email unico, etc.) sao identicas ao original; mudou apenas
 * a fonte dos dados: agora repositorios em vez de List em memoria.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DiscenteRepository discenteRepository;
    private final DocenteRepository docenteRepository;
    private final LogService logService;
    private final PerfilService perfilService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          DiscenteRepository discenteRepository,
                          DocenteRepository docenteRepository,
                          LogService logService,
                          PerfilService perfilService) {
        this.usuarioRepository = usuarioRepository;
        this.discenteRepository = discenteRepository;
        this.docenteRepository = docenteRepository;
        this.logService = logService;
        this.perfilService = perfilService;
    }

    @Transactional
    public Discente autocadastroDiscente(String nome, String email, String senha,
                                         int matricula, int semestre) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        Discente d = new Discente(nome, email, senha, matricula, semestre, 0);
        d.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.DISCENTE));
        d = usuarioRepository.save(d);
        logService.registrar(nome, "Autocadastro de discente realizado");
        return d;
    }

    @Transactional
    public Docente cadastrarDocente(String responsavel, String nome, String email,
                                    String siape, String departamento) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Docente d = new Docente(nome, email, senhaGerada, siape, departamento);
        d.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.DOCENTE));
        d = usuarioRepository.save(d);
        enviarCredenciais(email, senhaGerada, "DOCENTE");
        logService.registrar(responsavel, "Cadastrou docente: " + nome);
        return d;
    }

    @Transactional
    public Coordenador cadastrarCoordenador(String responsavel, String nome, String email,
                                            String siape, String curso) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Coordenador c = new Coordenador(nome, email, senhaGerada, siape, curso);
        c.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.COORDENADOR));
        c = usuarioRepository.save(c);
        enviarCredenciais(email, senhaGerada, "COORDENADOR");
        logService.registrar(responsavel, "Cadastrou coordenador: " + nome);
        return c;
    }

    @Transactional
    public Comissao cadastrarComissao(String responsavel, String nome, String email, String area) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Comissao c = new Comissao(nome, email, senhaGerada, area);
        c.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.COMISSAO));
        c = usuarioRepository.save(c);
        enviarCredenciais(email, senhaGerada, "COMISSAO");
        logService.registrar(responsavel, "Cadastrou comissao: " + nome);
        return c;
    }

    @Transactional
    public Secretaria cadastrarSecretaria(String responsavel, String nome, String email, String setor) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }
        String senhaGerada = gerarSenhaProvisoria();
        Secretaria s = new Secretaria(nome, email, senhaGerada, setor);
        s.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.SECRETARIA));
        s = usuarioRepository.save(s);
        enviarCredenciais(email, senhaGerada, "SECRETARIA");
        logService.registrar(responsavel, "Cadastrou secretaria: " + nome);
        return s;
    }

    @Transactional
    public Administrador cadastrarAdminInicial(String nome, String email, String senha) {
        Administrador a = new Administrador(nome, email, senha);
        a.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.ADMINISTRADOR));
        return usuarioRepository.save(a);
    }

    public Usuario autenticar(String email, String senha) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .filter(u -> u.autenticar(email, senha))
                .orElse(null);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Discente> listarDiscentes() {
        return discenteRepository.findAll();
    }

    public List<Docente> listarDocentes() {
        return docenteRepository.findAll();
    }

    @Transactional
    public void alternarAtivacao(Usuario u) {
        u.setAtivo(!u.isAtivo());
        usuarioRepository.save(u);
        logService.registrar("ADMIN",
                (u.isAtivo() ? "Reativou" : "Inativou") + " usuario " + u.getNome());
    }

    private String gerarSenhaProvisoria() {
        return "senha" + (int) (Math.random() * 10000);
    }

    private void enviarCredenciais(String email, String senha, String perfil) {
        // Simulacao de envio de email (igual ao projeto original).
        System.out.println("\n[ENVIO DE CREDENCIAIS - SIMULADO]");
        System.out.println("Para......: " + email);
        System.out.println("Perfil....: " + perfil);
        System.out.println("Senha tmp.: " + senha);
        System.out.println("(troque no primeiro acesso)\n");
    }
}
