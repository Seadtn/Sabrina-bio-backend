package com.sabrinaBio.application.Modal;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AvailableOption {
    private String value;
    private String unit;
}

