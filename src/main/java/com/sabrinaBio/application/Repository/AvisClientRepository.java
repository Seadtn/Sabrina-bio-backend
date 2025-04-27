package com.sabrinaBio.application.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sabrinaBio.application.Modal.AvisClient;
import com.sabrinaBio.application.Modal.AvisType;

public interface AvisClientRepository extends JpaRepository<AvisClient, Long>{
    Optional<AvisClient> findByProductIdAndType(Long productId, AvisType type);

}
