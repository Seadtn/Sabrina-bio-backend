package com.sabrinaBio.application.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/productManagement/")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
	
	private final ProductRepository productRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId TUNISIA_ZONE = ZoneId.of("Africa/Tunis");

    @PostMapping("/newProduct")
    public ResponseEntity<?> createNewProduct(@RequestBody String productJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);
       
        try {
        if(product.isInSold()) {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; 
            String currentDateStr = currentDate.format(formatter);
            LocalDate promotionStartDate = LocalDate.parse(product.getStartDate(), formatter);
      
            if (currentDate.isEqual(promotionStartDate) || currentDate.isAfter(promotionStartDate)) {
                product.setPromotion(true);
            } else {
                product.setPromotion(false);
            }
            
            if (product.getCreationDate() == null || product.getCreationDate().isEmpty()) {
                product.setCreationDate(currentDateStr);
            }
            }
            
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body("Invalid date format. Please use YYYY-MM-DD format for dates");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error processing product: " + e.getMessage());
        }
        
    }
	
	@GetMapping("/getAllProducts")
	ResponseEntity<?> getAllProducts() {
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.findByActiveTrue());
	}
	
	@GetMapping("/getProductById/{id}")
	public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {	    
	    return productRepository.findById(id)
	        .map(product -> ResponseEntity.ok(new Gson().toJson(product)))
	        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                       .body("Product not found"));
	}
	
	@PostMapping("/deleteProduct/{id}")
	public void deleteProduct(@PathVariable("id") Long id) throws Exception {	    
        try {
            Product product = productRepository.findById(id).get();
            product.setActive(false);
            productRepository.save(product);
  
        } catch (Exception e) {
            throw new Exception("Failed to delete product with id: " + id + ". Error: " + e.getMessage());
        }
    }
	
	@GetMapping("/getBestSellers")
	public ResponseEntity<?> getBestSellers() {
	    List<Product> bestSellers = productRepository.findTop5ByActiveTrueOrderByQuantityAsc();
	    return ResponseEntity.status(HttpStatus.OK).body(bestSellers);
	}
	@GetMapping("/getRelatedProducts/{categoryId}")
	public ResponseEntity<?> getRelatedProducts(@PathVariable("categoryId") Long categoryId) {
	    List<Product> relatedProducts = productRepository.findTop4ByActiveTrueAndCategoryIdOrderByIdAsc(categoryId);
	    return ResponseEntity.status(HttpStatus.OK).body(relatedProducts);
	}
	
	@GetMapping("/getProductsSortedByNew")
	public ResponseEntity<?> getProductsSortedByNew() {
	    List<Product> sortedProducts = productRepository.findByActiveTrueOrderByProductNewDesc();
	    return ResponseEntity.status(HttpStatus.OK).body(sortedProducts);
	}
	
	@GetMapping("/getLatestMixedProducts")
	public ResponseEntity<?> getLatestMixedProductsWithoutPagination() {
	    try {
	        // Fetch up to 2 latest on-sale products sorted by startDate
	        List<Product> onSaleProducts = productRepository
	            .findTop2ByPromotionTrueAndProductNewFalseAndActiveTrueOrderByStartDateDesc();  // Without pagination
	        // Fetch up to 2 latest new products sorted by creationDate
	        List<Product> newProducts = productRepository
	            .findTop2ByPromotionFalseAndProductNewTrueAndActiveTrueOrderByCreationDateDesc();  // Without pagination
	        // Calculate the total fetched so far
	        int totalFetched = onSaleProducts.size() + newProducts.size();

	        // Determine how many additional regular products are needed to complete 4
	        int additionalRegularCount = Math.max(0, 4 - totalFetched);

	        // Fetch additional latest regular products sorted by creationDate
	        List<Product> regularProducts = new ArrayList<>();
	        if (additionalRegularCount > 0) {
	            regularProducts = productRepository
	                .findByPromotionFalseAndProductNewFalseAndActiveTrueOrderByCreationDateDesc();  // Without pagination
	        }

	        // Combine the results
	        List<Product> result = new ArrayList<>();
	        result.addAll(onSaleProducts);
	        result.addAll(newProducts);
	        result.addAll(regularProducts);

	        // Ensure the result has exactly 4 products (truncate if needed)
	        if (result.size() > 4) {
	            result = result.subList(0, 4);
	        }

	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error fetching mixed products: " + e.getMessage());
	    }
	}

	@Scheduled(cron = "0 0 0 * * *", zone = "Africa/Tunis")
	@Transactional
	public void updateSaleStatus() {
	    try {
	        log.info("Starting to update sale status at {}", LocalDateTime.now(TUNISIA_ZONE));
	        
	        // Get current date in Tunisia timezone
	        LocalDate currentDate = LocalDate.now(TUNISIA_ZONE);
	        LocalDate yesterdayDate = LocalDate.now(TUNISIA_ZONE).minusDays(1);
	        log.info("Current date for comparison (Tunisia): {}", currentDate);
	        
	        // Fetch products whose startDate or lastDate is today
	        List<Product> products = productRepository.findByStartOrEndDate(currentDate.toString(),yesterdayDate.toString());
	        log.info("Found {} products to check for sale status", products.size());
	        
	        for (Product product : products) {
	            try {
	                if (product.getStartDate() != null && product.getLastDate() != null) {
	                    LocalDate startDate = LocalDate.parse(product.getStartDate(), DATE_FORMATTER);
	                    LocalDate endDate = LocalDate.parse(product.getLastDate(), DATE_FORMATTER);
	                    
	                    log.info("Product ID: {}, Start: {}, End: {}, Current: {}", 
	                        product.getId(), startDate, endDate, currentDate);
	                    
	                    boolean shouldBeInPromotion = 
	                        (currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && 
	                        (currentDate.isEqual(endDate) || currentDate.isBefore(endDate));
	                    
	                    if (product.isPromotion() != shouldBeInPromotion) {
	                        log.info("Updating promotion status for product {} from {} to {}", 
	                            product.getId(), product.isPromotion(), shouldBeInPromotion);
	                        product.setPromotion(shouldBeInPromotion);
	                        
	                        // If promotion ends, set inSold to false and reset dates
	                        if (!shouldBeInPromotion) {
	                            log.info("Promotion ended for product {}. Setting inSold to false.", product.getId());
	                            product.setInSold(false);
	                            product.setSoldRatio(0);
	                            // Optionally, you can reset dates if required, but it's not mandatory
	                            product.setStartDate(null); // or ""
	                            product.setLastDate(null); // or ""
	                        }
	                        
	                        // Save only if changes have been made
	                        productRepository.save(product);
	                    }
	                }
	            } catch (DateTimeParseException e) {
	                log.error("Error parsing dates for product {}: {}", product.getId(), e.getMessage());
	            }
	        }
	        
	        log.info("Completed updating sale status");
	        
	    } catch (Exception e) {
	        log.error("Unexpected error in updateSaleStatus: {}", e.getMessage(), e);
	    }
	}


	@Scheduled(cron = "0 0 0 * * *", zone = "Africa/Tunis")
    @Transactional
    public void updateNewProductStatus() {
        try {
            log.info("Starting to update new product status at {}", LocalDateTime.now(TUNISIA_ZONE));
            
            // Get current date in Tunisia timezone
            LocalDate currentDate = LocalDate.now(TUNISIA_ZONE);
            log.info("Current date for comparison (Tunisia): {}", currentDate);
            
            List<Product> products = productRepository.findByActiveTrueAndProductNewTrue();
            log.info("Found {} active products to check for new status", products.size());
            
            for (Product product : products) {
                try {
                    if (product.getCreationDate() != null && product.isProductNew()) {
                        LocalDate creationDate = LocalDate.parse(product.getCreationDate(), DATE_FORMATTER);
                        LocalDate oneMonthAfterCreation = creationDate.plusMonths(1);
                        
                        log.info("Product ID: {}, Creation Date: {}, One Month After: {}, Current: {}", 
                            product.getId(), creationDate, oneMonthAfterCreation, currentDate);
                        
                        if (currentDate.isAfter(oneMonthAfterCreation)) {
                            log.info("Updating new product status to false for product {}", product.getId());
                            product.setProductNew(false);
                            productRepository.save(product);
                        }
                    }
                } catch (DateTimeParseException e) {
                    log.error("Error parsing creation date for product {}: {}", 
                        product.getId(), e.getMessage());
                }
            }
            
            log.info("Completed updating new product status");
            
        } catch (Exception e) {
            log.error("Error in updateNewProductStatus: {}", e.getMessage(), e);
        }
    }
}