package com.extensao.dto;

import com.extensao.entity.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * DTOs (Data Transfer Objects) do dominio de usuarios.
 *
 * Por que DTOs? Para nao expor as ENTIDADES diretamente na API:
 *  - evita recursao infinita no JSON (relacionamentos bidirecionais)
 *  - nao vaza a senha
 *  - deixa a API estavel mesmo que a entidade mude
 *
 * Sao "records" (classes imutaveis e enxutas do Java) agrupados por dominio.
 */
public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    // ---------- REQUESTS (entram na API) ----------

    public record LoginRequest(@NotBlank String email, @NotBlank String senha) {
    }

    public record AutocadastroDiscenteRequest(
            @NotBlank String nome, @NotBlank String email, @NotBlank String senha,
            int matricula, int semestre) {
    }

    public record CadastroDocenteRequest(
            String responsavel, @NotBlank String nome, @NotBlank String email,
            String siape, String departamento) {
    }

    public record CadastroCoordenadorRequest(
            String responsavel, @NotBlank String nome, @NotBlank String email,
            String siape, String curso) {
    }

    public record CadastroComissaoRequest(
            String responsavel, @NotBlank String nome, @NotBlank String email, String area) {
    }

    public record CadastroSecretariaRequest(
            String responsavel, @NotBlank String nome, @NotBlank String email, String setor) {
    }

    // ---------- RESPONSE (sai da API) ----------

    /**
     * Resposta polimorfica: campos comuns + campos especificos do tipo.
     * O metodo from() inspeciona o tipo concreto (POLIMORFISMO) para preencher
     * os campos certos -- equivalente ao antigo "exibirDashboard()".
     */
    public record UsuarioResponse(
            Long id, String tipo, String nome, String email, boolean ativo,
            LocalDate dataCadastro, String perfil,
            Integer matricula, Integer semestre, Integer horasAcumuladas,
            String siape, String departamento, String curso, String area, String setor) {

        public static UsuarioResponse from(Usuario u) {
            Integer matricula = null, semestre = null, horas = null;
            String siape = null, departamento = null, curso = null, area = null, setor = null;

            if (u instanceof Discente d) {
                matricula = d.getMatricula();
                semestre = d.getSemestre();
                horas = d.getHorasDeExtensaoAcumuladas();
            } else if (u instanceof Docente doc) {
                siape = doc.getSiape();
                departamento = doc.getDepartamento();
            } else if (u instanceof Coordenador c) {
                siape = c.getSiape();
                curso = c.getCurso();
            } else if (u instanceof Comissao com) {
                area = com.getArea();
            } else if (u instanceof Secretaria s) {
                setor = s.getSetor();
            }

            return new UsuarioResponse(
                    u.getId(), u.getTipo().name(), u.getNome(), u.getEmail(), u.isAtivo(),
                    u.getDataCadastro(), u.getPerfil() == null ? null : u.getPerfil().getNome(),
                    matricula, semestre, horas, siape, departamento, curso, area, setor);
        }
    }
}
