package org.example.lokyi_300385654.web;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.lokyi_300385654.entities.Customer;
import org.example.lokyi_300385654.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@AllArgsConstructor
public class CustomerController {

    private int remainingSeats = 20;
    private static final Pattern SEAT_CODE_PATTERN = Pattern.compile("^[1-5][A-E]$");
    private CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository,
                              @Value("${remainingSeats:20}") int remainingSeats) {
        this.customerRepository = customerRepository;
        this.remainingSeats = remainingSeats;
    }

    @GetMapping("/index")
    public String showSeats(Model model) {
        List<Customer> customerList = customerRepository.findAll();
        Map<String, String> customerSeats = new HashMap<>();
        for (Customer customer : customerList) {
            customerSeats.put(customer.getSeatno(), customer.getName());
        }
        model.addAttribute("customerSeats", customerSeats);
        model.addAttribute("remainingSeats", remainingSeats);
        model.addAttribute("currentDate", java.time.LocalDate.now());
        model.addAttribute("customerList", customerList);
        return "customers";
    }

    @PostMapping("/reserve")
    public String reserveSeat(@RequestParam String seatno,
                              @RequestParam String name,
                              @RequestParam String tdate,
                              Model model) {

        // Validation checks
        if (seatno.isBlank() || name.isBlank()) {
            model.addAttribute("errorMessage", "Seat or name cannot be blank.");
        } else if (!SEAT_CODE_PATTERN.matcher(seatno).matches()) {
            model.addAttribute("errorMessage", "Please follow the seat code format");
        } else if (customerRepository.existsBySeatno(seatno)) {
            model.addAttribute("errorMessage", "This seat is already taken.");
        } else if (remainingSeats <= 0) {
            model.addAttribute("errorMessage", "No seats available.");
        } else {
            Customer customer= new Customer();
            customer.setName(name);
            customer.setSeatno(seatno);
            customer.setTdate(new Date());
            customerRepository.save(customer);
            remainingSeats--;
        }

        List<Customer> customerList = customerRepository.findAll();
        Map<String, String> customerSeats = new HashMap<>();
        for (Customer customer : customerList) {
            customerSeats.put(customer.getSeatno(), customer.getName());
        }
        model.addAttribute("customerSeats", customerSeats);
        model.addAttribute("remainingSeats", remainingSeats);
        model.addAttribute("currentDate", java.time.LocalDate.now());
        model.addAttribute("customerList", customerList);
        return "customers";
    }

    @PostMapping(path = "/save")
    public String save(Customer customer, Model model, BindingResult
            bindingResult, ModelMap mm, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "editCustomer";
        } else {
            customerRepository.save(customer);
        }
        return "redirect:/index";
    }

    @GetMapping("/editCustomers")
    public String editCustomers(Model model, Long id, HttpSession session) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) throw new RuntimeException("Customer not found");
        model.addAttribute("customer", customer);
        return "editCustomers";
    }

    @GetMapping("/delete")
    public String delete(Long id) {
        customerRepository.deleteById(id);
        return "redirect:/index";
    }



}
