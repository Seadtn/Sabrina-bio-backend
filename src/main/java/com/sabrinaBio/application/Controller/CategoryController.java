package com.sabrinaBio.application.Controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabrinaBio.application.Modal.Category;
import com.sabrinaBio.application.Repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/v1/categoryManagement/")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
	final private CategoryRepository categoryRepository;
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
}
