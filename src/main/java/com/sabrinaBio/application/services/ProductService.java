package com.sabrinaBio.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.DTO.BannerDTO;
import com.sabrinaBio.application.Modal.DTO.MostSellerDTO;
import com.sabrinaBio.application.Repository.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
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
}