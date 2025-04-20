package com.sabrinaBio.application.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.ProductOfTheYear;
import com.sabrinaBio.application.Modal.DTO.ProductOTYResponseDTO;
import com.sabrinaBio.application.Repository.ProductOTYRepository;
import com.sabrinaBio.application.Repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/productoty/")
@RequiredArgsConstructor
public class ProductOTYController {

	private final ProductOTYRepository otyRepository;
	private final ProductRepository productRepository;

	@GetMapping
	public ResponseEntity<ProductOTYResponseDTO> getAllProductOTY(
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "10") int limit) {

		// Create Pageable with offset and limit
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<ProductOfTheYear> pageResult = otyRepository.findAll(pageable);

		// Get the content (List) of the page
		List<ProductOfTheYear> products = pageResult.getContent();

		// Get the total count of products
		long totalCount = pageResult.getTotalElements();

		// Return the response containing products and total count
		ProductOTYResponseDTO response = new ProductOTYResponseDTO(products, totalCount);
		return ResponseEntity.ok(response);
	}

	// GET ProductOTY by ID
	@GetMapping("/{id}")
	public ResponseEntity<ProductOfTheYear> getProductOTYById(@PathVariable Long id) {
		Optional<ProductOfTheYear> productOTY = otyRepository.findById(id);
		return productOTY.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// GET ProductOTY by ID
	@GetMapping("/active")
	public ResponseEntity<ProductOfTheYear> getProductOTYByActive() {
	    Optional<ProductOfTheYear> productOTY = otyRepository.findByActiveTrue();

	    // Check if a product is found
	    if (productOTY.isPresent()) {
	        return ResponseEntity.ok(productOTY.get()); // Return the found product
	    } else {

	    	return ResponseEntity.ok(null);  
	    }
	}


	@PostMapping
	@Transactional
	public ResponseEntity<?> createProductOTY(@RequestBody ProductOfTheYear productOTY) {
		Long productId = productOTY.getProduct().getId();

		// Check if this product already has a ProductOfTheYear entry
		Optional<ProductOfTheYear> existingOTY = otyRepository.findByProductId(productId);
		if (existingOTY.isPresent()) {
			// Instead of returning conflict, update the existing entry
			ProductOfTheYear existing = existingOTY.get();

			// Update fields from the request
			existing.setActive(productOTY.isActive());
			// Update other fields as needed...

			ProductOfTheYear updated = otyRepository.save(existing);
			return ResponseEntity.ok(updated);
		}

		// Continue with creating a new entry if none exists
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		productOTY.setProduct(existingProduct);
		ProductOfTheYear createdProductOTY = otyRepository.save(productOTY);

		return ResponseEntity.status(HttpStatus.CREATED).body(createdProductOTY);
	}

	// PUT update an existing ProductOTY
	@PutMapping("/{id}")
	public ResponseEntity<?> updateProductOTY(@PathVariable Long id, @RequestBody ProductOfTheYear updatedProductOTY) {
		if (!otyRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		// Get the existing ProductOTY
		ProductOfTheYear existingOTY = otyRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product of the Year not found"));

		// Check if we're trying to activate this ProductOTY
		if (!existingOTY.isActive() && updatedProductOTY.isActive()) {
			// Find any existing active Product of the Year (that is not this one)
			Optional<ProductOfTheYear> activeOTY = otyRepository.findByActiveTrue();

			// If one exists, deactivate it
			if (activeOTY.isPresent() && !activeOTY.get().getId().equals(id)) {
				ProductOfTheYear currentActive = activeOTY.get();
				currentActive.setActive(false);
				otyRepository.save(currentActive);
			}
		}

		// Check if the referenced product exists
		Product product = updatedProductOTY.getProduct();
		if (product != null) {
			Product existingProduct = productRepository.findById(product.getId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			// Set the full product entity to avoid detached entity issues
			updatedProductOTY.setProduct(existingProduct);
		}

		updatedProductOTY.setId(id);
		ProductOfTheYear productOTY = otyRepository.save(updatedProductOTY);
		return ResponseEntity.ok(productOTY);
	}

	// DELETE ProductOTY by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProductOTY(@PathVariable Long id) {
		if (!otyRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		otyRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
