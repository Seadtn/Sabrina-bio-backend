package com.sabrinaBio.application.Modal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Souscategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String arabicName;
    private String frenchName;
    private String englishName;
    private String creationDate;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}