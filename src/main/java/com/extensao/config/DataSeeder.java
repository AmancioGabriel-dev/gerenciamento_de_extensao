package com.extensao.config;

import com.extensao.entity.Coordenador;
import com.extensao.entity.Docente;
import com.extensao.model.TipoUsuario;
import com.extensao.repository.UsuarioRepository;
import com.extensao.service.PPCService;
import com.extensao.service.PerfilService;
import com.extensao.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Carga inicial de dados (equivalente ao antigo carregarDadosIniciais()).
 *
 * CommandLineRunner -> o Spring executa o metodo run() automaticamente assim
 * que a aplicacao termina de subir. Como o banco H2 e em memoria e reseta a
 * cada reinicio, recriamos perfis padrao + usuarios de exemplo toda vez.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PerfilService perfilService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PPCService ppcService;

    public DataSeeder(PerfilService perfilService, UsuarioService usuarioService,
                      UsuarioRepository usuarioRepository, PPCService ppcService) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.ppcService = ppcService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1) Perfis padrao (ADMIN, COORDENADOR, DOCENTE, COMISSAO, SECRETARIA, DISCENTE)
        perfilService.criarPerfisPadraoSeNecessario();

        // 2) Usuarios de exemplo (somente se o banco estiver vazio)
        if (usuarioRepository.count() == 0) {
            usuarioService.cadastrarAdminInicial("Admin Geral", "admin@univ.edu", "admin123");

            Docente docSeed = new Docente("Prof. Ana", "ana@univ.edu", "docente123", "S001", "Computacao");
            docSeed.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.DOCENTE));
            usuarioRepository.save(docSeed);

            Coordenador coordSeed = new Coordenador("Coord. Bruno", "bruno@univ.edu", "coord123",
                    "S002", "Ciencia da Computacao");
            coordSeed.setPerfil(perfilService.obterPerfilPadrao(TipoUsuario.COORDENADOR));
            usuarioRepository.save(coordSeed);

            ppcService.cadastrarVersao("SISTEMA", "1.0", 345, "Coord. Bruno",
                    LocalDate.now().minusYears(2), null);

            System.out.println("============================================================");
            System.out.println(" SEED carregado:");
            System.out.println("   Admin......: admin@univ.edu / admin123");
            System.out.println("   Docente....: ana@univ.edu   / docente123");
            System.out.println("   Coordenador: bruno@univ.edu / coord123");
            System.out.println(" API em http://localhost:8080  | H2 em /h2-console");
            System.out.println("============================================================");
        }
    }
}
