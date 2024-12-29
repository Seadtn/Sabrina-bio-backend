package com.sabrinaBio.application.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
