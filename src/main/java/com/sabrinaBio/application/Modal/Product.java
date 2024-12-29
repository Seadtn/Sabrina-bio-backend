package com.sabrinaBio.application.Modal;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

}
