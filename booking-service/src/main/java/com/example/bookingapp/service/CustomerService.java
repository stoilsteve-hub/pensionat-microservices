package com.example.bookingapp.service;

import com.example.bookingapp.config.RestTemplateConfig;
import com.example.bookingapp.model.CustomerDTO;
import com.example.bookingapp.model.CustomerResponseDTO;
import com.example.bookingapp.model.Feedback;
import com.example.bookingapp.model.LoginRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {
    private final RestTemplate restTemplate;
    //    private final String API_URL = "http://localhost:8081/api/customers";
//private final String API_URL = "http://customer-service:8081/api/customers";
    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public CustomerService(RestTemplateConfig restTemplateConfig) {
        this.restTemplate = restTemplateConfig.restTemplate();
    }

    public CustomerResponseDTO loginCustomer(String email, String password) {
        try {
            LoginRequestDTO request = new LoginRequestDTO(email, password);
            CustomerDTO response = restTemplate.postForObject(customerServiceUrl + "/login", request, CustomerDTO.class);
            return (response != null) ? new CustomerResponseDTO(response, Feedback.OK) :
                    new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            return new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (HttpStatusCodeException e) {
            return new CustomerResponseDTO(getLoginFeedbackFromStatus(e.getStatusCode()));
        }
    }

    public CustomerResponseDTO signupCustomer(CustomerDTO dto) {
        System.out.println("signupCustomer is called");
        if (dto != null){
            System.out.println("dto != null");
            if (dto.getEmail() != null && dto.getEmail().isBlank()){
                System.out.println("email is: " + dto.getEmail());
            }
            else {
                System.out.println("email is null");
            }
        }
        else {
            System.out.println("dto is null");
        }
        try {
            CustomerDTO response = restTemplate.postForObject(customerServiceUrl + "/signup", dto, CustomerDTO.class);
            if (response != null){
                System.out.println("response != null");
            }
            else {
                System.out.println("response is null");
            }
            return (response != null) ? new CustomerResponseDTO(response, Feedback.OK) :
                    new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            return new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (HttpStatusCodeException e) {
            return new CustomerResponseDTO(getFeedbackFromStatus(e.getStatusCode()));
        }
    }

    public CustomerResponseDTO updateCustomer(Long customerId, CustomerDTO customerDTO){
        try {
            restTemplate.put(customerServiceUrl + "/" + customerId, customerDTO);
            return new CustomerResponseDTO(Feedback.OK);
        } catch (ResourceAccessException e) {
            return new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            return new CustomerResponseDTO(Feedback.INVALID_USER);
        }
    }

    public CustomerResponseDTO deleteCustomer(Long customerId){
        try {
            restTemplate.delete(customerServiceUrl + "/" + customerId);
            return new CustomerResponseDTO(Feedback.OK);
        } catch (ResourceAccessException e) {
            return new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            return new CustomerResponseDTO(Feedback.HAS_ACTIVE_BOOKINGS);
        }
    }

    public CustomerResponseDTO getCustomerById(Long customerId) {
        try {
            CustomerDTO dto = restTemplate.getForObject(customerServiceUrl + "/" + customerId, CustomerDTO.class);
            return (dto != null) ? new CustomerResponseDTO(dto, Feedback.OK) : new CustomerResponseDTO(Feedback.INVALID_USER);
        } catch (ResourceAccessException e) {
            return new CustomerResponseDTO(Feedback.CUSTOMER_SERVICE_UNAVAILABLE);
        } catch (HttpStatusCodeException e) {
            return new CustomerResponseDTO(getFeedbackFromStatus(e.getStatusCode()));
        }
    }

    private Feedback getFeedbackFromStatus(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> Feedback.EMPTY_EMAIL;
            case 401 -> Feedback.UNAUTHORIZED;
            case 404 -> Feedback.INVALID_EMAIL;
            case 409 -> Feedback.USER_EXISTS;
//            case 503 -> Feedback.CUSTOMER_SERVICE_UNAVAILABLE;
            default -> Feedback.CUSTOMER_SERVICE_UNAVAILABLE;
        };
    }

    private Feedback getLoginFeedbackFromStatus(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> Feedback.EMPTY_EMAIL;
            case 401 -> Feedback.INVALID_PASSWORD;
            case 404 -> Feedback.INVALID_EMAIL;
//            case 503 -> Feedback.CUSTOMER_SERVICE_UNAVAILABLE;
            default -> Feedback.CUSTOMER_SERVICE_UNAVAILABLE;
        };
    }
}

