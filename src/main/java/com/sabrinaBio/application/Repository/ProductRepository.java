package com.sabrinaBio.application.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    List<Product> findTop5ByActiveTrueOrderByQuantityAsc();
    List<Product> findTop4ByActiveTrueAndCategoryIdOrderByIdAsc(Long categoryId);
    List<Product> findByActiveTrueOrderByProductNewDesc();
    
    List<Product> findTop2ByPromotionTrueAndProductNewFalseAndActiveTrueOrderByStartDateDesc();
    List<Product> findTop2ByPromotionFalseAndProductNewTrueAndActiveTrueOrderByCreationDateDesc();
    List<Product> findByPromotionFalseAndProductNewFalseAndActiveTrueOrderByCreationDateDesc();
    
    List<Product> findByActiveFalse();

    List<Product> findByActiveTrue();
}
