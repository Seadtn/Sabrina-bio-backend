package com.sabrinaBio.application.Modal.DTO;

import java.util.List;

import com.sabrinaBio.application.Modal.Command;

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
public class CommandPaginatedResponse {
    private List<Command> data;
    private int totalPages;
    private long totalElements;
    private CommandStats commandStats;
}
