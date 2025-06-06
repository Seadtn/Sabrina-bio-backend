package com.sabrinaBio.application.Modal.DTO;

import java.util.List;
import java.util.Map;

import com.sabrinaBio.application.Modal.AvailableOption;
import com.sabrinaBio.application.Modal.ProductType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MostSellerDTO {
    private Long id;
	private String name;
    private String nameFr;
    private String nameEng;
    private String image;
    private boolean freeDelivery;
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    private float price;
    private Map<Integer, Float> prices;
    private boolean promotion;
    private int soldRatio;
    private List<AvailableOption> availableOptions;
    
}
