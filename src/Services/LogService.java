package Services;

import Entities.LogAlteracao;

import java.util.ArrayList;
import java.util.List;

public class LogService {

    private final List<LogAlteracao> logs = new ArrayList<>();

    public void registrar(String usuarioResponsavel, String operacao) {
        logs.add(new LogAlteracao(usuarioResponsavel, operacao));
    }

    public List<LogAlteracao> listar() {
        return logs;
    }

    public void imprimirTodos() {
        if (logs.isEmpty()) {
            System.out.println("(Nenhum log registrado)");
            return;
        }
        System.out.println("\n--- LOGS DE ALTERACOES ---");
        logs.forEach(System.out::println);
    }
}
