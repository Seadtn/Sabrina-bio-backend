package com.sabrinaBio.application.Modal;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AvisClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private AvisType type;
    
    private boolean active;

    // For before/after type
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] beforeImageUrl;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] afterImageUrl;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] avisImageUrl;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] commentImage1;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] commentImage2;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] commentImage3;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] commentImage4;
    
    @ManyToOne
    private Product product;
    
    
}
