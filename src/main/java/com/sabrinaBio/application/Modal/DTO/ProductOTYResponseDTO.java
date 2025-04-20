package com.sabrinaBio.application.Modal.DTO;


import com.sabrinaBio.application.Modal.ProductOfTheYear;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductOTYResponseDTO {
    private List<ProductOfTheYear> products;
    private long totalCount;
}
