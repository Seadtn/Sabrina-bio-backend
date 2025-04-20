package com.sabrinaBio.application.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sabrinaBio.application.Modal.Command;
import com.sabrinaBio.application.Modal.Status;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    long countByStatus(Status status);

    @Query("SELECT SUM(c.totalPrice) FROM Command c WHERE c.status = :status")
    double sumTotalPriceByStatus(Status status);
}
