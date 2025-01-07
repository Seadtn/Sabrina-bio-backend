package com.sabrinaBio.application.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Souscategory;
@Repository
public interface SousCategoryRepository extends JpaRepository<Souscategory, Long> {
 List<Souscategory> findByCategoryId(long id );
}
