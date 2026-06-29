package com.extensao.repository;

import com.extensao.entity.PPC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PPCRepository extends JpaRepository<PPC, Long> {

    // Pega a versao mais recente (maior id) para a regra de encerrar a anterior.
    Optional<PPC> findTopByOrderByIdDesc();
}
