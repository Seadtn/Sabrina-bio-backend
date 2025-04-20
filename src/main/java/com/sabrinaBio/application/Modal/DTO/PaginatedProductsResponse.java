package com.sabrinaBio.application.Modal.DTO;

import java.util.List;

import com.sabrinaBio.application.Modal.Product;

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
public class PaginatedProductsResponse {
    private List<Product> products;
    private long total;
}
