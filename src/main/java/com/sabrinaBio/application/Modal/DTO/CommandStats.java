package com.sabrinaBio.application.Modal.DTO;

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
public class CommandStats {

	    private long acceptedCount;
	    private double acceptedMoney;
	    private long pendingCount;
	    private double pendingMoney;
	    private long rejectedCount;
	    private double rejectedMoney;

}
