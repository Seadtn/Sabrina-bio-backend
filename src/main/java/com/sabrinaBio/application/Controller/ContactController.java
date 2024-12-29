package com.sabrinaBio.application.Controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabrinaBio.application.Modal.Contact;
import com.sabrinaBio.application.Repository.ContactRepository;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/contactManagement/")
@RequiredArgsConstructor
public class ContactController {
	
	private final ContactRepository contactRepository;

	@PostMapping("/newContact")
	public ResponseEntity<?> createNewContact(@RequestBody String contactJson) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Contact contact = objectMapper.readValue(contactJson, Contact.class);
		return ResponseEntity.status(HttpStatus.OK).body(contactRepository.save(contact));
	}
	
	@GetMapping("/getAllContacts")
	ResponseEntity<?> getAllContacts() {
		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(contactRepository.findAll()));
	}
	
	
}