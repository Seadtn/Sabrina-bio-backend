package com.sabrinaBio.application.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabrinaBio.application.Modal.Command;
import com.sabrinaBio.application.Modal.CommandProduct;
import com.sabrinaBio.application.Modal.Product;
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
	ResponseEntity<?> getAllCommands() {
		return ResponseEntity.status(HttpStatus.OK).body(commandRepository.findAll());
	}
	@PostMapping("/changeCommandStatus")
	public ResponseEntity<?> changeCommandStatus(@RequestBody String commandJson) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Command command = objectMapper.readValue(commandJson, Command.class);
	    return ResponseEntity.ok(commandRepository.save(command));
	}
}
