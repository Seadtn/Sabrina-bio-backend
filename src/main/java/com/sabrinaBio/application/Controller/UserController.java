package com.sabrinaBio.application.Controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sabrinaBio.application.Modal.ResponseDto;
import com.sabrinaBio.application.Modal.User;
import com.sabrinaBio.application.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/authDashbord/")
@RequiredArgsConstructor
public class UserController {
	
 private final UserRepository userRepository;
BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User userData) throws IOException {
		Optional<User> optionalUser = userRepository.findByUsername(userData.getUsername());

		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Invalid username"));
		}
		
		User user = optionalUser.get();
		boolean result = bcpe.matches(userData.getPassword(), user.getPassword());
		if (!result) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Invalid credentials"));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Connected"));
	}
}
