package com.sabrinaBio.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.DTO.BannerDTO;
import com.sabrinaBio.application.Modal.DTO.MostSellerDTO;
import com.sabrinaBio.application.Repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    
    public List<MostSellerDTO> getBestSellers() {
        return productRepository.findTop5ByActiveTrueOrderByQuantityAsc()
            .stream()
            .map(this::mapToMostSellerDTO)
            .collect(Collectors.toList());
    }
    
    private MostSellerDTO mapToMostSellerDTO(Product product) {
        return MostSellerDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .nameFr(product.getNameFr())
            .nameEng(product.getNameEng())
            .price(product.getPrice())
            .image(product.getImage())
            .prices(product.getPrices())
            .promotion(product.isPromotion())
            .soldRatio(product.getSoldRatio())
            .availableOptions(product.getAvailableOptions())
            .build();
    }
    
    public List<BannerDTO> getLatestMixedProducts() {
        // Get promotional products
        List<BannerDTO> promotionalProducts = productRepository.findTop2PromotionalBanners();
        
        // Get new products
        List<BannerDTO> newProducts = productRepository.findTop2NewBanners();
        
        // Calculate how many regular products we need
        int totalFetched = promotionalProducts.size() + newProducts.size();
        int additionalRegularCount = Math.max(0, 4 - totalFetched);
        
        // Get regular products if needed
        List<BannerDTO> regularProducts = new ArrayList<>();
        if (additionalRegularCount > 0) {
			regularProducts = productRepository.findRegularBanners();
        }
        
        // Combine all products
        List<BannerDTO> result = new ArrayList<>();
        result.addAll(promotionalProducts);
        result.addAll(newProducts);
        result.addAll(regularProducts);
        
        // Limit to 4 products
        return result.size() > 4 ? result.subList(0, 4) : result;
    }
    public List<Product> findFilteredProducts(Long categoryId, Long subcategoryId, String search, String sort, Pageable pageable) {
        List<Product> products = productRepository.findFilteredProducts(categoryId, subcategoryId, search, sort, pageable);
        
        // Post-process the list if sorting by price and promotions need to be considered
        if (sort != null && (sort.equals("highPrice") || sort.equals("lowPrice"))) {
            products.sort((p1, p2) -> {
                float price1 = p1.isPromotion() ? p1.getPrice() * (1 - p1.getSoldRatio() * 0.01f) : p1.getPrice();
                float price2 = p2.isPromotion() ? p2.getPrice() * (1 - p2.getSoldRatio() * 0.01f) : p2.getPrice();
                
                int comparison = Float.compare(price1, price2);
                return sort.equals("highPrice") ? -comparison : comparison;
            });
        }
        
        return products;
    }
}