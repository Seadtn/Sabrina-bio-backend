package com.sabrinaBio.application.Modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    private String name;
    private float price;
    private int quantity;
    private String creationDate;
    private boolean productNew;
    
    @ManyToOne
    private Category category;
    
    private boolean inSold;
    private boolean promotion;
    private int soldRatio;
    private String startDate;
    private String lastDate;
    private boolean active;
    
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_prices")
    @MapKeyColumn(name = "option_value")
    @Column(name = "price")
    private Map<Integer, Float> prices;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "available_options", joinColumns = @JoinColumn(name = "product_id"))
    private List<AvailableOption> availableOptions = new ArrayList<>();
    
    private boolean hasTaste;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tastes", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> tastes = new ArrayList<>();
}
