package com.sabrinaBio.application.Modal.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.DTO.ProductAdminDTO;

@Component
public class ProductTableMapper {

    public ProductAdminDTO toDTO(Product product) {
        return ProductAdminDTO.builder()
                .id(product.getId())
                .image(product.getImage())
                .name(product.getName())
                .nameFr(product.getNameFr())
                .nameEng(product.getNameEng())
                .price(product.getPrice())
                .prices(product.getPrices())
                .soldRatio(product.getSoldRatio())
                .promotion(product.isPromotion())
                .quantity(product.getQuantity())
                .freeDelivery(product.isFreeDelivery())
                .lastDate(product.getLastDate())
                .startDate(product.getStartDate())
                .productType(product.getProductType())
                .creationDate(product.getCreationDate())
                .taste(product.getTastes())
                .availableOptions(product.getAvailableOptions())
                .hasTaste(product.isHasTaste())
                .active(product.isActive())
                .description(product.getDescription())
                .productNew(product.isProductNew())
                .categoryName(product.getCategory() != null ? product.getCategory().getFrenchName() : null)
                .subCategoryName(product.getSouscategory() != null ? product.getSouscategory().getFrenchName() : null)
                .build();
    }

    public List<ProductAdminDTO> toDTOList(List<Product> products) {
        return products.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }
}

