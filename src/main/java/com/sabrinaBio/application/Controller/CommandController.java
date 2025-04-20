package com.sabrinaBio.application.Controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabrinaBio.application.Modal.Command;
import com.sabrinaBio.application.Modal.CommandProduct;
import com.sabrinaBio.application.Modal.Product;
import com.sabrinaBio.application.Modal.Status;
import com.sabrinaBio.application.Modal.DTO.CommandPaginatedResponse;
import com.sabrinaBio.application.Modal.DTO.CommandStats;
import com.sabrinaBio.application.Repository.CommandRepository;
import com.sabrinaBio.application.Repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/commandManagement/")
@RequiredArgsConstructor
public class CommandController {
	private final CommandRepository commandRepository;
	private final ProductRepository productRepository;
	
	@PostMapping("/newCommand")
	public ResponseEntity<?> createNewCommand(@RequestBody String commandJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Command command = objectMapper.readValue(commandJson, Command.class);

        for (CommandProduct commandProduct : command.getCommandProducts()) {
            Product product = productRepository.findById(commandProduct.getProduct().getId()).get();
            int orderedQuantity = commandProduct.getQuantity();
            if (product.getQuantity() >= orderedQuantity) {
                product.setQuantity(product.getQuantity() - orderedQuantity); 
                productRepository.save(product); 
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Not enough stock for product: " + product.getName());
            }
        }

        // Save the command after updating the products' quantities
        commandRepository.save(command);
        return ResponseEntity.status(HttpStatus.OK).body(command);
    }
	
	@GetMapping("/getAllCommands")
	public ResponseEntity<?> getAllCommands(
	    @RequestParam(defaultValue = "0") int offset,
	    @RequestParam(defaultValue = "10") int limit) {

	    Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("id")));
	    
	    Page<Command> paginatedCommands = commandRepository.findAll(pageable);
	    long totalElements = commandRepository.count();

	    long acceptedCount = commandRepository.countByStatus(Status.Accepted);
	    long pendingCount = commandRepository.countByStatus(Status.Pending);
	    long rejectedCount = commandRepository.countByStatus(Status.Rejected);
	    
	    double acceptedMoney = commandRepository.sumTotalPriceByStatus(Status.Accepted);
	    double pendingMoney = commandRepository.sumTotalPriceByStatus(Status.Pending);
	    double rejectedMoney = commandRepository.sumTotalPriceByStatus(Status.Rejected);

	    int totalPages = paginatedCommands.getTotalPages();

	    return ResponseEntity.status(HttpStatus.OK).body(new CommandPaginatedResponse(
	        paginatedCommands.getContent(), 
	        totalPages,
	        totalElements,
	        new CommandStats(
	            acceptedCount, acceptedMoney,
	            pendingCount, pendingMoney,
	            rejectedCount, rejectedMoney
	        )
	    ));
	}


	@PostMapping("/changeCommandStatus")
	public ResponseEntity<?> changeCommandStatus(@RequestBody String commandJson) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Command command = objectMapper.readValue(commandJson, Command.class);
	    return ResponseEntity.ok(commandRepository.save(command));
	}
}
