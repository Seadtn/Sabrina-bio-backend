package com.sabrinaBio.application.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Contact;
@Repository
public interface ContactRepository  extends JpaRepository<Contact, Long>{

}
