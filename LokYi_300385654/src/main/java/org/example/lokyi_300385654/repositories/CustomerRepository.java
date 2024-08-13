package org.example.lokyi_300385654.repositories;

import org.example.lokyi_300385654.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsBySeatno(String seatno);
}
