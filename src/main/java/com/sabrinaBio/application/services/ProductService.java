package com.sabrinaBio.application.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sabrinaBio.application.Modal.Category;
import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.Souscategory;
import com.sabrinaBio.application.Modal.DTO.BannerDTO;
import com.sabrinaBio.application.Modal.DTO.MostSellerDTO;
import com.sabrinaBio.application.Modal.DTO.ProductAdminDTO;
import com.sabrinaBio.application.Modal.Mapper.ProductTableMapper;
import com.sabrinaBio.application.Repository.CategoryRepository;
import com.sabrinaBio.application.Repository.ProductRepository;
import com.sabrinaBio.application.Repository.SousCategoryRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional

public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final SousCategoryRepository sousCategoryRepository;

	private final ProductTableMapper productMapper;

	public List<MostSellerDTO> getBestSellers() {
		return productRepository.findTop6ByActiveTrueOrderByQuantityAsc().stream().map(this::mapToMostSellerDTO)
				.collect(Collectors.toList());
	}

	private MostSellerDTO mapToMostSellerDTO(Product product) {
		return MostSellerDTO.builder().id(product.getId()).name(product.getName()).nameFr(product.getNameFr())
				.nameEng(product.getNameEng()).price(product.getPrice()).productType(product.getProductType())
				.freeDelivery(product.isFreeDelivery()).image(product.getImage()).prices(product.getPrices())
				.promotion(product.isPromotion()).soldRatio(product.getSoldRatio())
				.availableOptions(product.getAvailableOptions()).build();
	}

	public List<BannerDTO> getLatestMixedProducts() {
		// Get new products
		List<BannerDTO> newProducts = productRepository.findTop4NewBanners(PageRequest.of(0, 4));

		// Initialize promotional products list
		List<BannerDTO> promotionalProducts = new ArrayList<>();

		// If fewer than 4 new products, fetch promotional ones to fill the gap
		if (newProducts.size() < 4) {
			int remaining = 4 - newProducts.size();
			promotionalProducts = productRepository.findTopPromotionalBanners(PageRequest.of(0, remaining));
		}

		// Calculate how many more products we need
		int totalFetched = newProducts.size() + promotionalProducts.size();
		int remaining = 4 - totalFetched;

		// Fetch regular products if needed
		List<BannerDTO> regularProducts = remaining > 0
				? productRepository.findRegularBanners(PageRequest.of(0, remaining))
				: Collections.emptyList();

		// Combine all lists
		List<BannerDTO> result = new ArrayList<>(4);
		result.addAll(newProducts);
		result.addAll(promotionalProducts);
		result.addAll(regularProducts);
		// Return exactly 4 products
		return result.size() > 4 ? result.subList(0, 4) : result;
	}

	public List<MostSellerDTO> findFilteredProducts(Long categoryId, Long subcategoryId, String search, String sort,
	        Pageable pageable) {

	    List<Product> products = productRepository.findFilteredProducts(categoryId, subcategoryId, search, sort, pageable);

	    // Post-process sorting if needed
	    if (sort != null && (sort.equals("highPrice") || sort.equals("lowPrice"))) {
	        products.sort((p1, p2) -> {
	            float price1 = p1.isPromotion() ? p1.getPrice() * (1 - p1.getSoldRatio() * 0.01f) : p1.getPrice();
	            float price2 = p2.isPromotion() ? p2.getPrice() * (1 - p2.getSoldRatio() * 0.01f) : p2.getPrice();

	            int comparison = Float.compare(price1, price2);
	            return sort.equals("highPrice") ? -comparison : comparison;
	        });
	    }

	    // Convert to MostSellerDTO
	    return products.stream()
	            .map(this::mapToMostSellerDTO)
	            .collect(Collectors.toList());
	}

	public Page<ProductAdminDTO> findFilteredProductsPageTable(Long categoryId, Long subcategoryId, String search,
			String sort, Pageable pageable) {
		Sort sorting = pageable.getSort(); // Use the sort from pageable if it's passed in.

		// If sort isn't applied yet, apply it based on the provided parameter
		if (sorting.isUnsorted()) {
			if ("highPrice".equals(sort)) {
				sorting = Sort.by("price").descending();
			} else if ("lowPrice".equals(sort)) {
				sorting = Sort.by("price").ascending();
			} else if ("name".equals(sort)) {
				sorting = Sort.by("name").ascending();
			} else {
				sorting = Sort.by("id").descending();
			}
		}

		// Apply the sorting to the pageable object
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);

		// Fetch the paginated products
		Page<Product> productsPage = productRepository.findFilteredProductsTable(categoryId, subcategoryId, search,
				pageable);
        List<ProductAdminDTO> productAdminDTOList = productMapper.toDTOList(productsPage.getContent());

        return new PageImpl<>(productAdminDTOList, pageable, productsPage.getTotalElements());
	}

	public Category promoteSubcategory(Long sousCategoryId) {
		// 1. Fetch the subcategory or throw if not found
		Souscategory souscategory = sousCategoryRepository.findById(sousCategoryId)
				.orElseThrow(() -> new RuntimeException("Sous-category not found"));

		// 2. Determine the new 'tri' value
		long totalCategories = categoryRepository.count();

		// 3. Create a new category from the sous-category's data
		Category newCategory = Category.builder().arabicName(souscategory.getArabicName())
				.frenchName(souscategory.getFrenchName()).englishName(souscategory.getEnglishName())
				.creationDate(souscategory.getCreationDate()).tri(totalCategories + 1).products(new ArrayList<>())
				.build();

		// 4. Save the new category
		Category savedCategory = categoryRepository.save(newCategory);

		// 5. Reassign products
		List<Product> products = productRepository.findBySouscategoryId(sousCategoryId);
		for (Product product : products) {
			product.setCategory(savedCategory);
			product.setSouscategory(null);
		}
		productRepository.saveAll(products);

		// 6. Delete the subcategory now that it's promoted
		sousCategoryRepository.delete(souscategory);

		return savedCategory;
	}

}