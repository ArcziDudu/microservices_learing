package com.microservices.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class CustomerService{

    private final CustomerRepository customerRepository;
    private final WebClient webClient;
    public void registerCustomer(CustomerRegistrationRequest customerRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRequest.firstName())
                .lastName(customerRequest.firstName())
                .email(customerRequest.email())
                .build();
        customerRepository.saveAndFlush(customer);
        WebClient webClient = WebClient.create("http://localhost:8081/api/v1");

        FraudCheckResponse fraudCheckResponse = webClient
                .get()
                .uri("/fraud-check/{customerId}", customer.getId())
                .retrieve()
                .bodyToMono(FraudCheckResponse.class)
                .block();

        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }

    }
}
