package com.sabrinaBio.application.Modal.DTO;

import java.util.List;
import java.util.Map;

import com.sabrinaBio.application.Modal.AvailableOption;
import com.sabrinaBio.application.Modal.ProductType;


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
public class ProductAdminDTO {
    private Long id;
	private String image;
    private String name;
    private String nameFr;
    private String nameEng;
    private float price;
    private Map<Integer, Float> prices;
    private int soldRatio;
    private boolean promotion;
    private int quantity;
    private String creationDate;
    private String description;
    private boolean freeDelivery;
    private boolean productNew;
    private boolean active;
    private String startDate;
    private String lastDate;
    private ProductType productType;
    private String categoryName;
    private String subCategoryName;
    private List<AvailableOption> availableOptions;
    private boolean hasTaste;
    private List<String> taste;
    

}
