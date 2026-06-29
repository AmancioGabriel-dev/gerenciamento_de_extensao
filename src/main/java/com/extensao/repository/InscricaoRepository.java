package com.extensao.repository;

import com.extensao.entity.Discente;
import com.extensao.entity.Docente;
import com.extensao.entity.Inscricao;
import com.extensao.model.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByDiscente(Discente discente);

    // Navega pelas propriedades: Inscricao -> oportunidade -> docenteResponsavel
    List<Inscricao> findByOportunidadeDocenteResponsavelAndStatus(Docente docente, StatusInscricao status);
}
