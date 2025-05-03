package com.sabrinaBio.application.Modal.DTO;




import com.sabrinaBio.application.Modal.Status;

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
public class CommandDTO {
	private Long id;
    private Status status;
    private String firstName;
    private String lastName;
    private String mail;
    private String phone;
    private String phone2;
    private String creationDate;
    private String confirmationDate;
    private String city;
    private long postalCode;
    private String paymentMethod;
    private float totalPrice;
}
