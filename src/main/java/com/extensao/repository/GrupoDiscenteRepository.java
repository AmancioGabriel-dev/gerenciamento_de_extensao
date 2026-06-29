package com.extensao.repository;

import com.extensao.entity.Docente;
import com.extensao.entity.GrupoDiscente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoDiscenteRepository extends JpaRepository<GrupoDiscente, Long> {

    List<GrupoDiscente> findByResponsavel(Docente responsavel);
}
