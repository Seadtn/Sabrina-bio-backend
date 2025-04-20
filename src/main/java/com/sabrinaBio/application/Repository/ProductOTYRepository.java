package com.sabrinaBio.application.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.ProductOfTheYear;

@Repository
public interface ProductOTYRepository extends JpaRepository<ProductOfTheYear, Long> {
	Optional<ProductOfTheYear> findByActiveTrue();
	 Optional<ProductOfTheYear> findByProductId(Long productId);
}
