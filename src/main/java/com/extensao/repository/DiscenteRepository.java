package com.extensao.repository;

import com.extensao.entity.Discente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Com a heranca SINGLE_TABLE, este repositorio retorna SOMENTE os discentes
 * (o Spring filtra automaticamente pelo discriminador). Substitui o antigo
 * "listarDiscentes()" que varria a lista com instanceof.
 */
public interface DiscenteRepository extends JpaRepository<Discente, Long> {
}
