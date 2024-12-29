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
import com.google.gson.Gson;
import com.sabrinaBio.application.Modal.Command;
import com.sabrinaBio.application.Modal.Status;
import com.sabrinaBio.application.Repository.CommandRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/commandManagement/")
@RequiredArgsConstructor
public class CommandController {
	private final CommandRepository commandRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@PostMapping("/newCommand")
	public ResponseEntity<?> createNewCommand(@RequestBody String commandJson) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Command command = objectMapper.readValue(commandJson, Command.class);
		return ResponseEntity.status(HttpStatus.OK).body(commandRepository.save(command));
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
