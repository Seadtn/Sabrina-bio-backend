package com.sabrinaBio.application.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Command;
import com.sabrinaBio.application.Modal.Status;
import com.sabrinaBio.application.Modal.DTO.CommandDTO;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
	
	@Query("SELECT new com.sabrinaBio.application.Modal.DTO.CommandDTO( " +
		       "c.id, c.status, c.firstName, c.lastName, c.mail, c.phone, c.phone2, " +
		       "c.creationDate, c.confirmationDate, c.city, c.postalCode, c.paymentMethod, c.totalPrice) " +
		       "FROM Command c")
		Page<CommandDTO> findAllAsDTO(Pageable pageable);
	
    long countByStatus(Status status);

    @Query("SELECT SUM(c.totalPrice) FROM Command c WHERE c.status = :status")
    double sumTotalPriceByStatus(Status status);
}
