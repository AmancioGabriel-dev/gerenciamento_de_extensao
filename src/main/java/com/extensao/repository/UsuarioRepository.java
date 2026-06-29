package com.extensao.repository;

import com.extensao.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de Usuario.
 *
 * CONCEITO NOVO: basta estender JpaRepository<Entidade, TipoDoId> e voce ja
 * ganha de graca: save, findById, findAll, delete, count...
 * O Spring GERA a implementacao em tempo de execucao. (Substitui as
 * "List<Usuario> em memoria" dos services antigos.)
 *
 * Metodos "derivados": o Spring entende o NOME do metodo e cria a query.
 * findByEmailIgnoreCase -> SELECT * FROM usuario WHERE LOWER(email)=LOWER(?)
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
