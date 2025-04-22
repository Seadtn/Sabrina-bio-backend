package com.sabrinaBio.application.Controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabrinaBio.application.Modal.Category;
import com.sabrinaBio.application.Modal.Souscategory;
import com.sabrinaBio.application.Repository.CategoryRepository;
import com.sabrinaBio.application.Repository.SousCategoryRepository;
import com.sabrinaBio.application.services.ProductService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/v1/categoryManagement/")
@RequiredArgsConstructor
public class CategoryController {
	final private CategoryRepository categoryRepository;
	final private SousCategoryRepository sousCategoryRepository;
	private final ProductService productService;

	@PostMapping("/newCategory")
	public ResponseEntity<?> createNewCategory(@RequestBody String categoryJson) throws IOException {
			    ObjectMapper objectMapper = new ObjectMapper();
			    Category category = objectMapper.readValue(categoryJson, Category.class);
	    return ResponseEntity.status(HttpStatus.OK).body(categoryRepository.save(category));
	}
	
	@GetMapping("/getAllCategories")
	ResponseEntity<?> getAllCategories() {
		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(categoryRepository.findAll()));
	}
	
	@PostMapping("/newSousCategory")
	public ResponseEntity<?> createNewSousCategory(@RequestBody String categoryJson) throws IOException {
			    ObjectMapper objectMapper = new ObjectMapper();
			    Souscategory category = objectMapper.readValue(categoryJson, Souscategory.class);
	    return ResponseEntity.status(HttpStatus.OK).body(sousCategoryRepository.save(category));
	}
	
	@GetMapping("/getAllSousCategories")
	ResponseEntity<?> getAllSousCategories() {
		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(sousCategoryRepository.findAll()));
	}
	@GetMapping("/getSousCategoriesbyIdCategory/{id}")
	ResponseEntity<?> getSousCategoriesByCategoryId(@PathVariable("id") Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(sousCategoryRepository.findByCategoryId(id)));
	}
	@PostMapping("/promote-souscategory/{id}")
	public ResponseEntity<Category> promoteSubcategory(@PathVariable Long id) {
	    return ResponseEntity.ok(productService.promoteSubcategory(id));
	}
}
