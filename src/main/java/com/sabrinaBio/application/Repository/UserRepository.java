package com.sabrinaBio.application.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sabrinaBio.application.Modal.User;

public interface UserRepository extends JpaRepository<User,Long> {
	 Optional<User> findByUsername(String username);

}
