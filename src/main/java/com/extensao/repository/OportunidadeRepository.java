package com.extensao.repository;

import com.extensao.entity.Oportunidade;
import com.extensao.model.StatusOportunidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Long> {

    List<Oportunidade> findByStatus(StatusOportunidade status);
}
