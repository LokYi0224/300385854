package org.example.lokyi_300385654.web;

import org.example.lokyi_300385654.entities.Customer;
import org.example.lokyi_300385654.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Model model;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowSeats() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1L, "John Doe", "1A", LocalDate.now()));

        when(customerRepository.findAll()).thenReturn(customers);

        String viewName = customerController.showSeats(model);

        verify(customerRepository, times(1)).findAll();
        verify(model, times(1)).addAttribute(eq("customerSeats"), anyMap());
        verify(model, times(1)).addAttribute(eq("remainingSeats"), anyInt());
        verify(model, times(1)).addAttribute(eq("currentDate"), any(LocalDate.class));
        verify(model, times(1)).addAttribute(eq("customerList"), eq(customers));

        assertEquals("customers", viewName);
    }

    @Test
    void testReserveSeat() {
        Customer customer = new Customer(1L, "Jane Doe", "2B", LocalDate.now());

        when(customerRepository.existsBySeatno(customer.getSeatno())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        String viewName = customerController.reserveSeat(customer.getSeatno(), customer.getName(), customer.getTdate().toString(), model);

        verify(customerRepository, times(1)).existsBySeatno(customer.getSeatno());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(model, times(1)).addAttribute(eq("successMessage"), anyString());

        assertEquals("customers", viewName);
    }

    @Test
    void testDeleteCustomer() {
        Long customerId = 1L;

        doNothing().when(customerRepository).deleteById(customerId);

        customerRepository.deleteById(customerId);

        verify(customerRepository, times(1)).deleteById(customerId);
    }
}