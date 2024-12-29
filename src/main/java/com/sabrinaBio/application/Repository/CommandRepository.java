package com.sabrinaBio.application.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Command;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {

}
